package id.co.dif.base_project.presentation.adapter

import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.data.Work
import id.co.dif.base_project.databinding.ItemWorkBinding
import id.co.dif.base_project.viewmodel.WorkViewModel

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 10:46
 *
 */


class WorkAdapter(
    var onClickEdit: (Work) -> Unit,
    var onClickDelete: (Work) -> Unit,
    val viewOnly: Boolean = false
) :
    BaseAdapter<WorkViewModel, ItemWorkBinding, Work>() {

    override val layoutResId = R.layout.item_work

    override fun onLoadItem(
        binding: ItemWorkBinding,
        data: MutableList<Work>,
        position: Int
    ) {
        val item = data[position]

        if (item.company != null) {
            binding.idEmpty.isVisible = true
            binding.txtName.setText(item.company)
            binding.txtCity.setText("${item.position} ${item.time_priode_from} to  ${item.time_priode_until}\n${item.city}")
        } else {
            binding.idEmpty.isVisible = false
            binding.txtName.setText("")
            binding.txtCity.setText("")
        }
        binding.edit.isVisible = !viewOnly
        binding.edit.setOnClickListener {
            showPopup(it, item)
        }
    }

    private fun showPopup(view: View, work: Work) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.popup_menu)
        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.delete -> {
                    Toast.makeText(view.context, item.title, Toast.LENGTH_SHORT).show()
                    onClickDelete(work)
                    true
                }

                R.id.edit -> {
                    Toast.makeText(view.context, item.title, Toast.LENGTH_SHORT).show()
                    onClickEdit(work)
                    true
                }
            }
            true
        }
        popup.show()
    }


}