package id.co.dif.base_project.presentation.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.SparePart
import id.co.dif.base_project.databinding.ActivityRequestSparePartBinding
import id.co.dif.base_project.utils.findViewsByType
import id.co.dif.base_project.utils.trimAllEditText
import id.co.dif.base_project.utils.valueIsEmpty
import id.co.dif.base_project.viewmodel.RequestReturnSparePartViewModel

class RequestReturnSparePartActivity(override val layoutResId: Int = R.layout.activity_request_spare_part) :
    BaseActivity<RequestReturnSparePartViewModel, ActivityRequestSparePartBinding>() {
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val ticId = preferences.ticketDetails.value?.tic_id
        binding.save.setOnClickListener {
            trimAllEditText()
            val validator = Validator(binding)
            validator.enableFormValidationMode();
            if (!validator.validate() && binding.articleCode.valueIsEmpty() && binding.articleName.valueIsEmpty() && binding.quantity.valueIsEmpty() && binding.snFautyUnit.valueIsEmpty()) {
                binding.articleCode.error = "Field cannot be empty"
                binding.articleName.error = "Field cannot be empty"
            }  else {
                viewModel.addSparePart(
                    tic_id = ticId.toString(),
                    param = mutableMapOf(
                        "spreq_name" to binding.articleName.text.toString(),
                        "spreq_code" to binding.articleCode.text.toString(),
                        "spreq_quantity" to binding.quantity.text.toString(),
                        "spreq_sn" to binding.snSparepart.text.toString(),
                        "spreq_snf" to binding.snFautyUnit.text.toString(),
                    )
                )
            }
        }

        viewModel.responseaddSparePart.observe(lifecycleOwner) {
            if (it.status == 200) {
                showToast("Successfully Add Spare Part!")
                Log.d(TAG, "result: ")
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                showToast("Something went wrong!")
            }
        }

        viewModel.responseGetSparePartByArticleName.observe(lifecycleOwner) {
            if (it.status == 200) {
                val adapter: ArrayAdapter<SparePart> = ArrayAdapter<SparePart>(
                    this,
                    R.layout.item_spinner_dropdown,
                    it.data.list
                )
                binding.articleName.setAdapter(adapter)
                if (binding.articleName.text.isNotEmpty() && adapter.count != 1) {
                    binding.articleName.showDropDown()
                }
                binding.articleName.setOnItemClickListener { parent, view, position, id ->
                    val item = it.data.list[position]
                    viewModel.selecteSparepartName = item.spareName
                    binding.articleName.setText(item.spareName)
                    adapter.clear()
                    binding.articleCode.error = null
                }
            }
        }

        viewModel.responseGetSparePartByArticleCode.observe(lifecycleOwner) {
            if (it.status == 200) {
                val adapter: ArrayAdapter<SparePart> = ArrayAdapter<SparePart>(
                    this,
                    R.layout.item_spinner_dropdown,
                    it.data.list
                )
                binding.articleCode.setAdapter(adapter)
                if (binding.articleCode.text.isNotEmpty() && adapter.count != 1) {
                    binding.articleCode.showDropDown()
                }
                binding.articleCode.setOnItemClickListener { parent, view, position, id ->
                    val item = it.data.list[position]
                    viewModel.selecteSparepartCode = item.spareCode
                    binding.articleCode.setText(item.spareCode)
                    adapter.clear()
                    binding.articleName.error = null
                }
            }
        }

        binding.articleName.doOnTextChanged { text, start, before, count ->
            if (viewModel.selecteSparepartName != text) {
                viewModel.getSparePartByArticleName(text.toString())
            }
        }


        binding.articleCode.doOnTextChanged { text, start, before, count ->
            if (viewModel.selecteSparepartCode != text) {
                viewModel.getSparePartByArticleCode(text.toString())
            }
        }
        binding.appBar.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}