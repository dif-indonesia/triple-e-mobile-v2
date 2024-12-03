package id.co.dif.base_project.presentation.dialog

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.TroubleTicket
import id.co.dif.base_project.databinding.DialogDashboardTicketListBinding
import id.co.dif.base_project.presentation.adapter.TroubleTicketAdapter
import id.co.dif.base_project.utils.PaginationScrollListener
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.isDeviceOnline
import id.co.dif.base_project.utils.toDp
import id.co.dif.base_project.viewmodel.TicketListDashboardDialogViewModel

class TicketListDashboardPopupDialog (
    var type_ticket: String
) :
    BaseBottomSheetDialog<TicketListDashboardDialogViewModel, DialogDashboardTicketListBinding, TroubleTicket>() {
    var location: Location? = null
    override val layoutResId = R.layout.dialog_dashboard_ticket_list
    lateinit var troubleTicketAdapter: TroubleTicketAdapter
    var isLoading = false
    var currentPage = 1
    var isLastPage = false
    var error = false
    private val pageStart = 1
    private var paginationListener: PaginationScrollListener? = null

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        troubleTicketAdapter = TroubleTicketAdapter()
        binding.rvTickets.adapter = troubleTicketAdapter

        binding.tvTypeTicket.text = type_ticket

        binding.rvTickets.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildLayoutPosition(view)
                outRect.left = 19.toDp
                outRect.right = 19.toDp
                outRect.top = 16.toDp
                if (position == troubleTicketAdapter.itemCount - 1) {
                    outRect.bottom = 25.toDp
                }
            }
        })

        viewModel.responseAllTroubleTickets.observe(lifecycleOwner) {
            populateTroubleTicketList(it)
        }
        setupTroubleTicketList()

    }

    private fun populateTroubleTicketList(it: Resource<BaseResponseList<TroubleTicket>>) {
        when (it) {
            is Resource.Error -> {
                if (currentPage > pageStart) currentPage--
                troubleTicketAdapter.removeLoadingFooter()
                isLoading = false
                Log.d("TAG", "populateTroubleTicketList: Error $currentPage")
            }

            is Resource.Loading -> {
                isLoading = true
                Log.d("TAG", "populateTroubleTicketList: Loading $currentPage")
            }

            is Resource.Success -> {
                isLoading = false
                troubleTicketAdapter.removeLoadingFooter()
                it.data?.let { response ->
                    if (response.status in StatusCode.SUCCESS) {
                        val data = response.data.list
                        troubleTicketAdapter.addAll(response.data.list)
                        isLastPage = data.isEmpty()
                        Log.d("TAG", "populateTroubleTicketList: ${data.size}")
                    } else {
                        isLastPage = true
                    }
                }
                binding.txtNoticket.isVisible = troubleTicketAdapter.data.isEmpty()
                Log.d("TAG", "populateTroubleTicketList: Success $currentPage")

            }
        }
        val filterCriteria = listOf(
            viewModel.fromDate,
            viewModel.untilDate,
            type_ticket,
            viewModel.search,
            viewModel.severety
        )
        val dataIsEmpty = troubleTicketAdapter.data.isEmpty()
        val filterIsEmpty = filterCriteria.all { it == "" }
        val shouldShowEmptyTicketView = filterIsEmpty && dataIsEmpty && !error
        val shouldShowEmptyTicketViewFilter = !shouldShowEmptyTicketView && dataIsEmpty
        val shouldShowErrorTicketView =
            !shouldShowEmptyTicketView && !shouldShowEmptyTicketViewFilter && error
    }

    private fun setupTroubleTicketList() {
        Log.d("TAG", "populateTroubleTicketList setupTroubleTicketList: ")
        troubleTicketAdapter = TroubleTicketAdapter()
        val layoutManager = LinearLayoutManager(context)
        if (paginationListener == null) {
            paginationListener = object :
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
                    troubleTicketAdapter.addLoadingFooter()
                    loadNextPage();
                }
            }
            binding.rvTickets.addOnScrollListener(paginationListener as PaginationScrollListener)
        }

        binding.rvTickets.layoutManager = layoutManager
        binding.rvTickets.adapter = troubleTicketAdapter
        loadFirstPage()
    }

    fun loadNextPage() {
        isLoading = true;
        currentPage++;
        viewModel.getListTroubleTicket(status = type_ticket, page = currentPage)
    }

    private fun loadFirstPage() {
        viewModel.getListTroubleTicket(status = type_ticket, page = pageStart)

        if (!requireContext().isDeviceOnline()) {
            currentActivity.showAlert(
                context = requireContext(),
                message = "You're currently offline!",
                value = "Tickets will be limited to last fetched data",
                buttonPrimaryText = "OK",
                iconId = R.drawable.baseline_info_24
            )
        }
    }

    private fun resetTroubleTicketList() {
        isLoading = false
        currentPage = 1
        isLastPage = false
        setupTroubleTicketList()
    }

}