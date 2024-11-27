package id.co.dif.base_project.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityForgotPasswordBinding
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.viewmodel.ForgotPasswordViewModel

class ForgotPasswordActivity : BaseActivity<ForgotPasswordViewModel, ActivityForgotPasswordBinding>() {
    override val layoutResId = R.layout.activity_forgot_password

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnSend.setOnClickListener {
            viewModel.forgotPassord(
                email = binding.email.text.toString(),
            )
        }

        viewModel.responseForgotPassword.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                binding.txtCheckYourmail.isVisible = true
                binding.yourMail.isVisible = true
                binding.btnNext.isVisible = true
                binding.btnSend.isVisible = false
                binding.email.isVisible = false
                binding.txtEmailAddress.isVisible = false
                binding.yourMail.text = it.data.email
                Toast.makeText(this, "Send email succes, check your email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

    }

}