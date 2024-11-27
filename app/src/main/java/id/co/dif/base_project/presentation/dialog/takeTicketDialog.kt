package id.co.dif.base_project.presentation.dialog

import android.os.Bundle
import android.widget.Toast
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.databinding.FragmentDialogCheckinBinding
import id.co.dif.base_project.databinding.FragmentDialogTakeTicketBinding
import id.co.dif.base_project.viewmodel.PermitViewModel

class takeTicketDialog (
    var id: String? = null,
    private val onActionComplete: (() -> Unit)? = null // Callback untuk refresh
) : BaseBottomSheetDialog<PermitViewModel, FragmentDialogTakeTicketBinding, Any?>() {

    override val layoutResId = R.layout.fragment_dialog_take_ticket

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnYes.setOnClickListener {
            viewModel.takeTicket(
                id = id
            )
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        viewModel.responseTakeTicket.observe(lifecycleOwner) {
            print("${it.message} : message")
            if (it.status == 200) {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                dismiss()
                onActionComplete?.invoke()
            } else {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }


    }
}
