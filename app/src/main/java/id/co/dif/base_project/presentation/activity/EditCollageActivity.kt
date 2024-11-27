package id.co.dif.base_project.presentation.activity

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityAddCollageBinding
import id.co.dif.base_project.databinding.ActivityEditCollageBinding
import id.co.dif.base_project.utils.findViewsByType
import id.co.dif.base_project.utils.trimAllEditText
import id.co.dif.base_project.viewmodel.EditCollageViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.Inflater

class EditCollageActivity: BaseActivity<EditCollageViewModel, ActivityEditCollageBinding>() {
    override val layoutResId =  R.layout.activity_edit_collage

    lateinit var inflater: Inflater



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

//        val rbCollege = findViewById<RadioButton>(R.id.rb_college)
//        val rbGraduateschool = findViewById<RadioButton>(R.id.rb_graduate_school)




        var education = preferences.userEducation.value
//        binding.title.setText("Edit Collage")

        binding.txtSchool.setText("${education?.school}")
        binding.fromdate.setText("${education?.time_priode_from}")
        binding.untildate.setText("${education?.time_priode_until}")
        binding.txtDescription.setText("${education?.description}")
        binding.graduate.setText("${education?.graduate}")
        if (binding.graduate.isChecked){
            education?.graduate == "graduate"
        } else {
            education?.graduate == ""
        }
        binding.txtDescription.setText("${education?.description}")
        binding.edtConnection1.setText("${education?.connection1}")
        binding.edtConnection2.setText("${education?.connection2}")
        binding.edtConnection3.setText("${education?.connection3}")
        if(education?.attended_for == "Graduate School") {
            binding.attended.check(R.id.rb_graduate_school)
        } else {
            binding.attended.check(R.id.rb_college)
        }
//        binding.attended.set
//        binding.attended.setOnCheckedChangeListener(
//            binding.attended.setOnCheckedChangeListener{group, checkedId ->
//                val radio: RadioButton = findViewById(checkedId)
//            }





        binding.fromdate.setOnClickListener {
            showDatePicker(binding.fromdate)
        }

        binding.untildate.setOnClickListener {
            showDatePicker(binding.untildate)
        }

        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }




        val school = binding.txtSchool
        val fromdate = binding.fromdate
        val untildate = binding.untildate
        val graduate = binding.graduate
        val description = binding.txtDescription
        val connection1 = binding.edtConnection1
        val connection2 = binding.edtConnection2
        val connection3 = binding.edtConnection3
        val attended = binding.attended


        var radioVal = if(binding.attended.checkedRadioButtonId == R.id.rb_graduate_school) "Graduate School" else "College"
        binding.attended.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                radioVal = "${radio.text}"
            })

        binding.btSave.setOnClickListener {
            trimAllEditText()
            val schoolValue = school.text.toString()
            val fromdateValue = fromdate.text.toString()
            val untildateValue = untildate.text.toString()
            val graduateValue = graduate.text.toString()
            val descriptionValue = description.text.toString()
            val connection1Value = connection1.text.toString()
            val connection2Value = connection2.text.toString()
            val connection3Value = connection3.text.toString()
            val attendedFor = attended
//            val rbcollegeValue = rbCollege.text.toString()

//            var attendedValue = attended.setOnCheckedChangeListener {group, checkedId ->
//                if (R.id.rb_college == checkedId) "collage" else "graduated"
//            }




            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

            var isValid = true

            if (TextUtils.isEmpty(schoolValue)){
                school.error = "School is required"
//                return@setOnClickListener
                isValid = false
            }
            if (TextUtils.isEmpty(fromdateValue)){
                fromdate.error = "Position is required"
//                return@setOnClickListener
                isValid = false
            }

            if (TextUtils.isEmpty(fromdateValue)){
                fromdate.error = "City is required"
//                return@setOnClickListener
                isValid = false
            }

            if (TextUtils.isEmpty(descriptionValue)){
                description.error = "Description is required"
//                return@setOnClickListener
                isValid = false
            }

            else{

                viewModel.editeducation(
                    "${education?.id}",
                    mutableMapOf(
                        "school" to schoolValue,
                        "time_priode_from" to fromdateValue,
                        "time_priode_until" to untildateValue,
                        "graduated" to graduateValue,
                        "description" to descriptionValue,
                        "connection1" to connection1Value,
                        "connection2" to connection2Value,
                        "connection3" to connection3Value,
                        "attended_for" to radioVal,

                        )
                )
            } }

        viewModel.responseeducationedit.observe(lifecycleOwner) {
            if (it.status == 200) {
                onBackPressed()
                showSuccessMessage(this, "Successfully Edited!")
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