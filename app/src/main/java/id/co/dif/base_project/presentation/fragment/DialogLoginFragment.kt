package id.co.dif.base_project.presentation.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import br.com.ilhasoft.support.validation.Validator
import eightbitlab.com.blurview.RenderScriptBlur
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.databinding.FragmentDialogLoginBinding
import id.co.dif.base_project.presentation.activity.ForgotPasswordActivity
import id.co.dif.base_project.presentation.activity.InputOtpActivity
import id.co.dif.base_project.utils.SharedPreferenceManager
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.viewmodel.LoginViewModel

class DialogLoginFragment() :
    BaseBottomSheetDialog<LoginViewModel, FragmentDialogLoginBinding, Any?>() {
    override val layoutResId = R.layout.fragment_dialog_login
    private var isPasswordVisible = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet = findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
                bottomSheet.setBackgroundResource(android.R.color.transparent)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                window?.setDimAmount(0f)
            }
        }
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val radius = 10f
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        val decorView = currentActivity.window.decorView
        val rootView = decorView?.findViewById(android.R.id.content) as ViewGroup?
        val windowBackground = decorView?.background

        rootView?.let {
            binding.cardLogin.setupWith(it, RenderScriptBlur(requireContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true)
        }

        binding.showpassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            showpasswordVisibility()
        }

        binding.btnHide.setOnClickListener {
            dismiss()
        }

        binding.idForgotPw.setOnClickListener {
            startActivity(Intent(requireContext(), ForgotPasswordActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val validator = Validator(binding)
            validator.enableFormValidationMode();
            if (validator.validate()) {
                viewModel.postLogin(
                    email = binding.edtEmail.text.toString(),
                    password = binding.edtPassword.text.toString()
                )
            }
        }

        viewModel.responseLogin.observe(viewLifecycleOwner) {
            if (it.status == 200) {
                preferences.rememberMe.value = binding.rememberme.isChecked
                preferences.loginData.value = it.data
                binding.progressbar.isVisible = true
                startActivity(Intent(requireContext(), InputOtpActivity::class.java))
            } else {
                it.data?.let { loginData ->
                    loginData.emailMessage?.let { message ->
                        binding.edtEmail.error = message
                    }
                    loginData.passwordMessage?.let { message ->
                        binding.edtPassword.error = message
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.progressbar.isVisible = false
    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    //   Show hide Password
    private fun showpasswordVisibility() {
        if (isPasswordVisible) {
            binding.edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.edtPassword.setSelection(binding.edtPassword.length())
    }


}