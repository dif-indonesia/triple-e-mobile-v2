package id.co.dif.base_project.presentation.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.*
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityRegisterBinding
import id.co.dif.base_project.utils.valueIsEmpty
import id.co.dif.base_project.viewmodel.RegisterViewModel
import java.util.*


class RegisterActivity : BaseActivity<RegisterViewModel, ActivityRegisterBinding>() {

    override val layoutResId = R.layout.activity_register
    private lateinit var statusdate: TextView

    private var isPasswordVisible = false

    private var isPasswordEnable = false


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        statusdate = findViewById(R.id.statusdate)
        val calendar = android.icu.util.Calendar.getInstance()
        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            statusdate.text = "$selectedDate"
        }, calendar.get(android.icu.util.Calendar.YEAR), calendar.get(android.icu.util.Calendar.MONTH), calendar.get(
            android.icu.util.Calendar.DAY_OF_MONTH))
        binding.statusdate.setOnClickListener {
            datePicker.show()
            binding.statusdate.error = null
        }

        binding.showpassword.setOnClickListener {
            isPasswordEnable = !isPasswordEnable
            showpasswordVisibility()
        }

        binding.showreypepass.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            showretypepasswordVisibility()
        }

        // Deklaras ID field
        val txtFullname = binding.fullname
        val txtemail = binding.email
        val txtmobile = binding.mobile
        val txtstatusdate = binding.statusdate
        val txtpassword = binding.password
        val txtretypepassword = binding.retypepassword

        binding.btRegister.setOnClickListener {
            val fullnameValue = txtFullname.text.toString()
            val emailValue = txtemail.text.toString()
            val mobileValue = txtmobile.text.toString()
            val statusdateValue = txtstatusdate.text.toString()
            val passwordValue = txtpassword.text.toString()
            val retypepasswordValue = txtretypepassword.text.toString()

            if (TextUtils.isEmpty(fullnameValue)){
                txtFullname.error = "Fullname is required"
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()){
                txtemail.error = "Please enter a valid email address (@gmail.com)"
            }
            else if (binding.mobile.valueIsEmpty()){
                txtmobile.error = "Mobile is required"
            }
            else if (binding.statusdate.valueIsEmpty()){
                txtstatusdate.error = "Birthdate is required"
            }
            else if (binding.password.valueIsEmpty()){
                txtpassword.error = "Password is required"
            }

            else if (binding.retypepassword.valueIsEmpty()){
                txtretypepassword.error = "Password is required"
            }

            else if (passwordValue.length < 6 || !passwordValue.matches(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+.*".toRegex())){
                txtpassword.error = "Please enter a valid password (at least 6 character and 1 special charakter"
            }
            else if (passwordValue != retypepasswordValue){
                txtretypepassword.error = "Password do not match"
            }
            else{
                viewModel.postRegister(
                    mutableMapOf(
                        "emp_name" to binding.fullname.text.toString(),
                        "emp_password" to binding.password.text.toString(),
                        "emp_email" to binding.email.text.toString(),
                        "emp_mobile" to binding.mobile.text.toString(),
                        "emp_birthdate" to binding.statusdate.text.toString(),
                        )
                )
            }
        }


        viewModel.responseRegister.observe(lifecycleOwner) {
            if (it.status == 200) {
                Toast.makeText(this, "Succesfully Register", Toast.LENGTH_SHORT).show()
                showSuccessMessage(this, "Register successfully!")
                startActivity(Intent(this, OnBoardingActivity::class.java))
            }
        }

        viewModel.responseRegister.observe(lifecycleOwner) {
            if (it.status == 400) {
                showSuccessMessage(this, "Credentials Not Found!")

            }
        }

        binding.idLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showpasswordVisibility() {
        if (isPasswordEnable) {
            binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.password.setSelection(binding.password.length())
    }

    private fun showretypepasswordVisibility() {
        if (isPasswordVisible) {
            binding.retypepassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding.retypepassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        binding.retypepassword.setSelection(binding.retypepassword.length())
    }
}

