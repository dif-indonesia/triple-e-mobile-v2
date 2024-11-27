package id.co.dif.base_project.presentation.activity

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.isVisible
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityAddCollageBinding
import id.co.dif.base_project.utils.findViewsByType
import id.co.dif.base_project.utils.trimAllEditText
import id.co.dif.base_project.viewmodel.AddCollageViewModel
import java.util.Calendar

class AddCollageActivity : BaseActivity<AddCollageViewModel, ActivityAddCollageBinding>() {
    override val layoutResId = R.layout.activity_add_collage
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.fromdate.setOnClickListener {
            showDatePicker(binding.fromdate)
            binding.fromdate.error = null
        }

        binding.untildate.setOnClickListener {
            showDatePicker(binding.untildate)
            binding.untildate.error = null
        }
        var radioVal =
            if (binding.attended.checkedRadioButtonId == R.id.rb_college) getString(R.string.college) else getString(
                R.string.graduate_school
            )
        binding.attended.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                radioVal = "${radio.text}"
            })



        binding.btSave.setOnClickListener {
            trimAllEditText()
            val validator = Validator(binding)
            validator.enableFormValidationMode();
            if (!validator.validate()) {
                Toast.makeText(this, "Complete all data", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addeducation(
                    mutableMapOf(
                        "school" to binding.txtSchool.text.toString(),
                        "time_priode_from" to binding.fromdate.text.toString(),
                        "time_priode_until" to binding.untildate.text.toString(),
                        "graduated" to if (binding.graduate.isChecked) "graduate" else "not graduated yet",
                        "description" to binding.txtDescription.text.toString(),
                        "connection1" to binding.edtConnection1.text.toString(),
                        "connection2" to binding.edtConnection2.text.toString(),
                        "connection3" to binding.edtConnection3.text.toString(),
                        "attended_for" to radioVal
                    )
                )
            }
        }

        viewModel.responseaddcollage.observe(lifecycleOwner) {
            if (it.status == 200) {
                onBackPressed()
                showSuccessMessage(this, "Collage created successfully!")

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
    private fun isRadioButtonSelected(): Boolean {
        // Check if any of the radio buttons are selected
        return binding.rbCollege.isChecked || binding.rbGraduateSchool.isChecked
    }

}