package id.co.dif.base_project.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.ActivityGetOtpactivityBinding

class GetOTPActivity : BaseActivity<BaseViewModel, ActivityGetOtpactivityBinding>() {
    override val layoutResId = R.layout.activity_get_otpactivity


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


        val id_spinner = findViewById<Spinner>(R.id.id_spinner)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.country_codes,
            android.R.layout.simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        id_spinner.adapter = adapter

        id_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // panggil method atau lakukan tindakan lainnya ketika pilihan spinner berubah
                val selectedCountryCode = parent?.getItemAtPosition(position).toString()
                Toast.makeText(applicationContext, "Selected country code: $selectedCountryCode", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // tidak ada tindakan yang dilakukan ketika tidak ada pilihan yang dipilih
            }
        }


        binding.idGetcode.setOnClickListener {
            startActivity(Intent(this, InputOtpActivity::class.java))
        }




    }
}