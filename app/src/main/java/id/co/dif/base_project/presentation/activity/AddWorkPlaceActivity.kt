package id.co.dif.base_project.presentation.activity

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.AddWorkplaceBinding
import id.co.dif.base_project.utils.findViewsByType
import id.co.dif.base_project.utils.trimAllEditText
import id.co.dif.base_project.viewmodel.AddWorkPlaceViewModel
import java.util.*
import java.util.zip.Inflater

class AddWorkPlaceActivity : BaseActivity<AddWorkPlaceViewModel, AddWorkplaceBinding>() {
    override val layoutResId = R.layout.add_workplace

    lateinit var inflater: Inflater


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


        binding.fromdate.setOnClickListener {
            showDatePicker(binding.fromdate)
            binding.fromdate.error = null
        }

        binding.untildate.setOnClickListener {
            showDatePicker(binding.untildate)
            binding.untildate.error = null
        }

        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btSave.setOnClickListener {
            trimAllEditText()
            val validator = Validator(binding)
            validator.enableFormValidationMode();
            if (!validator.validate()) {
                Toast.makeText(this, "Complete all data", Toast.LENGTH_SHORT).show()
            }else {
                viewModel.putworkadd(
                    mutableMapOf(
                        "company" to binding.txtCompany.text.toString(),
                        "position" to binding.txtPosition.text.toString(),
                        "city" to binding.txtCitytown.text.toString(),
                        "description" to binding.txtDescription.text.toString(),
                        "time_priode_from" to binding.fromdate.text.toString(),
                        "time_priode_until" to binding.untildate.text.toString(),

                        )
                )
            }
        }

        viewModel.responseworkadd.observe(lifecycleOwner) {
            if (it.status == 200) {
                // Call the showSuccessMessage function with a context and success message
                showSuccessMessage(this, "WorkPlace created successfully!")

                onBackPressed()
//                viewModel.responseWorkList.observe(lifecycleOwner) {
//                    onBackPressed()
//                }

            }
        }


    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$year-${month + 1}-$dayOfMonth"
                val monthName = getMonthName(month)
                editText.setText("$dayOfMonth $monthName $year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun getMonthName(month: Int): String {
        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        return months[month]
    }

}