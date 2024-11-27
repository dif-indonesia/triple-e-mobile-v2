package id.co.dif.base_project.presentation.activity

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.AddWorkplaceBinding
import id.co.dif.base_project.databinding.EditWorkplaceBinding
import id.co.dif.base_project.utils.findViewsByType
import id.co.dif.base_project.utils.trimAllEditText
import id.co.dif.base_project.viewmodel.EditWorkPlaceViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.Inflater

class EditWorkPlaceActivity: BaseActivity<EditWorkPlaceViewModel, EditWorkplaceBinding>() {
    override val layoutResId =  R.layout.edit_workplace

    lateinit var inflater: Inflater



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        var work = preferences.userWork.value
//        binding.title.setText("Edit Work")

        binding.txtCitytown.setText("${work?.city}")
        binding.txtCompany.setText("${work?.company}")
        binding.txtDescription.setText("${work?.description}")
        binding.txtPosition.setText("${work?.position}")
        binding.fromdate.setText("${work?.time_priode_from}")
        binding.untildate.setText("${work?.time_priode_until}")



        binding.fromdate.setOnClickListener {
            showDatePicker(binding.fromdate)
        }

        binding.untildate.setOnClickListener {
            showDatePicker(binding.untildate)
        }

        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }




        val company = binding.txtCompany
        val position = binding.txtPosition
        val city = binding.txtCitytown
        val description = binding.txtDescription
        val fromdate = binding.fromdate
        val untildate = binding.untildate

        binding.btSave.setOnClickListener {
            trimAllEditText()

            val companyValue = company.text.toString()
            val positionValue = position.text.toString()
            val cityValue = city.text.toString()
            val descriptionValue = description.text.toString()
            val fromdateValue = fromdate.text.toString()
            val untildateValue = untildate.text.toString()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

            var isValid = true

            if (TextUtils.isEmpty(companyValue)){
                company.error = "Company is required"
//                return@setOnClickListener
                isValid = false
            }
            if (TextUtils.isEmpty(positionValue)){
                position.error = "Position is required"
//                return@setOnClickListener
                isValid = false
            }

            if (TextUtils.isEmpty(cityValue)){
                city.error = "City is required"
//                return@setOnClickListener
                isValid = false
            }

            if (TextUtils.isEmpty(descriptionValue)){
                description.error = "Description is required"
//                return@setOnClickListener
                isValid = false
            }

            if (TextUtils.isEmpty(fromdateValue)){
                fromdate.error = "Fromdate is required"
//                return@setOnClickListener
                isValid = false
            }

            if (TextUtils.isEmpty(untildateValue)){
                untildate.error = "Untildate is required"
//                return@setOnClickListener
                isValid = false
            }
            else{
                viewModel.putworkedit(
                    "${work?.id}",
                    mutableMapOf(
                        "company" to companyValue,
                        "position" to positionValue,
                        "city" to cityValue,
                        "description" to descriptionValue,
                        "time_priode_from" to fromdateValue,
                        "time_priode_until" to untildateValue,

                        )
                )
            } }

        viewModel.responseworkedit.observe(lifecycleOwner) {
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