package id.co.dif.base_project.presentation.adapter

import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.data.Education
import id.co.dif.base_project.databinding.ItemEducationBinding
import id.co.dif.base_project.viewmodel.EducationViewModel

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 10:46
 *
 */


class EducationAdapter(
    var onClickEdit: (Education) -> Unit,
    var onClickDelete: (Education) -> Unit,
    val viewOnly: Boolean = false
) : BaseAdapter<EducationViewModel, ItemEducationBinding, Education>() {

    override val layoutResId = R.layout.item_education


    override fun onLoadItem(
        binding: ItemEducationBinding,
        data: MutableList<Education>,
        position: Int
    ) {
        val item = data[position]
        if (item.school != null) {
            binding.idEmpty.isVisible = true
            binding.txtName.setText(item.school)
            binding.txtFromdate.setText("${item.time_priode_from} To  ${item.time_priode_until}")
            binding.txtDescription.setText(item.description)
        } else {
            binding.idEmpty.isVisible = false
            binding.txtName.setText("")
            binding.txtFromdate.setText("")
            binding.txtDescription.setText("")
        }

        binding.edit.isVisible = !viewOnly
        binding.edit.setOnClickListener {
            showPopup(it, item)
        }
    }


    private fun showPopup(view: View, education: Education) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.popup_menu)
        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.delete -> {
                    Toast.makeText(view.context, item.title, Toast.LENGTH_SHORT).show()
                    onClickDelete(education)
                    true
                }

                R.id.edit -> {
                    Toast.makeText(view.context, item.title, Toast.LENGTH_SHORT).show()
                    onClickEdit(education)
                    true
                }
            }
            true
        }
        popup.show()
    }
}