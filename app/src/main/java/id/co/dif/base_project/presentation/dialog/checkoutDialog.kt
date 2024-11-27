package id.co.dif.base_project.presentation.dialog

import android.os.Bundle
import android.widget.Toast
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.databinding.FragmentDialogCheckinBinding
import id.co.dif.base_project.databinding.FragmentDialogCheckoutBinding
import id.co.dif.base_project.viewmodel.PermitViewModel
import id.co.dif.base_project.viewmodel.TroubleTicketViewModel

class CheckoutDialog(
    var id: String? = null,
    private val onActionComplete: (() -> Unit)? = null // Callback untuk refresh
) : BaseBottomSheetDialog<PermitViewModel, FragmentDialogCheckoutBinding, Any?>() {

    override val layoutResId = R.layout.fragment_dialog_checkout

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnYes.setOnClickListener {
            viewModel.checkOut(
                id = id
            )
        }

        binding.btnNo.setOnClickListener {
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