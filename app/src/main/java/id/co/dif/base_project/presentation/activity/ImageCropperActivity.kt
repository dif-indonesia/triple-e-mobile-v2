package id.co.dif.base_project.presentation.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.ActivityImageCropperBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageCropperActivity : BaseActivity<BaseViewModel, ActivityImageCropperBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_image_cropper

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val imageUri = intent.data
        binding.cropImageView.setImageUriAsync(imageUri)
        binding.appBar.onActionButtonClicked = {
            onRotateClick()
        }
        binding.tvFinaliseCropping.setOnClickListener {
            finaliseCropping()
        }
        val xRatio = intent.getIntExtra("x_ratio", 1)
        val yRatio = intent.getIntExtra("y_ratio", 1)
        binding.cropImageView.setAspectRatio(xRatio, yRatio)
    }

    private fun finaliseCropping() {
        viewModel.showLoading()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val bitmap = binding.cropImageView.getCroppedImage()
                val cacheDir = File(cacheDir, "image")
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }
                val fileName = "cropped-image.jpg"
                val cacheFile = File(cacheDir, fileName)
                val outputStream = FileOutputStream(cacheFile)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                val intent = Intent()
                intent.data = cacheFile.toUri()
                viewModel.dissmissLoading()
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun onRotateClick() {
        binding.cropImageView.rotateImage(90)
    }
}