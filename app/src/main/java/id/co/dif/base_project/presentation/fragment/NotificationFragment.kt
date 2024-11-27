package id.co.dif.base_project.presentation.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.data.MessageNotification
import id.co.dif.base_project.data.Notification
import id.co.dif.base_project.databinding.FragmentNotificationBinding
import id.co.dif.base_project.presentation.activity.DetilTicketActivity
import id.co.dif.base_project.presentation.adapter.NotificationAdapter
import id.co.dif.base_project.presentation.dialog.PickerDialog
import id.co.dif.base_project.utils.LinearSpacingItemDecoration
import id.co.dif.base_project.utils.PaginationScrollListener
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.asVisibility
import id.co.dif.base_project.viewmodel.NotificationViewModel

class NotificationFragment : BaseFragment<NotificationViewModel, FragmentNotificationBinding>() {
    override val layoutResId = R.layout.fragment_notification
    var isLoading = false
    var currentPage = 1
    var isLastPage = false
    private val pageStart = 1
    var onMessageRead: () -> Unit = { -> }
    var onRefresh: () -> Unit = {}
    lateinit var notificationAdapter: NotificationAdapter
    private var currentNotificationInteractedId: Int? = null

    private fun toggleEmptyView(showEmptyView: Boolean) {
        binding.layoutEmpty.visibility = showEmptyView.asVisibility
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewModel.getNotification(currentPage)

        binding.rvNotification.addItemDecoration(
            LinearSpacingItemDecoration(topMost = 16, bottomMost = 64)
        )
        viewModel.responseMarkNotificationAsRead.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                if (currentNotificationInteractedId == null) {
                    return@observe
                }
                onMessageRead()
                val message =
                    notificationAdapter.data.first { it.id == currentNotificationInteractedId }
                val messageIndex = notificationAdapter.data.indexOf(message)
                message.mesShow = false
                notificationAdapter.notifyItemChanged(messageIndex)
                currentNotificationInteractedId = null
            }
        }
        viewModel.responseNotification.observe(lifecycleOwner) {
            binding.layoutOnRefresh.isRefreshing = false
            onRefresh()
            populateNotificationList(it)
        }
        binding.layoutOnRefresh.setOnRefreshListener {
            binding.layoutOnRefresh.isRefreshing = true
            viewModel.getNotification(currentPage)
            setupNotificationList()
        }
        binding.rvNotification.addItemDecoration(
            LinearSpacingItemDecoration(
                top = 5,
                bottom = 5,
                right = 20,
                left = 20
            )
        )
        setupNotificationList()
    }

    private fun showNotificationPopupDialog(item: MessageNotification) {
        PickerDialog.newInstance(
            arrayListOf(
                "Mark as read"
            )
        ) { index, value ->
            when (index) {
                0 -> {
                    currentNotificationInteractedId = item.id
                    viewModel.markNotificationAsRead(item.id)
                }
            }
        }.show(childFragmentManager, "Notification Action")
    }

    private fun populateNotificationList(it: Resource<BaseResponseList<MessageNotification>>) {
        when (it) {
            is Resource.Error -> {
                notificationAdapter.removeLoadingFooter()
                if (currentPage > pageStart) currentPage--
                isLoading = false
            }

            is Resource.Loading -> {
                notificationAdapter.addLoadingFooter()
                isLoading = true
            }

            is Resource.Success -> {
                isLoading = false
                notificationAdapter.removeLoadingFooter()
                it.data?.let { response ->
                    if (response.status in StatusCode.SUCCESS) {
                        if (response.data.page == pageStart) {
                            notificationAdapter.data.clear()
                            notificationAdapter.notifyDataSetChanged()
                        }
                        notificationAdapter.addAll(response.data.list)
                        notificationAdapter.notifyDataSetChanged()
                        toggleEmptyView(notificationAdapter.data.isEmpty())
                        if (response.data.list.isEmpty()) isLastPage = true
                    } else{
                        showToast("Something went wrong!")
                    }
                }
            }
        }
    }

    private fun goToDetailTicket(data: MessageNotification) {
        preferences.selectedTicketId.value = data.mesReffId.toString()
        val intent = Intent(context, DetilTicketActivity::class.java)
        intent.putExtra("should_go_to_timeline", true)
        intent.putExtra("notes_position", -1)
        context?.startActivity(intent)
    }

    private fun navigateToDestinationFragment() {
        val destinationFragment = MessageNotificationFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        // Perform the fragment transaction
        fragmentManager.beginTransaction()
            .replace(R.id.id_message_frag, destinationFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupNotificationList() {
        isLoading = false
        currentPage = 1
        isLastPage = false
        binding.rvNotification.clearOnScrollListeners()
        notificationAdapter = NotificationAdapter()
        notificationAdapter.onNotificationClicked = { index, item ->
            viewModel.markNotificationAsRead(item.id)
            currentNotificationInteractedId = item.id
            goToDetailTicket(item)
        }
        notificationAdapter.onNotificationLongClick = {
            showNotificationPopupDialog(it)
        }
        val layoutManager = LinearLayoutManager(context)


        binding.rvNotification.layoutManager = layoutManager
        binding.rvNotification.adapter = notificationAdapter
        binding.rvNotification.addOnScrollListener(object :
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

    fun loadNextPage() {
        viewModel.cancelJob()
        viewModel.getNotification(page = currentPage)
    }

    fun loadFirstPage() {
        viewModel.getNotification(page = pageStart)
    }
}