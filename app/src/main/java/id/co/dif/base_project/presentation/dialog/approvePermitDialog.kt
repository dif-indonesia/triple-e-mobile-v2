package id.co.dif.base_project.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import eightbitlab.com.blurview.RenderScriptBlur
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.databinding.FragmentDialogApprovePermitBinding
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.viewmodel.PermitViewModel
import id.co.dif.base_project.viewmodel.TroubleTicketViewModel

class approvePermitDialog (
    var id: String? = null,
    private val onActionComplete: (() -> Unit)? = null // Callback untuk refresh
): BaseBottomSheetDialog<PermitViewModel, FragmentDialogApprovePermitBinding, Any?>() {
    override val layoutResId = R.layout.fragment_dialog_approve_permit

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

        binding.btnYes.setOnClickListener {
            viewModel.approvePermit( id = id,
                mutableMapOf(
                    "permit_approved" to true, //value => true or false, true for approve, false for reject
                    "permit_information" to "" //insert value if approved is false
                )
            ).log("kondisi terima")
        }

        binding.decline.setOnClickListener {
            viewModel.approvePermit( id = id,
                mutableMapOf(
                    "permit_approved" to false, //value => true or false, true for approve, false for reject
                    "permit_information" to binding.note.text.toString().orDefault("") //insert value if approved is false
                )
            ).log("kondisi terima")
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        viewModel.responseApprovePermit.observe(viewLifecycleOwner) { response ->
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