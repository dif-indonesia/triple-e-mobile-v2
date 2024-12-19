package id.co.dif.base_project.presentation.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.data.Notification
import id.co.dif.base_project.data.TroubleTicket
import id.co.dif.base_project.databinding.DialogPopupAlertBinding
import id.co.dif.base_project.databinding.FragmentTroubleTicketBinding
import id.co.dif.base_project.presentation.activity.AddTicketActivity
import id.co.dif.base_project.presentation.activity.SelectSiteActivity
import id.co.dif.base_project.presentation.adapter.TroubleTicketAdapter
import id.co.dif.base_project.presentation.dialog.CheckinDialog
import id.co.dif.base_project.presentation.dialog.CheckoutDialog
import id.co.dif.base_project.presentation.dialog.FilterDialog
import id.co.dif.base_project.presentation.dialog.PickerDialog
import id.co.dif.base_project.presentation.dialog.RequestPendingDialog
import id.co.dif.base_project.presentation.dialog.approveCheckinDialog
import id.co.dif.base_project.presentation.dialog.approvePermitDialog
import id.co.dif.base_project.presentation.dialog.approvedReqSubmitTicketDialog
import id.co.dif.base_project.presentation.dialog.declineInformationDialog
import id.co.dif.base_project.presentation.dialog.submitTicketDialog
import id.co.dif.base_project.presentation.dialog.takeTicketDialog
import id.co.dif.base_project.utils.LinearSpacingItemDecoration
import id.co.dif.base_project.utils.PaginationScrollListener
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.dimBehind
import id.co.dif.base_project.utils.ifTrue
import id.co.dif.base_project.utils.isDeviceOnline
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.viewmodel.TroubleTicketViewModel

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 00:34
 *
 */
class TroubleTicketFragment : BaseFragment<TroubleTicketViewModel, FragmentTroubleTicketBinding>() {
    override val layoutResId = R.layout.fragment_trouble_ticket
    var shouldExecuteOnResume = false
    lateinit var troubleTicketAdapter: TroubleTicketAdapter
    var isLoading = false
    var currentPage = 1
    var isLastPage = false
    var error = false
    var hasStarted = false
    private val pageStart = 1
    lateinit var alertBinding: DialogPopupAlertBinding
    private lateinit var alertWindow: PopupWindow
    private var currentAlertAnchor: View? = null
    private var onAlertPopupClick: () -> Unit = { -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldExecuteOnResume = false
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        preferences.requestUpload
        binding.edtSearch.doOnTextChanged() { text, start, before, count ->
            viewModel.search = text.toString()
            resetTroubleTicketList()
        }

        binding.filter.setOnClickListener {
            showDialog()
        }
        binding.btnEmptyListModifyFilter.setOnClickListener {
            showDialog()
        }
        binding.btnErrorList.setOnClickListener {
            resetTroubleTicketList()
        }

        setupChartSelectedChip()
        troubleTicketAdapter = TroubleTicketAdapter(
            onItemClicked = {onCheckinChekout(it)},
            onAlertClicked = { anchor, message ->
                declineInformationDialog(message).show(childFragmentManager, "declineInformationDialog")
            }
        )

//        troubleTicketAdapter = TroubleTicketAdapter{onCheckinChekout(it)}
//
//        troubleTicketAdapter.onAlertClick = { anchor, message, onAction ->
//            onAlertPopupClick = onAction
//            showAlertPopup(anchor = anchor, message = message).log("pop up")
//        }

        binding.recyclerView.adapter = troubleTicketAdapter

        session?.let { session ->
            val visibility = (session.emp_security ?: 0) >= 2
            binding.btnAddTicket.isVisible = visibility
//            binding.btnEmptyListAdd.isVisible = visibility
        }
//
//        binding.btnAddTicket.isVisible =
//            session?.emp_security == 3 || session?.emp_security == 2
//        binding.btnEmptyListAdd.isVisible =
//            session?.emp_security == 3 || session?.emp_security == 2

        binding.btnAddTicket.setOnClickListener {
            startActivity(AddTicketActivity.newInstance(requireContext()))
        }
//        binding.btnAddTicket.setOnClickListener {
//            startActivity(SelectSiteActivity.newInstance(requireContext()))
//        }
        binding.btnEmptyListAdd.setOnClickListener {
            startActivity(AddTicketActivity.newInstance(requireContext()))
        }

        binding.recyclerView.addItemDecoration(
            LinearSpacingItemDecoration(
                topMost = 16,
                top = 16,
                right = 16,
                left = 16,
            )
        )


        viewModel.responseAllTroubleTickets.observe(lifecycleOwner) {
            binding.layoutOnRefresh.isRefreshing = false
            populateTroubleTicketList(it)
        }

        binding.layoutOnRefresh.setOnRefreshListener {
            binding.layoutOnRefresh.isRefreshing = true
            resetTroubleTicketList()
        }
        if (!hasStarted) {
            resetTroubleTicketList()
            hasStarted = true
        }

    }

    private fun resetTroubleTicketList() {
        isLoading = false
        currentPage = 1
        isLastPage = false
        setupTroubleTicketList()
    }

    private fun showDialog() {
        val dialog: DialogFragment = FilterDialog(
            fromDate = viewModel.fromDate,
            untilDate = viewModel.untilDate,
            status = viewModel.status,
            sortBy = viewModel.sortBy,
            severety = viewModel.severety,
            filterIsOn = viewModel.filterIsOn,
            onClickSave = { sortby, fromDate, untilDate, status, severety, filterIsOn ->
                binding.groupFilterDot.isVisible = filterIsOn
                viewModel.filterIsOn = filterIsOn
                viewModel.severety = severety
                viewModel.fromDate = fromDate
                viewModel.sortBy = sortby
                viewModel.untilDate = untilDate
                viewModel.status = status
                resetTroubleTicketList()
            })
        dialog.show(childFragmentManager, "dialog")
    }

    private fun populateTroubleTicketList(it: Resource<BaseResponseList<TroubleTicket>>) {
        when (it) {
            is Resource.Error -> {
                if (currentPage > pageStart) currentPage--
                troubleTicketAdapter.removeLoadingFooter()
                isLoading = false
                error = true
            }

            is Resource.Loading -> {
                troubleTicketAdapter.addLoadingFooter()
                isLoading = true
                error = false
            }

            is Resource.Success -> {
                isLoading = false
                error = false
                troubleTicketAdapter.removeLoadingFooter()
                it.data?.let { response ->
                    if (response.status in StatusCode.SUCCESS) {
                        val data = response.data.list
                        troubleTicketAdapter.addAll(data)
                        if (data.isEmpty()) isLastPage = true
                    }
                }

            }
        }
        val filterCriteria = listOf(
            viewModel.fromDate,
            viewModel.untilDate,
            viewModel.status,
            viewModel.search,
            viewModel.severety
        )
        val dataIsEmpty = troubleTicketAdapter.data.isEmpty()
        val filterIsEmpty = filterCriteria.all { it == "" }
        val shouldShowEmptyTicketView = filterIsEmpty && dataIsEmpty && !error
        val shouldShowEmptyTicketViewFilter = !shouldShowEmptyTicketView && dataIsEmpty
        val shouldShowErrorTicketView =
            !shouldShowEmptyTicketView && !shouldShowEmptyTicketViewFilter && error

        binding.layoutEmptyState.isVisible = shouldShowEmptyTicketView
        binding.layoutErrorState.isVisible = shouldShowErrorTicketView
        binding.layoutEmptyStateFilter.isVisible = shouldShowEmptyTicketViewFilter


    }

    private fun setupTroubleTicketList() {
        troubleTicketAdapter = TroubleTicketAdapter(
            onItemClicked = {onCheckinChekout(it)},
            onAlertClicked = { anchor, message ->
                declineInformationDialog(message).show(childFragmentManager, "declineInformationDialog")
            }
        )
        troubleTicketAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.clearOnScrollListeners()
        val paginationListener = object :
            PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                loadNextPage();
            }
        }
        binding.recyclerView.addOnScrollListener(paginationListener as PaginationScrollListener)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = troubleTicketAdapter
        loadFirstPage()
    }

    fun loadNextPage() {
        isLoading = true;
        currentPage++;
        viewModel.getListTroubleTicket(page = currentPage)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getListTroubleTicketThenSaveItToLocal()
        if (shouldExecuteOnResume) {
            resetTroubleTicketList()
        } else {
            shouldExecuteOnResume = true
        }
    }

    private fun loadFirstPage() {
        viewModel.getListTroubleTicket(page = pageStart)

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


//    private fun onCheckinChekout(tt: TroubleTicket){
//        CheckinDialog().show(childFragmentManager, "CheckinDialog")
//    }

    private fun onCheckinChekout(tt: TroubleTicket) {
        val ticId = tt.ticId
        val permitId = tt.permitStatus?.permitId
        val subTicketId = tt.submitStatus?.submitId
        val latitude = tt.site?.siteLatitude
        val longtitud = tt.site?.siteLangtitude
        val checkID = tt.checkinStatus?.checkinId
        val pendingId = tt.pendingStatus?.pndId
        when {
            tt.pendingStatus?.pdnRequestAt != null && tt.pendingStatus?.issuer == null -> {
                RequestPendingDialog(
                    pendingId,
                    tt.ticId,
                    tt.pendingStatus?.pdnReason
                ){
                    setupTroubleTicketList()
                }.show(childFragmentManager, "PendingRequestDialog")
            }
            tt.pendingStatus?.pdnRequestAt != null && tt.pendingStatus?.issuer != null -> {
                RequestPendingDialog(
                    pendingId,
                    tt.ticId,
                    tt.pendingStatus?.pdnReason,
                    tt.pendingStatus?.pdnInformation
                ){
                    setupTroubleTicketList()
                }.show(childFragmentManager, "PendingRequestDialogApprove")
            }
            tt.permitStatus?.permitApproved == true && tt.ticCheckinAt == null && tt.checkinStatus != null -> {
                approveCheckinDialog(
                    checkID,
                    tt.ticFieldEngineer,
                    tt.checkinStatus?.checkinRequestAt,
                    tt.checkinStatus?.checkinEviden,
                    tt.checkinStatus?.checkinInformation
                ){
                    setupTroubleTicketList()
                }.show(childFragmentManager, "ApproveCheckinDialog")
            }
            tt.permitStatus == null ->{
                takeTicketDialog(ticId){
                    setupTroubleTicketList()
                }.show(childFragmentManager, "TakeTicketDialog")
            }
            // Jika permitStatus.permitApproved dan permitStatus.permitInformation keduanya null
            tt.permitStatus?.permitApproved == null && tt.permitStatus?.permitInformation == null -> {
                approvePermitDialog(permitId){
                    setupTroubleTicketList()
                }.show(childFragmentManager, "ApprovePermitDialog")
            }
            // Jika permitStatus.permitApproved dan permitStatus.permitApproved keduanya true
            tt.permitStatus?.permitApproved == true && tt.ticCheckinAt == null-> {
                CheckinDialog(
                    ticId,
                    latitude,
                    longtitud
                )
                {
                    setupTroubleTicketList()
                }.show(childFragmentManager, "CheckinDialog")
            }
            // Jika tic_checkin_at tidak null
            tt.permitStatus?.permitApproved == true && tt.ticCheckinAt != null  && tt.submitStatus == null || tt.submitStatus?.submitApproved == false-> {
                submitTicketDialog(ticId){
                    setupTroubleTicketList()
                }.show(childFragmentManager, "SubmitTicDialog")
            }
            // Jika tic_checkin_at tidak null
            tt.permitStatus?.permitApproved == true && tt.ticCheckinAt != null  && tt.submitStatus?.submitApproved == true -> {
                CheckoutDialog(ticId){
                    setupTroubleTicketList()
                }.show(childFragmentManager, "CheckoutDialog")
            }
            // Jika tic_checkin_at tidak null
            tt.permitStatus?.permitApproved == true && tt.ticCheckinAt != null  && tt.submitStatus != null-> {
                approvedReqSubmitTicketDialog(subTicketId){
                    setupTroubleTicketList()
                }.show(childFragmentManager, "ApproveSubmitTicDialog")
            }
        }
    }

    private fun showAlertPopup(
        anchor: View,
        dimBehind: Boolean? = true,
        message: String?
    ) {
        Log.d("ShowAlertPopup", "Showing popup with message: $message")
        currentAlertAnchor = anchor
        alertWindow.dismiss()
        alertBinding.text.text = message
        alertWindow.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val xOff = -alertWindow.contentView.measuredWidth
        val yOff = (-alertWindow.contentView.measuredHeight - anchor.height) / 2

        Log.d("ShowAlertPopup", "xOff: $xOff, yOff: $yOff")

        alertWindow.showAsDropDown(anchor, xOff, yOff)
        dimBehind.ifTrue {
            alertWindow.dimBehind()
            alertWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertWindow.isOutsideTouchable = true
            alertWindow.isFocusable = true
            alertWindow.update()

            alertWindow.contentView.setOnTouchListener { view, event ->
                (event.action == MotionEvent.ACTION_OUTSIDE).ifTrue {
                    alertWindow.dismiss()
                }
                view.performClick()
            }
        }

    }

    private fun setupChartSelectedChip() = fragmentContext?.let { context ->

        binding.root.setOnClickListener {
            alertWindow.dismiss()
        }
        val li = LayoutInflater.from(context)
        alertBinding =
            DialogPopupAlertBinding.inflate(li)
        alertWindow = PopupWindow(
            alertBinding.root,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertBinding.root.setOnClickListener {
            onAlertPopupClick()
            alertWindow.dismiss()
        }
        binding.root.viewTreeObserver.addOnScrollChangedListener {
            currentAlertAnchor?.let {
                alertWindow.dismiss()
                currentAlertAnchor = null
            }
        }

        // Dismiss if the chip goes out of the visible screen

    }

}