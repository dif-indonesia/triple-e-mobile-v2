package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.ActivityChangeLogBinding

class ChangeLogActivity : BaseActivity<BaseViewModel, ActivityChangeLogBinding>() {
    override val layoutResId = R.layout.activity_change_log

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rootLayout.onBackButtonClicked ={
            onBackPressedDispatcher.onBackPressed()
        }
    }

}