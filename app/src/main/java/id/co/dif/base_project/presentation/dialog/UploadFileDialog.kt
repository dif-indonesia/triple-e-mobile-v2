package id.co.dif.base_project

import android.os.Bundle
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.FragmentDialogUploadFileBinding


class DialogUploadFile(val onClickCamera:()->Unit, val onClickfile:()->Unit)  : BaseBottomSheetDialog<BaseViewModel, FragmentDialogUploadFileBinding, Any?>() {
    override val layoutResId = R.layout.fragment_dialog_upload_file

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.file.setOnClickListener {
            dialog?.dismiss()
            onClickfile()
//            dialog?.show()
        }

        binding.camera.setOnClickListener {
            dialog?.dismiss()
            onClickCamera.invoke()
        }




    }



}