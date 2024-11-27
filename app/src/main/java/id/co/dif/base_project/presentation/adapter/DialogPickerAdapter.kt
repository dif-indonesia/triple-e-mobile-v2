package id.co.dif.base_project.presentation.adapter

import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.BaseData
import id.co.dif.base_project.databinding.ItemPickerBinding

data class ItemPicker(
    val text: String
) : BaseData()

class DialogPickerAdapter : BaseAdapter<BaseViewModel, ItemPickerBinding, ItemPicker>() {
    var onClicked: (Int, ItemPicker) -> Unit = { _, _ -> }
    override val layoutResId: Int = R.layout.item_picker
    override fun onLoadItem(binding: ItemPickerBinding, data: MutableList<ItemPicker>, position: Int) {
        binding.text.text = data[position].text
        binding.text.setOnClickListener {
            onClicked(position, data[position])
        }
    }
}