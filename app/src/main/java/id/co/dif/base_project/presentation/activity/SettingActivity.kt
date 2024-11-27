package id.co.dif.base_project.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.ActivitySettingBinding
import id.co.dif.base_project.utils.stopLocationServiceScheduler
import kotlinx.coroutines.launch

class SettingActivity : BaseActivity<BaseViewModel, ActivitySettingBinding>() {
    override val layoutResId= R.layout.activity_setting

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }
        val role = basiInfo?.emp_security ?: 0
        val projectid = basiInfo?.asgn_project_id

//        binding.areaacces.isVisible =
//            role >= 4 && projectid == "5"

        binding.areaaccesteleglobal.isVisible = role >= 4

        binding.project.isVisible = role >= 7

        binding.areaaccesteleglobal.setOnClickListener {
            startActivity(Intent(this, AreaAccesTeleglobalActivity::class.java))
        }

        binding.project.setOnClickListener {
            startActivity(Intent(this, ProjectActivity::class.java))
        }

        binding.txAccountSetting.setOnClickListener {
            startActivity(Intent(this, AccountSettingActivity::class.java))
        }
        binding.txActivity.setOnClickListener {
            startActivity(Intent(this, Activity::class.java))
        }
       binding.txChangePw.setOnClickListener {
           startActivity(Intent(this, ChangePasswordActivity::class.java))
       }
        binding.txChangeLog.setOnClickListener {
            startActivity(Intent(this, ChangeLogActivity::class.java))
        }
        binding.idLogout.setOnClickListener {
            lifecycleScope.launch {
                val response = viewModel.apiServices.postSesionLog(
                    bearerToken = "Bearer ${session?.token_access}",
                    "logout"
                )
            }
            preferences.wipe()
            stopLocationServiceScheduler()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}