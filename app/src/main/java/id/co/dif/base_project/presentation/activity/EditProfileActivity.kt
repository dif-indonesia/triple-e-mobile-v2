package id.co.dif.base_project.presentation.activity

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import br.com.ilhasoft.support.validation.Validator
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import id.co.dif.base_project.MAX_IMAGE_SIZE_MEGABYTES
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityEditProfileBinding
import id.co.dif.base_project.presentation.dialog.DialogUploadProfileCover
import id.co.dif.base_project.presentation.dialog.PickerDialog
import id.co.dif.base_project.utils.AndroidDownloader
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.base64ImageToBitmap
import id.co.dif.base_project.utils.compressImage
import id.co.dif.base_project.utils.compressImageFileToDefinedSize
import id.co.dif.base_project.utils.copyImageToFile
import id.co.dif.base_project.utils.eval
import id.co.dif.base_project.utils.findViewsByType
import id.co.dif.base_project.utils.getFileFromUri
import id.co.dif.base_project.utils.getFileSizeInBytes
import id.co.dif.base_project.utils.getImageFromUri
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.makeMultipartData
import id.co.dif.base_project.utils.megaBytes
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.rotateImageCorrectly
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.viewmodel.EditProfileViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import java.io.File

class EditProfileActivity : BaseActivity<EditProfileViewModel, ActivityEditProfileBinding>() {
    override val layoutResId = R.layout.activity_edit_profile
    private lateinit var statusTextView: TextView
    private lateinit var header: ImageView
    private lateinit var img_profile: ImageView
    private lateinit var dateinfo: TextView
    private var imageCropRequestCode = -1
    private val downloader: AndroidDownloader by inject()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        var info = preferences.myDetailProfile.value

        binding.appBar.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

        setupMaritalStatus()
        setupNumberofKids()
        binding.marriedStatus.setText(info?.marital.orDefault())
        binding.numberOfKids.setText(info?.kids.toString().orDefault())
        binding.idNama.setText(info?.fullname.orDefault())
        binding.position.setText(basiInfo?.emp_position.orDefault())
        binding.aboutmyself.setText(info?.about.orDefault())
        binding.fullname.setText(info?.fullname.orDefault())
        binding.email.setText(info?.email.orDefault())
        binding.dateinfo.setText(info?.birthday.orDefault())
        if (info?.gender?.lowercase() == "female") {
            binding.rgGender.check(R.id.rb_female)
        } else {
            binding.rgGender.check(R.id.rb_male)
        }
        binding.phonenumber.setText(info?.phone.orDefault())
        binding.emailarlternative.setText(info?.alt_email.orDefault())
        binding.location.setText(info?.location.orDefault())
        binding.edtLatitude.setText(info?.latitude.orDefault())
        binding.edtLongtitude.setText(info?.longtitude)

        binding.nik.setText(info?.nik.orDefault())
        binding.npwp.setText(info?.npwp.orDefault())
        binding.bpjsnaker.setText(info?.bpjs.orDefault())
        binding.bpjskes.setText(info?.bpjs_kes.orDefault())

        binding.address.setText(info?.address.orDefault())
        binding.linkedin.setText(info?.linkedin.orDefault())
        binding.bank.setText(info?.bank.orDefault())
        binding.noRekening.setText(info?.bank_account.orDefault())
        binding.skillInstalation.setValue(info?.skill?.Installation ?: 0F)
        binding.skillCommisionning.setValue(info?.skill?.Commissioning ?: 0F)
        binding.skillIntegration.setValue(info?.skill?.Integration ?: 0F)
        binding.skillProject.setValue(info?.skill?.Project ?: 0F)
        binding.skillBusiness.setValue(info?.skill?.Business ?: 0F)

        info?.photo_profile?.let { encoded ->
            binding.imgProfile.setImageBitmap(base64ImageToBitmap(encoded))
        }
        info?.cover?.let { image ->
            Log.d(TAG, "coverrrrr: $image")
            binding.header.loadImage(image, shimmerDrawable(), skipMemoryCaching = true)
        }


        binding.fileCv.setOnClickListener {
            val url = info?.cv.toString()
            downloader.downloadFile(
                url = url,
                mimeType = "application/pdf",
                title = "File CV"
            )
        }

        info?.ktp?.let { encodeed ->
            binding.fileKtp.setImageBitmap(base64ImageToBitmap(encodeed))
        }

        binding.fileKtp.isVisible = info?.ktp != null

        binding.fileCv.isVisible = info?.cv != null
        binding.fileKtp.setOnClickListener {
            StfalconImageViewer.Builder<String>(this, arrayOf(info?.ktp)) { view, image ->
                view.setImageBitmap(base64ImageToBitmap(image))
            }.show()
        }
        statusTextView = findViewById(R.id.statusTextView)
        header = findViewById(R.id.header)
        img_profile = findViewById((R.id.img_profile))

        binding.btUploadFile.setOnClickListener {
            val radiofile = if (binding.rgFile.checkedRadioButtonId == R.id.rb_cv) "CV" else "KTP"
            if (radiofile == "CV") {
                openFilePdf("Option 1")
            } else {
                openFileImage("Option 2")
            }
        }

        binding.imgProfile.setOnClickListener {
            val dialog: DialogFragment = DialogUploadProfileCover(
                onClickProfile = {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"

                    imageCropRequestCode = 1
                    startActivityForResult(intent, 5)
                },
                onClickCover = {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    imageCropRequestCode = 2
                    startActivityForResult(intent, 5)
                }
            )


            dialog.show(supportFragmentManager, "dialog")
        }
//        setAutoFormatListener(binding.nik)
//        setAutoFormatListener(binding.npwp)
//        setAutoFormatListener(binding.bpjsnaker)
//        setAutoFormatListener(binding.bpjskes)
        binding.btSubmit.setOnClickListener {
            val validator = Validator(binding)
            validator.enableFormValidationMode();
            if (validator.validate()) {
                submitdata()
            } else {
                Toast.makeText(this, "there are fields that are still empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.responseeditProfileList.observe(lifecycleOwner) {
            if (it.status == 200) {
                viewModel.file?.let { file ->
                    val radiofile =
                        if (binding.rgFile.checkedRadioButtonId == R.id.rb_cv) "CV" else "KTP"
                    val key = (radiofile == "CV").eval("file_cv", "file_ktp")
                    val param = makeMultipartData(key, file)
                    viewModel.uploadfile(param = mutableListOf(param))
                }
                showToast("Profile Successfully Edited!")
                onBackPressedDispatcher.onBackPressed()
            } else {
                showToast("Something went wrong!")
            }
        }

        viewModel.responseeuploadfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.ERROR) {
                showToast("Failed Upload File")
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uri = data?.data

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    uri?.let {
                        viewModel.showLoading()
                        lifecycleScope.launch {
                            var file: File = copyImageToFile(uri)
                            file = rotateImageCorrectly(this@EditProfileActivity, file)
                            file = compressImageFileToDefinedSize(MAX_IMAGE_SIZE_MEGABYTES.megaBytes, file.toUri())
                            Picasso.get().load(file).into(img_profile)
                            val param = makeMultipartData("emp_photo", file)
                            viewModel.updateprofile(param)
                            viewModel.dissmissLoading()
                        }
                    }

                }

                2 -> {
                    uri?.let {
                        viewModel.showLoading()
                        lifecycleScope.launch {
                            var file: File = copyImageToFile(uri)
                            file = rotateImageCorrectly(this@EditProfileActivity, file)
                            file = compressImageFileToDefinedSize(MAX_IMAGE_SIZE_MEGABYTES.megaBytes, file.toUri())
                            Picasso.get().load(file).into(header)
                            val param = makeMultipartData("emp_cover", file)
                            viewModel.updatecover(param)
                            viewModel.dissmissLoading()
                        }
                    }
                }

                3 -> {
                    data?.let {
                        val fileUri = it.data
                        statusTextView.text = "Selected file: ${getFileNameFromUri(fileUri)}"
                        viewModel.file = fileUri?.let { getFileFromUri(this, it) }
                        val file = makeMultipartData("file_ktp", viewModel.file!!)
                        viewModel.uploadfile(param = mutableListOf(file))
                    }

                }

                4 -> {
                    data?.let {
                        val fileUri = it.data
                        statusTextView.text = "Selected file: ${getFileNameFromUri(fileUri)}"
                        viewModel.file = fileUri?.let { getFileFromUri(this, it) }
                        val param = makeMultipartData("file_cv", viewModel.file!!)
                        viewModel.uploadfile(param = mutableListOf(param))
                    }
                }

                5 -> {
                    Log.d(TAG, "onActivityResult request code: $imageCropRequestCode")
                    val intent = Intent(this, ImageCropperActivity::class.java)
                    intent.data = uri
                    if (imageCropRequestCode == 1) {
                        intent.putExtra("x_ratio", 1)
                        intent.putExtra("y_ratio", 1)
                    } else {
                        intent.putExtra("x_ratio", 1590)
                        intent.putExtra("y_ratio", 420)
                    }
                    startActivityForResult(intent, imageCropRequestCode)
                }
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri?): String {
        uri?.let {
            contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                val fileName = cursor.getString(nameIndex)
                val index = fileName.lastIndexOf('.')
                return if (index == -1) {
                    fileName
                } else {
                    fileName.substring(0, index)
                }
            }
        }
        return "Unknown file name"
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, EditProfileActivity::class.java)
        }

    }

    private fun submitdata() {
        findViewsByType(
            binding.root,
            EditText::class.java
        ).forEach { it.setText(it.text.trim()) }
        var radioVal =
            if (binding.rgGender.checkedRadioButtonId == R.id.rb_female) "Female" else "Male"
        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            radioVal = "${radio.text}"
        }
        var selectedValue = if (binding.rgTimeArea.checkedRadioButtonId == R.id.rb_wib) {
            "WIB"
        } else if (binding.rgTimeArea.checkedRadioButtonId == R.id.rb_wit) {
            "WIT"
        } else if (binding.rgTimeArea.checkedRadioButtonId == R.id.rb_wita) {
            "WITA"
        } else {
            ""
        }
        binding.rgTimeArea.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            selectedValue = "${radioButton.text}"
        }

        viewModel.handleUpdateProfile(
            mutableMapOf(
                "emp_remarks" to binding.aboutmyself.text.toString(),
                "emp_name" to binding.fullname.text.toString(),
                "emp_email" to binding.email.text.toString(),
                "emp_birthdate" to binding.dateinfo.text.toString(),
                "emp_gender" to radioVal,
                "emp_address" to binding.address.text.toString(),
                "emp_mobile" to binding.phonenumber.text.toString(),
                "emp_alt_email" to binding.emailarlternative.text.toString(),
                "emp_location" to binding.location.text.toString(),
                "emp_latitude" to binding.edtLatitude.text.toString(),
                "emp_longtitude" to binding.edtLongtitude.text.toString(),
                "emp_nik" to binding.nik.text.toString(),
                "emp_npwp" to binding.npwp.text.toString(),
                "emp_bpjs" to binding.bpjsnaker.text.toString(),
                "emp_bpjs_kes" to binding.bpjskes.text.toString(),
                "emp_linkedin" to binding.linkedin.text.toString(),
                "emp_bank" to binding.bank.text.toString(),
                "emp_bankaccount" to binding.noRekening.text.toString(),
                "emp_marital" to binding.marriedStatus.text.toString(),
                "emp_kids" to binding.numberOfKids.text.toString(),
                "emp_time_area" to selectedValue,
                "emp_skill" to arrayListOf(
                    binding.skillInstalation.value.toInt().toString(),
                    binding.skillCommisionning.value.toInt().toString(),
                    binding.skillIntegration.value.toInt().toString(),
                    binding.skillProject.value.toInt().toString(),
                    binding.skillBusiness.value.toInt().toString(),
                )
            )
        )
    }

    private fun setupNumberofKids() {
        binding.numberOfKids.setOnClickListener {
            PickerDialog.newInstance(
                arrayListOf(
                    "0",
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9"
                )
            ) { index, value ->
                binding.numberOfKids.setText(value)
                binding.numberOfKids.error = null
            }.show(supportFragmentManager, getString(R.string.number_of_kids))
        }
    }

    private fun setupMaritalStatus() {
        binding.marriedStatus.setOnClickListener {
            PickerDialog.newInstance(
                arrayListOf(
                    "Married",
                    "Single",
                )
            ) { index, value ->
                binding.marriedStatus.setText(value)
                binding.marriedStatus.error = null
            }.show(supportFragmentManager, getString(R.string.marital_status))
        }
    }

    private fun openFileImage(filePath: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 3)
    }

    private fun openFilePdf(filePath: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent, 4)
    }

    fun getFileNameFromUri(url: String?): String? {
        val path = Uri.parse(url).path
        return path?.substringAfterLast("/")
    }


}

