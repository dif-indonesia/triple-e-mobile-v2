package id.co.dif.base_project.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import eightbitlab.com.blurview.RenderScriptBlur
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.databinding.FragmentDialogApproveCheckinBinding
import id.co.dif.base_project.databinding.FragmentDialogApprovePermitBinding
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.viewmodel.PermitViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class approveCheckinDialog (
    var id: Int? = null,
    var nama: String? = null,
    var date: String? = null,
    var image: String? = null,
    var checkinInformation: String? = null,
    private val onActionComplete: (() -> Unit)? = null // Callback untuk refresh
): BaseBottomSheetDialog<PermitViewModel, FragmentDialogApproveCheckinBinding, Any?>() {
    override val layoutResId = R.layout.fragment_dialog_approve_checkin

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet = findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
                bottomSheet.setBackgroundResource(android.R.color.transparent)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                window?.setDimAmount(0f)
            }
        }
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val radius = 10f
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        val decorView = currentActivity.window.decorView
        val rootView = decorView?.findViewById(android.R.id.content) as ViewGroup?
        val windowBackground = decorView?.background

        rootView?.let {
            binding.cardPopUp.setupWith(it, RenderScriptBlur(requireContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true)
        }

        binding.name.text = nama
        binding.checkinInformation.text = checkinInformation
        binding.idUploadImage.loadImage(image, shimmerDrawable())
        val inputDate = date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale("id", "ID"))
        try {
            // Parsing tanggal dari string input
            val date = inputFormat.parse(inputDate)
            // Format ulang tanggal menjadi string yang diinginkan
            val formattedDate = date?.let { outputFormat.format(it) }
            // Set teks pada binding.date
            binding.date.text = formattedDate
        } catch (e: Exception) {
            e.printStackTrace()
            binding.date.text = "Tanggal tidak valid"
        }

        binding.idUploadImage.setOnClickListener {
            StfalconImageViewer.Builder<String>(context, arrayOf(image)) { view, image ->
                Picasso.get().load(image).into(view)
            }.show()
        }

        binding.btnYes.setOnClickListener {
            viewModel.approveCheckin( id = id,
                mutableMapOf(
                    "checkin_approved" to true, //value => true or false, true for approve, false for reject
                    "checkin_information" to  binding.note.text.toString().orDefault("") //insert value if approved is false
                )
            ).log("kondisi terima")
        }

        binding.decline.setOnClickListener {
            viewModel.approveCheckin( id = id,
                mutableMapOf(
                    "checkin_approved" to false, //value => true or false, true for approve, false for reject
                    "checkin_information" to binding.note.text.toString().orDefault("") //insert value if approved is false
                )
            ).log("kondisi terima")
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        viewModel.responseCheckinCheckout.observe(viewLifecycleOwner) { response ->
            if (response.status == 200) {
                Toast.makeText(context, "Succusfully submit!", Toast.LENGTH_SHORT).show()
                dismiss()
                onActionComplete?.invoke()
            } else {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }


    }
}