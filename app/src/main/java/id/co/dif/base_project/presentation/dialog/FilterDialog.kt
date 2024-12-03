package id.co.dif.base_project.presentation.dialog

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.data.TicketStatus
import id.co.dif.base_project.databinding.FragmentDialogFilterBelitungBinding
import id.co.dif.base_project.databinding.FragmentDialogFilterBinding
import id.co.dif.base_project.utils.colorRes
import id.co.dif.base_project.viewmodel.TroubleTicketViewModel
import java.util.Calendar

class FilterDialog(
    var sortBy: String,
    var fromDate: String,
    var untilDate: String,
    var status: String,
    var severety: String,
    var filterIsOn: Boolean,
    val onClickSave: (
        sortBy: String,
        fromDate: String,
        untilDate: String,
        status: String,
        severity: String,
        filterIsOn: Boolean,
    ) -> Unit
) : BaseBottomSheetDialog<TroubleTicketViewModel, FragmentDialogFilterBinding, Any?>() {
    override val layoutResId = R.layout.fragment_dialog_filter
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val myArray = arrayListOf<String>().also {
            it.addAll(TicketStatus.getAll().map { it.label })
        }
        val myArray_severity = resources.getStringArray(R.array.Spinner_Items_Severity)

        binding.btnNewest.setOnClickListener {
            toggleSorting(true)
            sortBy = "tic_updated,desc"
        }

        binding.btnOldest.setOnClickListener {
            toggleSorting(false)
            sortBy = "tic_updated,asc"
        }

        if (sortBy == "tic_updated,asc") {
            toggleSorting(false)
        } else if (sortBy == "tic_updated,desc") {
            toggleSorting(true)
        }




        binding.startdate.setText(fromDate)
        binding.severity.setText(severety)
        binding.untildate.setText(untilDate)
        Log.d("TAG", "onViewBindingCreated: \n")

        binding.status.setText(status)

        binding.startdate.setOnClickListener {
            showDatePicker(binding.startdate)
//            dialog?.dismiss()
        }

        binding.untildate.setOnClickListener {
            showDatePicker(binding.untildate)
//            dialog?.dismiss()
        }
//        binding.status.setOnClickListener {
//            dialog?.dismiss()
//        }
        binding.apply.setOnClickListener {
            severety = binding.severity.text.toString()
            status = binding.status.text.toString()
            fromDate = binding.startdate.text.toString()
            untilDate = binding.untildate.text.toString()
            filterIsOn = sortBy != "tic_updated,desc"
                    || untilDate != ""
                    || fromDate != ""
                    || status != ""
                    || severety != ""
            onClickSave(sortBy, fromDate, untilDate, status, severety, filterIsOn)
            dismiss()
        }

        binding.reset.setOnClickListener {
            filterIsOn = false
            sortBy = "tic_updated,desc"
            toggleSorting(true)
            binding.untildate.setText("")
            binding.startdate.setText("")
            binding.status.setText("")
            binding.severity.setText("")
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
//        binding.status.setOnClickListener {
//            PickerDialog.newInstance(
//                arrayListOf<String>().also { it.addAll(myArray) }
//            ) { index, value ->
//                binding.status.setText(value)
//                binding.status.error = null
//            }.show(childFragmentManager, getString(R.string.status))
//        }

        binding.status.setOnClickListener {
            val statusList = arrayListOf(
                "Assigned",
                "On Progress",
                "Completed",
                "Pending"
            )
            PickerDialog.newInstance(statusList) { index, value ->
                binding.status.setText(value)
                binding.status.error = null
            }.show(childFragmentManager, getString(R.string.status))
        }


        binding.severity.setOnClickListener {
            PickerDialog.newInstance(
                arrayListOf<String>().also { it.addAll(myArray_severity) }
            ) { index, value ->
                binding.severity.setText(value)
                binding.severity.error = null
            }.show(childFragmentManager, getString(R.string.severety))
        }
//        binding.appply.setOnClickListener {
//            viewModel.getListTroubleTicket(status = selectedValue)
//            viewModel.getListTroubleTicket(from = fromdate)
//            viewModel.getListTroubleTicket(status = fromdate)
//            dismiss()
//        }


    }

    private fun toggleSorting(newest: Boolean) {
        if (newest) {
            binding.btnNewest.setBackgroundResource(R.drawable.bg_selected)
            binding.btnOldest.setBackgroundResource(R.drawable.bg_unselected)
            binding.btnNewest.setTextColor(R.color.white.colorRes(requireContext()))
            binding.btnOldest.setTextColor(R.color.black.colorRes(requireContext()))
        } else {
            binding.btnNewest.setBackgroundResource(R.drawable.bg_unselected)
            binding.btnOldest.setBackgroundResource(R.drawable.bg_selected)
            binding.btnNewest.setTextColor(R.color.black.colorRes(requireContext()))
            binding.btnOldest.setTextColor(R.color.white.colorRes(requireContext()))
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(), { _, year, month, dayOfMonth ->
                val date = "$year-${month + 1}-$dayOfMonth"
                editText.setText("$date")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
                Calendar.DAY_OF_MONTH
            )
        )
        datePicker.show()

    }
}