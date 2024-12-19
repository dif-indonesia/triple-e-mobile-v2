package id.co.dif.base_project.presentation.dialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.data.TicketStatus
import id.co.dif.base_project.databinding.FragmentDialogCheckinBinding
import id.co.dif.base_project.databinding.FragmentDialogFilterBinding
import id.co.dif.base_project.presentation.activity.CameraActivity
import id.co.dif.base_project.presentation.activity.InputOtpActivity
import id.co.dif.base_project.presentation.activity.TicketPermitActivity
import id.co.dif.base_project.presentation.fragment.DetailFragment.Companion.OPEN_CAMERA_REQUEST_CODE
import id.co.dif.base_project.utils.calculateDistance
import id.co.dif.base_project.utils.colorRes
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.makeMultipartData
import id.co.dif.base_project.viewmodel.PermitViewModel
import id.co.dif.base_project.viewmodel.TroubleTicketViewModel
import java.util.Calendar

class CheckinDialog(
    var id: String? = null,
    var latitude: String? = null,
    var longtitude: String? = null,
    private val onActionComplete: (() -> Unit)? = null // Callback untuk refresh
) : BaseBottomSheetDialog<PermitViewModel, FragmentDialogCheckinBinding, Any?>() {

    override val layoutResId = R.layout.fragment_dialog_checkin

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnYes.setOnClickListener {
            val currentLat = preferences.myDetailProfile.value?.latitude?.toDouble() ?: 0.0
            val currentLon = preferences.myDetailProfile.value?.longtitude?.toDouble() ?: 0.0

            val targetLat = latitude?.toDoubleOrNull() ?: 0.0
            val targetLon = longtitude?.toDoubleOrNull() ?: 0.0

            val distance = calculateDistance(currentLat, currentLon, targetLat, targetLon)

            if (distance <= 100.0) {
                // Jarak dalam radius 100 meter, lanjutkan check-in
                viewModel.checkIn(
                    id = id,
                    in_radius = true
                )
                dismiss() // Tutup dialog setelah check-in
//            } else {
//                dismiss()
//                Toast.makeText(
//                    context,
//                    "Anda berada di luar radius (lebih dari 100 meter).",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
            } else {
                // Ambil konteks dari Activity, bukan Fragment
                val currentContext = activity ?: return@setOnClickListener

                // Tampilkan AlertDialog untuk mengirim eviden
                AlertDialog.Builder(currentContext)
                    .setMessage("Anda berada di luar radius, kirim eviden?")
                    .setPositiveButton("Yes") { _, _ ->
                        preferences.selectedTicketId.value = id
                        currentContext.startActivity(Intent(currentContext, TicketPermitActivity::class.java))
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show()

                // Tutup dialog utama setelah AlertDialog dibuat
                dismiss()
            }

        }

        binding.btnPending.setOnClickListener {
            dismiss()
        }


        binding.btnClose.setOnClickListener {
            dismiss()
        }

        viewModel.responseCheckinCheckout.observe(lifecycleOwner) {
            if (it.status == 200) {
                Toast.makeText(context, "Succusfully submit!", Toast.LENGTH_SHORT).show()
                dismiss()
                onActionComplete?.invoke()
            } else {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
