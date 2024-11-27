package id.co.dif.base_project.presentation.adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import androidx.core.widget.TextViewCompat
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Notification
import id.co.dif.base_project.data.SiteHistory
import id.co.dif.base_project.data.TicketSeverity
import id.co.dif.base_project.data.TicketStatus
import id.co.dif.base_project.databinding.ItemHistoryBinding
import id.co.dif.base_project.presentation.activity.DetilTicketActivity
import id.co.dif.base_project.utils.colorRes
import id.co.dif.base_project.utils.str


class HistoryAdapter() : BaseAdapter<BaseViewModel, ItemHistoryBinding, SiteHistory>() {

    override val layoutResId = R.layout.item_history
    var onHistoryClicked: (data: Notification) -> Unit = { _ -> }

    override fun onLoadItem(
        binding: ItemHistoryBinding,
        data: MutableList<SiteHistory>,
        position: Int
    ) {
        context?.let { context ->

            val item = data[position]
            binding.txtTechnicianName.text = item.assign_to
            binding.idNumber.text = item.id
            binding.type.text = item.type
                  binding.etStatus.text = item.status
            Log.d("TAG", "onLoadItem:  ${item.status} ")
            TicketStatus.fromLabel(item.status.str).also {
                val fg = it.foreground.colorRes(context)
                val bg = it.background.colorRes(context)
                TextViewCompat.setCompoundDrawableTintList(
                    binding.etStatus,
                    ColorStateList.valueOf(fg)
                )
                binding.etStatus.setTextColor(fg)
            }
//        binding.date.text = item.created
            binding.txtPicName.text = item.managed_by
            binding.txtTechnicianName.text = item.assign_to
            //      binding.type.text = item.severety
            binding.txtDate.text = item.update

            TicketSeverity.fromLabel(item.severety.str).also {
                binding.type.text = it.label
                binding.type.backgroundTintList =
                    ColorStateList.valueOf(it.background.colorRes(context))
                binding.type.setTextColor(it.foreground.colorRes(context))
            }
            binding.txtCode.text = item.ticket_number

            binding.background.setOnClickListener {
                preferences.selectedTicketId.value = item.id
                context?.startActivity(Intent(context, DetilTicketActivity::class.java))
            }
        }


    }
}


