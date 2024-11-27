package id.co.dif.base_project.presentation.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.DialogPickerBinding
import id.co.dif.base_project.presentation.adapter.DialogPickerAdapter
import id.co.dif.base_project.presentation.adapter.ItemPicker

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 04:28
 *
 */


class PickerDialog(val items: List<String>, val onSelectedItem: (Int, String)->Unit) :
    BaseBottomSheetDialog<BaseViewModel, DialogPickerBinding, Any?>() {

    override val layoutResId = R.layout.dialog_picker

    companion object {
        fun newInstance(data: List<String>, onSelectedItem: (Int, String)->Unit): PickerDialog {
            return PickerDialog(data, onSelectedItem)
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.txtTitle.text = "$tag :"
//        binding.pickerView.items = generatePickerItems()
//        binding.btnChoose.setOnClickListener {
//            dismiss()
//            onSelectedItem(binding.pickerView.selectedItem?.id ?: 0)
//        }

        binding.listView.adapter = DialogPickerAdapter().apply {
            data.addAll(items.map { ItemPicker(it) })
            notifyDataSetChanged()
            onClicked = {position, selected ->
                dismiss()
                onSelectedItem(position, items[position])
            }
        }


//        binding.listView.adapter = ArrayAdapter(requireContext(), R.layout.item_picker, data)
//        binding.listView.setOnItemClickListener { _, _, position, _ ->
//            dismiss()
//            onSelectedItem(position, data[position])
//        }
//        binding.listView.smoothScrollToPosition(1)

//        if (selected.isNotEmpty()) {
//            binding.pickerView.items.forEach {
//                if (it.title == selected) {
//                    binding.pickerView.setSelectedItem(it)
//                }
//            }
//        }
    }

//    private fun generatePickerItems(): List<Item> {
//        return mutableListOf<Item>().apply {
//            data.forEachIndexed { index, item ->
//                add(PickerItem(id = index, title = item))
//            }
//        }
//    }

}