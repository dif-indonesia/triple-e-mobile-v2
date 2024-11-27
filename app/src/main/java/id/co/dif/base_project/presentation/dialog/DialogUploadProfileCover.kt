package id.co.dif.base_project.presentation.dialog

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.databinding.FragmentDialogUploadProfileCoverBinding
import id.co.dif.base_project.viewmodel.EditProfileViewModel

class DialogUploadProfileCover(val onClickProfile:()->Unit, val onClickCover:()->Unit) : BaseBottomSheetDialog<EditProfileViewModel, FragmentDialogUploadProfileCoverBinding, Any?>() {
    override val layoutResId = R.layout.fragment_dialog_upload_profile_cover

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnProfile.setOnClickListener {
            dialog?.dismiss()
            onClickProfile()
        }

        binding.btnCover.setOnClickListener {
            dialog?.dismiss()
            onClickCover.invoke()
        }


    }

}