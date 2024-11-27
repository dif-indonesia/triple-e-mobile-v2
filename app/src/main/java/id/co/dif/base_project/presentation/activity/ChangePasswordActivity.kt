package id.co.dif.base_project.presentation.activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityChangePasswordBinding
import id.co.dif.base_project.viewmodel.ChangePasswordViewModel

class ChangePasswordActivity : BaseActivity<ChangePasswordViewModel, ActivityChangePasswordBinding>() {
    override val layoutResId = R.layout.activity_change_password
    private var isPasswordVisible = false

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.showpasswordOld.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            showpasswordOld()
        }

        binding.showpasswordNew.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            showpasswordNew()
        }

        binding.showpasswordConfirmPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            showpasswordConfirm()
        }

        binding.save.setOnClickListener {
            val validator = Validator(binding)
            validator.enableFormValidationMode();
            if (validator.validate()){
                viewModel.changepassword(
                    mutableMapOf(
                        "old_password" to binding.oldPassword.text.toString(),
                        "new_password" to binding.newPassword.text.toString(),
                        "confirm_new_password" to binding.retypePassword.text.toString()
                    )
                )
            }
        }

        viewModel.responseChangePassword.observe(lifecycleOwner) {
            Log.d(TAG, "onViewBindingCredfdfated: $it")
            if (it.status == 200) {
                onBackPressed()
                showSuccessMessage(this, "Changes Password successfully!")

            } else {
                showToast(it.message)
            }
        }
    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showpasswordOld() {
        if (isPasswordVisible) {
            binding.oldPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding.oldPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.oldPassword.setSelection(binding.oldPassword.length())
    }

    private fun showpasswordNew() {
        if (isPasswordVisible) {
            binding.newPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding.newPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.newPassword.setSelection(binding.newPassword.length())
    }

    private fun showpasswordConfirm() {
        if (isPasswordVisible) {
            binding.retypePassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding.retypePassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.retypePassword.setSelection(binding.retypePassword.length())
    }

}