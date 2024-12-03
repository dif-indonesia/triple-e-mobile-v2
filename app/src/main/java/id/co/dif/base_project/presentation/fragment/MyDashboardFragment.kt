package id.co.dif.base_project.presentation.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.data.PlainValueLabel
import id.co.dif.base_project.data.TicketHandling
import id.co.dif.base_project.data.TicketQuality
import id.co.dif.base_project.databinding.FragmentMyDashboardBinding
import id.co.dif.base_project.databinding.ItemChartSelectedPopupBinding
import id.co.dif.base_project.presentation.activity.ActiveUserActivity
import id.co.dif.base_project.presentation.adapter.ActiveUsersAdapter
import id.co.dif.base_project.presentation.dialog.TicketListDashboardPopupDialog
import id.co.dif.base_project.presentation.dialog.approvedReqSubmitTicketDialog
import id.co.dif.base_project.utils.LinearSpacingItemDecoration
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.base64ImageToBitmap
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.populateNakedBarChart
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.utils.toFormattedNumber
import id.co.dif.base_project.viewmodel.MyDashboardViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

class MyDashboardFragment :
    BaseFragment<MyDashboardViewModel, FragmentMyDashboardBinding>() {
    override val layoutResId: Int = R.layout.fragment_my_dashboard
    private lateinit var chartSelectedChip: PopupWindow
    private lateinit var chartSelectedChipBinding: ItemChartSelectedPopupBinding
    private var currentChartSelectedChipAnchor: View? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewModel.responseTicketInfo.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                binding.ticketInfo = it.data
                binding.executePendingBindings()
            }
        }

        viewModel.cancelJob()
        viewModel.getFirst5ActiveUsers()
        viewModel.getUserActivityLog()
        viewModel.getTicketInfo()
        viewModel.getSavedBasicInfo()
        viewModel.getCompletedProfile()
        viewModel.getTicketQuality()
        viewModel.getTicketHandling()
        setupChartSelectedChip()

        viewModel.responseCompletedProfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                binding.completedProfile = it.data
            }
        }
        viewModel.responseTicketHandling.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                consumeTicketHandling(it.data)
            }
        }
        viewModel.responseTicketQuality.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                consumeCompetenceDevelopmentProgressBars(it.data)
            }
        }

        viewModel.savedBasicInfo.observe(lifecycleOwner) {
            it?.let {
                binding.skill = it.skill
                binding.name = it.fullname
                binding.role = it.position
                it.cover?.let {
                    binding.header.loadImage(it, shimmerDrawable())
                }
                it.photo_profile?.let { encoded ->
                    binding.imgProfile.setImageBitmap(base64ImageToBitmap(encoded))
                }
            }
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
        viewModel.responseActiveUsers.observe(this.lifecycleOwner) { response ->
            if (response.status in StatusCode.SUCCESS) {
                binding.rvActiveUsers.adapter = ActiveUsersAdapter(response.data.list)
            }
        }
//        viewModel.responseUserActivityLog.observe(this.lifecycleOwner) {
//            if (it.status in StatusCode.SUCCESS) {
//                populateUserActivityLogChart(it.data.list)
//            }
//        }
        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(context, ActiveUserActivity::class.java))
        }

        session?.let { session ->
            val visibility = (session.emp_security ?: 0) >= 2
            binding.layoutActiveUser.isVisible = visibility
        }

        session?.let { session ->
            val visibility = (session.emp_security ?: 0) <= 2 && (session.emp_security ?: 0) != 0
            binding.layoutMttr.isVisible = visibility
        }

        binding.icTicketEntry1.setOnClickListener {
            TicketListDashboardPopupDialog("Closed").show(childFragmentManager, "TicketListDashboardDialog")
        }
        binding.icTicketEntry2.setOnClickListener {
            TicketListDashboardPopupDialog("On Progress").show(childFragmentManager, "TicketListDashboardDialog")
        }
        binding.icTicketEntry3.setOnClickListener {
            TicketListDashboardPopupDialog("").show(childFragmentManager, "TicketListDashboardDialog")
        }
        binding.icTicketEntry4.setOnClickListener {
            TicketListDashboardPopupDialog("All").show(childFragmentManager, "TicketListDashboardDialog")
        }



    }

    @SuppressLint("SetTextI18n")
    private fun consumeTicketHandling(data: TicketHandling) {
        binding.progressMyTicketHandlingContribution.progress = data.ticketHandling
        binding.tvProgressMyTicketHandlingContribution.text = "${data.ticketHandling}%"
    }



    @SuppressLint("SetTextI18n")
    private fun consumeCompetenceDevelopmentProgressBars(data: TicketQuality) {
        data.let {
            val numericValue = it.program
            val numberFormat = NumberFormat.getNumberInstance(Locale.US)
            val formattedNumber = numberFormat.format(numericValue)
            val formattedWithK = "$formattedNumber\u006B"
            binding.tvProgressProgramCompetenceDevelopmentStatus.text = formattedWithK
            binding.progressProgramCompetenceDevelopmentStatus.progress = it.program
        }
    }

//    private fun populateUserActivityLogChart(userActivitiesLog: List<PlainValueLabel>) {
//        val labels = userActivitiesLog.map { it.label }
//        val values = userActivitiesLog.map { it.count }
//        binding.chartUserActivity.populateNakedBarChart(values)
//        val totalMinutes = values.sum()
//        val hours = totalMinutes / 60
//        val minutes = totalMinutes % 60
//        binding.totalHoursActivity = hours.toString()
//        binding.totalMinutesActivity = minutes.toString()
//        binding.totalDaysActivity = userActivitiesLog.size.toString()
//
//        binding.chartUserActivity.setOnChartValueSelectedListener(object :
//            OnChartValueSelectedListener {
//            override fun onValueSelected(e: Entry?, h: Highlight?) {
//                val minutes = e!!.y.roundToInt()
//                val totalHours = minutes / 60
//                val totalMinutes = minutes % 60
//                showChartChipPopup(
//                    highlight = h,
//                    anchor = binding.chartUserActivity,
//                    xLabel = labels,
//                    value = "$totalHours hours $totalMinutes minutes"
//                )
//
//            }
//
//            override fun onNothingSelected() {
//                chartSelectedChip.dismiss()
//            }
//
//        })
//
//    }

    private fun setupChartSelectedChip() {
        chartSelectedChipBinding =
            ItemChartSelectedPopupBinding.inflate(layoutInflater, null, false)
        chartSelectedChip = PopupWindow(
            chartSelectedChipBinding.root,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        chartSelectedChipBinding.cardItemChartSelectedPopup.setOnClickListener {
            (currentChartSelectedChipAnchor as Chart<*>).highlightValues(null)
            chartSelectedChip.dismiss()
        }
        // Dismiss if the chip goes out of the visible screen
        binding.root.viewTreeObserver.addOnScrollChangedListener {
            currentChartSelectedChipAnchor?.let {
                val scrollBounds = Rect()
                binding.root.getHitRect(scrollBounds)
                if (it.getLocalVisibleRect(scrollBounds).not() && chartSelectedChip.isShowing) {
                    chartSelectedChip.dismiss()
                    (currentChartSelectedChipAnchor as Chart<*>).highlightValue(null)
                    currentChartSelectedChipAnchor = null


                }
            }
        }
    }

    private fun showChartChipPopup(
        highlight: Highlight?,
        anchor: View,
        xLabel: List<String>,
        value: String
    ) {
        currentChartSelectedChipAnchor?.let {
            (currentChartSelectedChipAnchor as Chart<*>).highlightValue(null)
        }
        chartSelectedChip.dismiss()
        val dataIndex = highlight!!.x.toInt()
        val label = xLabel[dataIndex]
        chartSelectedChipBinding.tvDate.text = label
        chartSelectedChipBinding.viewDot.isVisible = false
        chartSelectedChipBinding.tvValue.text = value
        currentChartSelectedChipAnchor = anchor
        (currentChartSelectedChipAnchor as Chart<*>).also {
            it.highlightValue(highlight)
        }

        chartSelectedChip.contentView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val xOff = highlight.xPx.toInt() - chartSelectedChip.contentView.measuredWidth / 2
        val yOff = -anchor.height - chartSelectedChip.contentView.measuredHeight


        chartSelectedChip.showAsDropDown(
            anchor,
            xOff,
            yOff,
        )

    }

    override fun onPause() {
        super.onPause()
        chartSelectedChip.dismiss()
    }
}