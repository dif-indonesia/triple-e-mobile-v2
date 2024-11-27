package id.co.dif.base_project.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.ActivityAddTicketBelitungBinding
import id.co.dif.base_project.databinding.ActivityPermitTicketBinding
import id.co.dif.base_project.utils.makeMultipartData
import id.co.dif.base_project.viewmodel.PermitViewModel
import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toFile
import androidx.core.net.toUri
import id.co.dif.base_project.MAX_IMAGE_SIZE_MEGABYTES
import id.co.dif.base_project.utils.compressImageFileToDefinedSize
import id.co.dif.base_project.utils.copyImageToFile
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.megaBytes
import okhttp3.MultipartBody
import java.io.File


class TicketPermitActivity: BaseActivity<PermitViewModel, ActivityPermitTicketBinding> () {
    override val layoutResId = R.layout.activity_permit_ticket

    private var capturedImageUri: Uri? = null
    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { fileUri ->
                    var uri = fileUri
                    viewModel.file = uri.toFile()
                }
                capturedImageUri = result.data?.data
                if (capturedImageUri != null) {
                    try {
                        val imageView = binding.layout1.findViewById<ImageView>(R.id.imageResult)
                        val inputStream = contentResolver.openInputStream(capturedImageUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()
                        imageView.setImageBitmap(bitmap)
                        showToast("Gambar berhasil diambil!")
                    } catch (e: Exception) {
                        Log.e("ImageError", "Error decoding image: ${e.localizedMessage}")
                        showToast("Gagal memuat gambar.")
                    }
                } else {
                    showToast("Gambar tidak ditemukan.")
                }
            } else {
                showToast("Pengambilan gambar dibatalkan.")
            }
        }


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.buttonCam.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) // Menambahkan flag untuk nonaktifkan animasi
            imageResultLauncher.launch(intent)
            overridePendingTransition(0, 0) // Nonaktifkan animasi transisi
        }

        binding.buttonSubmit.setOnClickListener {
            val params = hashMapOf<String, Any?>(
                "checkin_eviden" to viewModel.file,
                "checkin_information" to binding.information.text.toString()
            )
            println("viewModel File $viewModel")
            viewModel.checkInEviden(
                id = preferences.selectedTicketId.value,
                in_radius = false,
                param =  params.map { makeMultipartData(it.key, it.value) }.toMutableList()
            )
        }

        viewModel.responseCheckinCheckout.observe(lifecycleOwner) {
            if (it.status == 200) {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                onBackPressed()
            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }


    }
}