package id.co.dif.base_project.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.data.ChatNotification
import id.co.dif.base_project.data.MessageNotification
import id.co.dif.base_project.databinding.FragmentMessageNotificationBinding
import id.co.dif.base_project.presentation.adapter.MessageNotificationAdapter
import id.co.dif.base_project.presentation.dialog.PickerDialog
import id.co.dif.base_project.presentation.dialog.PopupMessagesDialog
import id.co.dif.base_project.utils.LinearSpacingItemDecoration
import id.co.dif.base_project.utils.PaginationScrollListener
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.asVisibility
import id.co.dif.base_project.utils.ellipsize
import id.co.dif.base_project.viewmodel.MessageNotificationViewModel

class MessageNotificationFragment :
    BaseFragment<MessageNotificationViewModel, FragmentMessageNotificationBinding>() {
    override val layoutResId = R.layout.fragment_message_notification
    var onShowEmptyView: (showEmptyView: Boolean, fragment: Fragment) -> Unit = { _, _ -> }
    var onNotificationRead: () -> Unit = {}
    var onRefresh: () -> Unit = {}
    var isLoading = false
    var currentPage = 1
    var isLastPage = false
    private val pageStart = 1
    lateinit var messageNotificationAdapter: MessageNotificationAdapter
    private var currentMessageInteractedId: Int? = null

    private fun toggleEmptyView(showEmptyView: Boolean) {
        binding.layoutEmpty.visibility = showEmptyView.asVisibility
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.addMessages.setOnClickListener {
            PopupMessagesDialog.newInstance { message, receiver, messageId ->
                setupMessageNotificationList()
                onMessageSent(message, receiver, messageId)
            }.show(
                childFragmentManager,
                PopupMessagesDialog::class.java.name
            )
        }

        binding.rvMessageNotification.addItemDecoration(
            LinearSpacingItemDecoration(topMost = 16, bottomMost = 64)
        )

        viewModel.responseMessageNotification.observe(lifecycleOwner) {
            binding.layoutOnRefresh.isRefreshing = false
            onRefresh()
            populateMessageList(it)
        }
        viewModel.responseMessageAsRead.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                val message =
                    messageNotificationAdapter.data.first { it.id == currentMessageInteractedId }
                message.chat_show = false
                val messageIndex = messageNotificationAdapter.data.indexOf(message)
                messageNotificationAdapter.notifyItemChanged(messageIndex)
                onNotificationRead()
            }
        }
        binding.layoutOnRefresh.setOnRefreshListener {
            setupMessageNotificationList()
            binding.layoutOnRefresh.isRefreshing = true
        }
        viewModel.responseDeleteChat.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                val message =
                    messageNotificationAdapter.data.firstOrNull() { it.id == currentMessageInteractedId }
                Log.d("TAG", "sdfdssdfdfs: $message $currentMessageInteractedId")
                if (message != null) {
                    message.chat_show = true
                    val messageIndex = messageNotificationAdapter.data.indexOf(message)
                    messageNotificationAdapter.data.remove(message)
                    messageNotificationAdapter.notifyItemRemoved(messageIndex)
                } else {
                    messageNotificationAdapter.notifyDataSetChanged()
                }
                toggleEmptyView(messageNotificationAdapter.data.isEmpty())
                currentActivity.showToast(getString(R.string.message_successfully_deleted))
                onRefresh()
            } else {
                currentActivity.showToast(it.message)
            }
        }
        setupMessageNotificationList()
    }

    private fun onMessageSent(message: String, receiver: String, messageId: Int) {
        currentActivity.showAlert(
            context = currentActivity,
            message = getString(R.string.your_message_was_sent, message.ellipsize(100), receiver),
            iconId = R.drawable.baseline_info_24,
            iconTint = R.color.light_blue,
            buttonPrimaryText = getString(R.string.undo),
            buttonSecondaryText = getString(R.string.dismiss),
            onButtonPrimaryClicked = {
                currentMessageInteractedId = messageId
                viewModel.deleteChat(id = messageId)
            }
        )
    }

    private fun showMessagePopupDialog(item: ChatNotification) {
        PickerDialog.newInstance(
            arrayListOf(
                getString(R.string.delete_message),
                getString(R.string.mark_as_read)
            )
        ) { index, value ->
            when (index) {
                0 -> {
                    currentMessageInteractedId = item.id
                    viewModel.deleteChat(item.id)
                }

                1 -> {
                    currentMessageInteractedId = item.id
                    viewModel.markChatAsRead(item.id)
                }
            }
        }.show(childFragmentManager, getString(R.string.message_action))
    }

    private fun populateMessageList(it: Resource<BaseResponseList<ChatNotification>>) {
        when (it) {
            is Resource.Error -> {
                messageNotificationAdapter.removeLoadingFooter()
                if (currentPage > pageStart) currentPage--
                isLoading = false
            }

            is Resource.Loading -> {
                messageNotificationAdapter.addLoadingFooter()
                isLoading = true
            }

            is Resource.Success -> {
                isLoading = false
                messageNotificationAdapter.removeLoadingFooter()
                it.data?.let { response ->
                    if (response.status in StatusCode.SUCCESS) {
                        if (response.data.page == pageStart) {
                            messageNotificationAdapter.data.clear()
                            messageNotificationAdapter.notifyDataSetChanged()
                        }
                        messageNotificationAdapter.addAll(response.data.list)
                        messageNotificationAdapter.notifyDataSetChanged()
                        toggleEmptyView(messageNotificationAdapter.data.isEmpty())
                        if (response.data.list.isEmpty()) isLastPage = true

                    }
                }
            }
        }
    }

    fun loadNextPage() {
        viewModel.cancelJob()
        viewModel.getMessageNotification(page = currentPage)
    }

    fun loadFirstPage() {
        viewModel.getMessageNotification(page = pageStart)
    }

    fun setupMessageNotificationList() {
        isLoading = false
        currentPage = 1
        isLastPage = false
        binding.rvMessageNotification.clearOnScrollListeners()
        messageNotificationAdapter = MessageNotificationAdapter()

        messageNotificationAdapter.onMessageLongClick = {
            showMessagePopupDialog(it)
        }
        val layoutManager = LinearLayoutManager(context)

        binding.rvMessageNotification.layoutManager = layoutManager
        binding.rvMessageNotification.adapter = messageNotificationAdapter
        binding.rvMessageNotification.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true;
                currentPage++;
                loadNextPage();
            }
        })
        loadFirstPage()
    }

    override fun onResume() {
        super.onResume()
        binding.rvMessageNotification.adapter?.let {
            val adapter = binding.rvMessageNotification.adapter as MessageNotificationAdapter
            onShowEmptyView(adapter.data.isEmpty(), this)
        }
    }


}