package id.co.dif.base_project.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.base.BaseViewHolder
import id.co.dif.base_project.data.ChatNotification
import id.co.dif.base_project.data.MessageNotification
import id.co.dif.base_project.databinding.ItemMessageNotificationBinding
import id.co.dif.base_project.databinding.ItemNotificationLoadingBinding
import id.co.dif.base_project.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MessageNotificationAdapter(data: List<ChatNotification> = listOf()) :
    BaseAdapter<NotificationViewModel, ViewBinding, ChatNotification>() {

    var timeInMillis = System.currentTimeMillis()
    val text: String = TimeAgo.using(timeInMillis)
    var onMessageLongClick: (item: ChatNotification) -> Unit = { _ -> }

    init {
        this.data.clear()
        this.data.addAll(data)
    }

    var isLoading = false

    override val layoutResId = R.layout.item_message_notification

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding> {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = when (viewType) {
            NotificationAdapter.ITEM_VIEW_TYPE_CONTENT -> {
                ItemMessageNotificationBinding.inflate(inflater, parent, false)
            }

            NotificationAdapter.ITEM_VIEW_TYPE_LOADING -> {
                ItemNotificationLoadingBinding.inflate(inflater, parent, false)
            }

            else -> throw Error("Unrecognized view type $viewType")
        }

        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.lastIndex && isLoading) NotificationAdapter.ITEM_VIEW_TYPE_LOADING else NotificationAdapter.ITEM_VIEW_TYPE_CONTENT
    }

    fun addLoadingFooter() {
        isLoading = true
        add(ChatNotification("", "", "", false, "", 0))
    }

    fun add(activeUser: ChatNotification) {
        data.add(activeUser)
        notifyItemInserted(data.lastIndex)
    }


    fun addAll(newData: List<ChatNotification>) {
        for (result in newData) {
            add(result)
        }
    }

    fun removeLoadingFooter() {
        isLoading = false
        val position = data.lastIndex
        if (data.lastOrNull() != null) {
            data.removeLast()
            notifyItemRemoved(position)
        }
    }


    override fun onLoadItem(
        binding: ViewBinding,
        data: MutableList<ChatNotification>,
        position: Int
    ) {
        when (getItemViewType(position)) {
            NotificationAdapter.ITEM_VIEW_TYPE_CONTENT -> {
                binding as ItemMessageNotificationBinding
                val item = data[position]



                binding.mAgo.text = parseTimeToAgo(item.chat_time.toString())
//        binding.mAgo.text = item.mesDate
                binding.txtName.text = item.chat_sender
                binding.txtDescription.text = item.chat_content
                binding.isRead = !item.chat_show
                binding.layoutContent.setOnLongClickListener {
                    onMessageLongClick(item)
                    true
                }
            }

            else -> {
            }
        }

    }


    private fun parseTimeToAgo(timeString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val parsedTime = dateFormat.parse(timeString)

        val timeAgo = TimeAgo.using(parsedTime.time, TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build())

        return timeAgo
    }


//    override fun getItemCount(): Int {
//        return 5
//    }


}