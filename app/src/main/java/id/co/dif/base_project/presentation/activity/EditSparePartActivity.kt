package id.co.dif.base_project.presentation.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.SparePart
import id.co.dif.base_project.databinding.ActivityEditSparePartBinding
import id.co.dif.base_project.utils.trimAllEditText
import id.co.dif.base_project.utils.valueIsEmpty
import id.co.dif.base_project.viewmodel.EditSparePartViewModel

class EditSparePartActivity : BaseActivity<EditSparePartViewModel, ActivityEditSparePartBinding>() {
    override val layoutResId = R.layout.activity_edit_spare_part

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        var sparepart = preferences.sparePart.value
        val ticId = intent.getStringExtra("TIC_ID")

        binding.articleName.setText(sparepart?.spreqName.orEmpty())
        binding.articleCode.setText(sparepart?.spreqCode.orEmpty())
        binding.quantity.setText(sparepart?.spreqQuantity.orEmpty())
        binding.snSparepart.setText(sparepart?.spreqSn.orEmpty())
        binding.snFautyUnit.setText(sparepart?.spreqSnf.orEmpty())

        binding.save.setOnClickListener {
            val validator = Validator(binding)
            trimAllEditText()
            validator.enableFormValidationMode();
            if (!validator.validate() && binding.articleCode.valueIsEmpty() && binding.articleName.valueIsEmpty() && binding.quantity.valueIsEmpty() && binding.snFautyUnit.valueIsEmpty()) {
                binding.articleCode.error = "Field cannot be empty"
                binding.articleName.error = "Field cannot be empty"
            } else {
                viewModel.editSparePart(
                    spreq_id = "${sparepart?.spreqId}",
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

        viewModel.responseEditSparePart.observe(lifecycleOwner) {
            if (it.status == 200) {
                showSuccessMessage(this, "Successfully Edited!")
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

        binding.appBar.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.articleCode.doOnTextChanged { text, start, before, count ->
            if (viewModel.selecteSparepartCode != text) {
                viewModel.getSparePartByArticleCode(text.toString())
            }
        }
    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}