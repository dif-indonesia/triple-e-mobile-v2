package id.co.dif.base_project.presentation.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityLoginBinding
import id.co.dif.base_project.viewmodel.LoginViewModel

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {


    var isAllFieldsChecked = false
    override val layoutResId = R.layout.activity_login

    private lateinit var etpassword: EditText
//    private lateinit var showpassword: Button
    private  var isPasswordVisible = false
    private lateinit var login: Button
    private lateinit var rememberme: CheckBox


//    val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
//    val editor = sharedPreferences.edit()



    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
//     Show hide password
        etpassword = findViewById(R.id.etpassword)
//        showpassword = findViewById(R.id.showpassword)
        login = findViewById(R.id.login)
        rememberme = findViewById(R.id.rememberme)

        binding.showpassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            showpasswordVisibility()
        }


        binding.idSignUp.setOnClickListener {
           startActivity(Intent(this, RegisterActivity::class.java))
       }
        binding.login.setOnClickListener {
            val validator = Validator(binding)
            validator.enableFormValidationMode();
            if(validator.validate()){
                viewModel.postLogin(
                    email = binding.etmail.text.toString(),
                    password = binding.etpassword.text.toString()
                )
            }
        }

        viewModel.responseLogin.observe(lifecycleOwner) {
            if (it.status == 200) {
                preferences.rememberMe.value = binding.rememberme.isChecked
                preferences.loginData.value = it.data
                startActivity(Intent(this, InputOtpActivity::class.java))
            } else {
                it.data?.let { loginData ->
                    loginData.emailMessage?.let { message ->
                        binding.etmail.error = message
                    }
                    loginData.passwordMessage?.let { message ->
                        binding.etpassword.error = message
                    }
                }
            }
        }

        viewModel.responseLogin.observe(lifecycleOwner) {
            if (it.status == 400) {
                showSuccessMessage(this, "Credentials Not Found!")

            }
        }

        viewModel.responseLogin.observe(lifecycleOwner) {
            if (it.status == 500) {
                showSuccessMessage(this, "Server Error!")
            }
        }




//        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
//        val username = sharedPreferences.getString("username", "")
//        val password = sharedPreferences.getString("password", "")
//        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)
//
//        if (rememberMe) {
//            binding.etmail.setText(username)
//            binding.etpassword.setText(password)
//            rememberme.isChecked = true
//        }
//
//        if (rememberme.isChecked) {
//            editor.putString("username", binding.etmail.text.toString())
//            editor.putString("password", binding.etpassword.text.toString())
//            editor.putBoolean("rememberMe", true)
//        } else {
//            editor.clear()
//        }
//        editor.apply()




    }

//  Login

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                // Keluar dari activity
                finishAffinity()
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }


 //   Show hide Password
    private fun showpasswordVisibility() {
        if (isPasswordVisible) {
            // Tampilkan teks password
            etpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            showpassword.text = ""
        } else {
            // Sembunyikan teks password
            etpassword.transformationMethod = PasswordTransformationMethod.getInstance()
//            showpassword.text = "Show"
        }
        // Set selection ke akhir teks
        etpassword.setSelection(etpassword.length())
    }

}