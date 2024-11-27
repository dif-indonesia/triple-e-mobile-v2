package id.co.dif.base_project.presentation.dialog

import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.databinding.FragmentDialogCheckinBinding
import id.co.dif.base_project.databinding.FragmentDialogRequestPermitBinding
import id.co.dif.base_project.viewmodel.DetilTicketViewModel
import id.co.dif.base_project.viewmodel.PermitViewModel
import id.co.dif.base_project.viewmodel.TroubleTicketViewModel

class requestPermitDialog(
    var id: String? = null
) : BaseBottomSheetDialog<PermitViewModel, FragmentDialogRequestPermitBinding, Any?>() {

    override val layoutResId = R.layout.fragment_dialog_request_permit

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnYes.setOnClickListener {
            viewModel.requestPermitById(id)
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        viewModel.responseRequestPermit.observe(lifecycleOwner) {
            if (it.status == 200) {
                dismiss()
                setFragmentResult("requestPermitKey", bundleOf("isSuccess" to true))
            }
        }


    }
}