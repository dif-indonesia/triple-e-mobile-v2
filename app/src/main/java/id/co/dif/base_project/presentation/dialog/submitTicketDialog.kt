package id.co.dif.base_project.presentation.dialog

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.databinding.FragmentDialogCheckinBinding
import id.co.dif.base_project.databinding.FragmentDialogSubmitTicketBinding
import id.co.dif.base_project.presentation.activity.TicketPermitActivity
import id.co.dif.base_project.viewmodel.PermitViewModel

class submitTicketDialog (
    var id: String? = null,
    private val onActionComplete: (() -> Unit)? = null // Callback untuk refresh
) : BaseBottomSheetDialog<PermitViewModel, FragmentDialogSubmitTicketBinding, Any?>() {

    override val layoutResId = R.layout.fragment_dialog_submit_ticket

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnYes.setOnClickListener {
            viewModel.submitTicket(
                id = id
            )
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        viewModel.responseSubmitTicket.observe(lifecycleOwner) {
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
