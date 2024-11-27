package id.co.dif.base_project.presentation.dialog

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.databinding.FragmentDialogCheckinBinding
import id.co.dif.base_project.databinding.FragmentDialogInformationDeclineBinding
import id.co.dif.base_project.viewmodel.PermitViewModel

class declineInformationDialog (
    var information_text: String? = null,
) : BaseBottomSheetDialog<PermitViewModel, FragmentDialogInformationDeclineBinding, Any?>() {

    override val layoutResId = R.layout.fragment_dialog_information_decline

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.textInformation.setText(information_text)


    }
    }