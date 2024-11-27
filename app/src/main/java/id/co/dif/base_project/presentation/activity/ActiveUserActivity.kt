package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.data.ActiveUser
import id.co.dif.base_project.databinding.ActivityUserActivityBinding
import id.co.dif.base_project.presentation.adapter.ActiveUsersAdapter
import id.co.dif.base_project.utils.LinearSpacingItemDecoration
import id.co.dif.base_project.utils.PaginationScrollListener
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.viewmodel.UserActivityViewModel

class ActiveUserActivity : BaseActivity<UserActivityViewModel, ActivityUserActivityBinding>() {
    override val layoutResId: Int = R.layout.activity_user_activity
    var isLoading = false
    var currentPage = 1
    var isLastPage = false
    private val pageStart = 1

    override fun onViewBindingCreated(savedInstanceState: Bundle?) = with(binding) {

        viewModel.usersListActivity.observe(this@ActiveUserActivity.lifecycleOwner) {
            populateUsersActivityList(it)
        }

        rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.rvActiveUsers.addItemDecoration(
            LinearSpacingItemDecoration(
                right = 14,
                left = 14,
                top = 4,
                bottom = 4,
                topMost = 14,
                bottomMost = 14
            )
        )
        setupUsersActivityList()

    }

    private fun populateUsersActivityList(it: Resource<BaseResponseList<ActiveUser>>) {
        val activeUsersAdapter = binding.rvActiveUsers.adapter as ActiveUsersAdapter
        when (it) {
            is Resource.Error -> {
                if (currentPage > pageStart) currentPage--
                isLoading = false
                activeUsersAdapter.removeLoadingFooter()
            }

            is Resource.Loading -> {
                isLoading = true
                activeUsersAdapter.addLoadingFooter()
            }

            is Resource.Success -> {
                isLoading = false
                activeUsersAdapter.removeLoadingFooter()
                it.data?.let { response ->
                    if (response.status in StatusCode.SUCCESS) {
                        activeUsersAdapter.addAll(response.data.list)
                        isLastPage = response.data.list.isEmpty()
                    }
                }
            }
        }
    }


    private fun setupUsersActivityList() {
        val activeUserAdapter = ActiveUsersAdapter()
        val layoutManager = LinearLayoutManager(this)

        binding.rvActiveUsers.layoutManager = layoutManager
        binding.rvActiveUsers.adapter = activeUserAdapter
        binding.rvActiveUsers.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
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
        viewModel.getListOfActiveUsers(page = currentPage)
    }

    private fun loadFirstPage() {
        viewModel.getListOfActiveUsers(page = pageStart)
    }

}