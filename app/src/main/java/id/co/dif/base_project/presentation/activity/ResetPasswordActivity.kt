package id.co.dif.base_project.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityResetPasswordBinding
import id.co.dif.base_project.viewmodel.ForgotPasswordViewModel

class ResetPasswordActivity :BaseActivity<ForgotPasswordViewModel, ActivityResetPasswordBinding>() {
    override val layoutResId = R.layout.activity_reset_password

    private var isPasswordVisible = false

    private var isPasswordEnable = false
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        if (Intent.ACTION_VIEW == intent.action) {
            val data: Uri? = intent.data
            if (data != null) {
                val scheme = data.scheme // e.g., "your_scheme"
                val host = data.host// e.g., "your_host"


            }
        }

        binding.icNewPass.setOnClickListener {
            isPasswordEnable = !isPasswordEnable
            showpasswordVisibility()
        }

        binding.icConfirmPass.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            showretypepasswordVisibility()
        }



        binding.btnReset.setOnClickListener {
            viewModel.resetPassword(
                newPassword = binding.newPassword.text.toString(),
                confirmPassword = binding.confirmPassword.text.toString(),
                code = binding.code.text.toString()
            )
        }

        viewModel.responseResetPassowrd.observe(lifecycleOwner) {
            if (it.status == 200) {
                startActivity(Intent(this, OnBoardingActivity::class.java))
                Toast.makeText(this, "your password has ben changes", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showpasswordVisibility() {
        if (isPasswordEnable) {
            binding.newPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding.newPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.newPassword.setSelection(binding.newPassword.length())
    }

    private fun showretypepasswordVisibility() {
        if (isPasswordVisible) {
            binding.confirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding.confirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.confirmPassword.setSelection(binding.confirmPassword.length())
    }

}