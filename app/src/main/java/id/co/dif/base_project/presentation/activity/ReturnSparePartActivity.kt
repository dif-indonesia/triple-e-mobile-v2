package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.ActivityReturnSparePartBinding

class ReturnSparePartActivity : BaseActivity<BaseViewModel, ActivityReturnSparePartBinding>() {
    override val layoutResId = R.layout.activity_return_spare_part
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.btBack.setOnClickListener {
            onBackPressed()
        }
    }


}
