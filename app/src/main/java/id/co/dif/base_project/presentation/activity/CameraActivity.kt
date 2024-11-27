package id.co.dif.base_project.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.common.util.concurrent.ListenableFuture
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.LoginData
import id.co.dif.base_project.databinding.ActivityCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import java.io.File
import java.io.FileOutputStream

class CameraActivity : BaseActivity<BaseViewModel, ActivityCameraBinding>(), KoinComponent {
    enum class CameraSelectorEnum {
        FRONT,
        BACK
    }

    private var aspectRatio: Int = AspectRatio.RATIO_4_3
    private var selectedCamera: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    override val layoutResId: Int
        get() = R.layout.activity_camera

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        getPermission(
            android.Manifest.permission.CAMERA,
            onDenied = {
                showAlert(
                    context = this,
                    message = "Cannot start the camera!",
                    value = "Missing permission",
                    buttonPrimaryText = "Back",
                    onButtonPrimaryClicked = { onBackPressedDispatcher.onBackPressed() },
                    cancelable = false
                )
            },
            onGranted = {
                startCamera()
            })


        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.shutterBtn.setOnClickListener {
            try {
                takePicture()
            }catch (e: Exception){
                showToast("Something went wrong. Cannot take picture!")
            }
        }

        binding.shutterBtn.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.anim_shutter_button_action_down)
                    )

                    false
                }

                MotionEvent.ACTION_UP -> {
                    view.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.anim_shutter_button_action_up)
                    )
                    false
                }

                else -> false
            }
        }


        binding.ratio.setOnClickListener {
            when (aspectRatio) {
                AspectRatio.RATIO_4_3 -> {
                    aspectRatio = AspectRatio.RATIO_16_9
                    binding.ratio.text = getString(R.string._16_9)
                }

                AspectRatio.RATIO_16_9 -> {
                    aspectRatio = AspectRatio.RATIO_4_3
                    binding.ratio.text = getString(R.string._4_3)
                }
            }
            startCamera(selectedCamera, aspectRatio)
        }
        binding.viewFlipCamera.setOnClickListener {
            selectedCamera = when (selectedCamera) {
                CameraSelector.DEFAULT_BACK_CAMERA -> {
                    binding.viewFlipCamera.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.anim_flip_camera_cc_rotate)
                    )
                    CameraSelector.DEFAULT_FRONT_CAMERA

                }

                CameraSelector.DEFAULT_FRONT_CAMERA -> {
                    binding.viewFlipCamera.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.anim_flip_camera_ccw_rotate)
                    )
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                else -> {
                    Toast.makeText(this, "Selected camera cannot be used!", Toast.LENGTH_SHORT)
                        .show()
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            }
            startCamera(selectedCamera)
        }

        initLoadingIndicator()
    }

    private fun initLoadingIndicator() = with(binding) {
        val circularProgressDrawable = CircularProgressDrawable(applicationContext)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 100f
        circularProgressDrawable.setColorSchemeColors(Color.WHITE, Color.WHITE, Color.WHITE)
        circularProgressDrawable.start()
        loadingIndicatorIcon.background = circularProgressDrawable
    }

    private fun attachTorchControl(camera: Camera) {
        binding.torchBtn.setOnCheckedChangeListener { _, isChecked ->
            camera.cameraControl.enableTorch(isChecked)
        }
        if (!camera.cameraInfo.hasFlashUnit()) {
            binding.torchBtn.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.anim_scale_down_dissapear)
            )
        } else {
            binding.torchBtn.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.anim_scale_up_appear)
            )

        }
    }

    private fun startCamera(
        selectedCamera: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
        aspectRatio: Int = AspectRatio.RATIO_4_3
    ) = with(binding) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(applicationContext)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = buildImagePreviewUseCase(aspectRatio)
                .also {
                    it.setSurfaceProvider(livePreview.surfaceProvider)
                }
            val cameraSelector = selectedCamera

            imageCapture = buildImageCaptureUseCase(aspectRatio)
            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this@CameraActivity, cameraSelector, preview, imageCapture
                )
                attachTorchControl(camera)
            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(applicationContext))
    }

    private fun setLoading(status: Boolean) = with(binding) {
        if (status) viewModel.showLoading()
        else viewModel.dissmissLoading()
    }

    private fun takePicture() {
        // Get a stable reference of the modifiable image capture use case
        setLoading(true)
        val outputFile =
            File(cacheDir, "Image_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    setLoading(false)
                    val msg = "Photo capture failed"
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                }

                @RequiresApi(Build.VERSION_CODES.N)
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Log.d("TAG", msg)
                    viewModel.viewModelScope.launch {
                        rotateImageCorrectly(outputFile.absoluteFile)
                        setLoading(false)
                        val intent = Intent()
                        intent.data = outputFile.toUri()
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun rotateImageCorrectly(photoFile: File) = withContext(Dispatchers.IO) {
        val sourceBitmap =
            MediaStore.Images.Media.getBitmap(
                applicationContext?.contentResolver,
                photoFile.toUri()
            )
        val exif = ExifInterface(photoFile.inputStream())
        val rotation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val rotationInDegrees = when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            ExifInterface.ORIENTATION_TRANSVERSE -> -90
            ExifInterface.ORIENTATION_TRANSPOSE -> -270
            else -> 0
        }
        val matrix = Matrix().apply {
            if (rotation != 0) preRotate(rotationInDegrees.toFloat())
        }
        val rotatedBitmap = Bitmap.createBitmap(
            sourceBitmap, 0, 0, sourceBitmap.width, sourceBitmap.height, matrix, true
        )

        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(photoFile))

        sourceBitmap.recycle()
        rotatedBitmap.recycle()
    }

    private fun buildImagePreviewUseCase(aspectRatio: Int): Preview {
        return Preview.Builder()
            .setTargetAspectRatio(aspectRatio)
            .build()
    }

    private fun buildImageCaptureUseCase(aspectRatio: Int): ImageCapture {
        return ImageCapture.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

}