package id.co.dif.base_project.presentation.adapter

import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.TroubleTicketTimeline
import id.co.dif.base_project.databinding.ItemMessageNotificationBinding


class MessagesAdapter() :
    BaseAdapter<BaseViewModel, ItemMessageNotificationBinding, TroubleTicketTimeline>() {


    override val layoutResId = R.layout.item_message_notification
    override fun onLoadItem(
        binding: ItemMessageNotificationBinding,
        data: MutableList<TroubleTicketTimeline>,
        position: Int
    ) {
//        val item = data[position]
//        binding.time.text = item.time
//        binding.date.text = item.date
//        binding.status.text = item.status
//        binding.name.text = item.name


    }

    override fun getItemCount(): Int {
        return 5
    }


}