package id.co.dif.base_project.presentation.fragment

import GridSpacingItemDecoration
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.DataList
import id.co.dif.base_project.data.AreaBenchmark
import id.co.dif.base_project.data.Employee
import id.co.dif.base_project.data.PieChartLegend
import id.co.dif.base_project.data.PlainValueLabel
import id.co.dif.base_project.data.ProgressBarConclusion
import id.co.dif.base_project.data.TicketTrend
import id.co.dif.base_project.data.TroubleTicketTicketInfo
import id.co.dif.base_project.data.TtTimeToClosed
import id.co.dif.base_project.data.UserOverallRank
import id.co.dif.base_project.databinding.FragmentTtDashboardBinding
import id.co.dif.base_project.databinding.ItemChartSelectedPopupBinding
import id.co.dif.base_project.presentation.adapter.PieChartLegendAdapter
import id.co.dif.base_project.presentation.adapter.UserOverallRankAdapter
import id.co.dif.base_project.utils.ColorGenerator
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.hideSoftKeyboard
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.populateDottedCurvedLine
import id.co.dif.base_project.utils.populateDottedDashedSmoothLineChartWithLabel
import id.co.dif.base_project.utils.populateFadeUnderLine
import id.co.dif.base_project.utils.populateFullPie
import id.co.dif.base_project.utils.populateHorizontalRoundedGroupedChart
import id.co.dif.base_project.utils.populateVerticalFadeTopGroupedChart
import id.co.dif.base_project.viewmodel.TTDashboardViewModel
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToInt

class TTDashboardFragment() :
    BaseFragment<TTDashboardViewModel, FragmentTtDashboardBinding>(), KoinComponent {
    override val layoutResId: Int
        get() = R.layout.fragment_tt_dashboard
    private val colorGen: ColorGenerator by inject()
    private lateinit var chartSelectedChip: PopupWindow
    private lateinit var chartSelectedChipBinding: ItemChartSelectedPopupBinding
    private var currentChartSelectedChipAnchor: View? = null
    private var overallRankAdapter: UserOverallRankAdapter = UserOverallRankAdapter(listOf())
    var checkEmployee = { -> showToast("Select employee first!") }

    override fun onResume() {
        super.onResume()
        val myProfileId = preferences.myDetailProfile.value?.id
        fetchData(myProfileId)
    }

    private fun fetchData(userId: Int?) {
        Log.w("TAG", "fetchData: $userId")
        viewModel.cancelAllJobs()
        overallRankAdapter = UserOverallRankAdapter(listOf())
        viewModel.fetchChartsData(userId)
//        findViewsByType(binding.root, Chart::class.java).onEach { it.invalidate() }
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.btnDailyTicketTrendCheck.setOnClickListener {
            checkEmployee()
        }
        setupAutoCompleteEmployeeName(preferences.employeeList.value ?: listOf())
        viewModel.responseListName.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                preferences.employeeList.value = it.data.list
                setupAutoCompleteEmployeeName(it.data.list)
            }
        }
        binding.radioGroupTtTimeToClosed.setOnCheckedChangeListener { group, checkedId ->
            val myProfileId = preferences.myDetailProfile.value?.id
            lifecycleScope.launch {
                when (checkedId) {
                    binding.radioItemMonthly.id -> {
                        viewModel.getTtTimeToClosedData(
                            id = myProfileId,
                            timeFrame = "monthly"
                        )

                    }

                    binding.radioItemYearly.id -> {
                        viewModel.getTtTimeToClosedData(
                            id = myProfileId,
                            timeFrame = "yearly"
                        )
                    }
                }
            }
        }
        viewModel.getListName(limit = -1)
        setupChartSelectedChip()
        binding.rvOverallRank.adapter = overallRankAdapter

        binding.tvValueEmployeeName.setText("${session?.name}")
        binding.etDailyTicketTrendInput.setText(session?.name)
        binding.tvValueInstitution.setText("${session?.emp_position}")

        viewModel.overallRank.observe(lifecycleOwner) {
            populateUserOverallRank(it)
        }


        viewModel.areaBenchmarkData.observe(lifecycleOwner) { res ->
            evaluateResponse(res, listOf(binding.shimmerAreaBenchmark)) {
                populateAreaBenchmarkChart(it)
            }
        }

        viewModel.ttTicketInfo.observe(lifecycleOwner) { res ->
            evaluateResponse(res, null) {
                consumeTtTicketInfo(it)
            }
        }

        viewModel.ticketTrends.observe(lifecycleOwner) { res ->
            evaluateResponse(
                res, listOf(
                    binding.shimmerTicketNew,
                    binding.shimmerTicketOngoing,
                    binding.shimmerTicketClosed,
                    binding.shimmerTicketRate,
                    binding.shimmerWeeklyTicketComparison,
                    binding.shimmerTroubleTicket,
                )
            ) {
                val (ongoing, closed, new, labels) = it


                populateClosedTicketChart(closed, labels)
                populateOngoingTicketChart(ongoing, labels)
                populateNewTicketChart(new, labels)
                populateRateTicketChart(closed, ongoing, labels)
                populateTicketComparison(it)
                populateTroubleTicketChart(it)
            }
        }

        viewModel.dailyTicketTrend.observe(lifecycleOwner) { res ->
            evaluateResponseList(res, binding.shimmerDailyTicketTrend) {
                populateDailyTicketTrend(it.list)
            }
        }


        viewModel.ttTimeToClosedData.observe(lifecycleOwner) { res ->
            evaluateResponse(res, listOf(binding.shimmerTtTimeToClosed)) {
                populateTtTimeToClosedChart(it)
            }
        }
    }

    private fun <T> evaluateResponse(resource: Resource<BaseResponse<T>>, loadingView: List<View>?, onSuccess: (T) -> Unit) {
        when (resource) {
            is Resource.Error -> {
                loadingView?.onEach { it.isVisible = false }
            }

            is Resource.Loading -> {
                loadingView?.onEach { it.isVisible = true }
            }

            is Resource.Success -> {
                loadingView?.onEach { it.isVisible = false }
                resource.data?.let { res ->
                    if (res.status in StatusCode.SUCCESS) {
                        onSuccess(res.data)
                    }
                }
            }
        }
    }

    private fun <T> evaluateResponseList(resource: Resource<BaseResponseList<T>>, loadingView: View?, onSuccess: (DataList<T>) -> Unit) {
        when (resource) {
            is Resource.Error -> loadingView?.isVisible = false
            is Resource.Loading -> loadingView?.isVisible = true
            is Resource.Success -> {
                loadingView?.isVisible = false
                resource.data?.let { res ->
                    if (res.status in StatusCode.SUCCESS) {
                        onSuccess(res.data)
                    }
                }
            }
        }
    }

    private fun setupAutoCompleteEmployeeName(employees: List<Employee>) {
        val adapter: ArrayAdapter<Employee> = ArrayAdapter<Employee>(
            currentActivity,
            R.layout.item_spinner_dropdown,
            employees
        )

        binding.etDailyTicketTrendInput.setAdapter(adapter)
        binding.etDailyTicketTrendInput.setOnItemClickListener { _, _, position, _ ->
            hideSoftKeyboard(currentActivity)
            binding.etDailyTicketTrendInput.clearFocus()
            val employee = adapter.getItem(position)
            employee?.let {
                checkEmployee = {
                    Log.w("TAG", "setupAutoCompleteEmployeeName: ${it.employeeId}")
                    fetchData(it.employeeId)
                    binding.tvValueEmployeeName.text = it.employeeName
                }
            }
        }
    }

    private fun populateUserOverallRank(response: Resource<BaseResponseList<UserOverallRank>>?) {
        when (response) {
            is Resource.Error -> {
                overallRankAdapter.removeLoadingFooter()
            }

            is Resource.Loading -> {
                overallRankAdapter.addLoadingFooter()
            }

            is Resource.Success -> {
                overallRankAdapter.removeLoadingFooter()
                response.data?.let { response ->
                    if (response.status in StatusCode.SUCCESS) {
                        val data = response.data.list
                        overallRankAdapter.addAll(data.toMutableList())
                    }
                }

            }

            else -> Unit
        }
    }

    private fun populateAreaBenchmarkChart(areaBenchmark: AreaBenchmark) {
        val colors = listOf(R.color.teal_200, R.color.pink, R.color.light_blue)
        binding.chartAreaBenchmark.populateHorizontalRoundedGroupedChart(
            rawData = listOf(
                areaBenchmark.values.new, areaBenchmark.values.onGoing, areaBenchmark.values.closed
            ),
            colors = colors,
            labels = areaBenchmark.labels
        )

        binding.chartAreaBenchmark.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                h?.dataSetIndex.log("index")
                var yLabel = ""
                var chipColor = R.color.light_orange
                when (h?.dataSetIndex) {
                    0 -> {
                        yLabel = "New: %s"
                        chipColor = colors[0]
                    }

                    1 -> {
                        yLabel = "Ongoing: %s"
                        chipColor = colors[1]
                    }

                    2 -> {
                        yLabel = "Closed: %s"
                        chipColor = colors[2]
                    }
                }
                showChartChipPopup(
                    entry = e,
                    highlight = h,
                    anchor = binding.chartAreaBenchmark,
                    chipColor = chipColor,
                    xLabel = areaBenchmark.labels,
                    xLabelTitle = yLabel
                )

            }

            override fun onNothingSelected() {
                chartSelectedChip.dismiss()
            }

        })
    }

    private fun consumeTtTicketInfo(data: TroubleTicketTicketInfo) {
        binding.ttTicketInfo = data
        binding.tvValueClosed.text = data.closed
        binding.tvValueOngoing.text = data.onGoing
        binding.tvValueNew.text = data.new
        binding.tvValueRate.text = data.rate
    }

    private fun populateNewTicketChart(new: List<Int>, labels: List<String>) {
        val formattedLabels = labels.mapNotNull { parseDateChartChip(it) }

        binding.chartTicketNew.populateFadeUnderLine(
            rawData = listOf(new),
            lineColors = listOf(R.color.pink),
            drawables = listOf(R.drawable.fade_pink)
        )
        binding.chartTicketNew.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                showChartChipPopup(
                    entry = e,
                    highlight = h,
                    anchor = binding.chartTicketNew,
                    chipColor = R.color.pink,
                    xLabel = formattedLabels
                )

            }

            override fun onNothingSelected() {
                chartSelectedChip.dismiss()
            }

        })
    }

    private fun setupChartSelectedChip() {
        chartSelectedChipBinding =
            ItemChartSelectedPopupBinding.inflate(layoutInflater, null, false)
        chartSelectedChip = PopupWindow(
            chartSelectedChipBinding.root,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        chartSelectedChipBinding.cardItemChartSelectedPopup.setOnClickListener {
            (currentChartSelectedChipAnchor as LineChart).highlightValues(null)
            chartSelectedChip.dismiss()
        }
        // Dismiss if the chip goes out of the visible screen
        binding.root.viewTreeObserver.addOnScrollChangedListener {
            currentChartSelectedChipAnchor?.let {
                val scrollBounds = Rect()
                binding.parent.getHitRect(scrollBounds)
                if (it.getLocalVisibleRect(scrollBounds).not() && chartSelectedChip.isShowing) {
                    chartSelectedChip.dismiss()
                    (currentChartSelectedChipAnchor as Chart<*>).highlightValue(null)
                    currentChartSelectedChipAnchor = null
                }
            }
        }
    }

    private fun populateOngoingTicketChart(ongoing: List<Int>, labels: List<String>) {
        val formattedLabels = labels.mapNotNull { parseDateChartChip(it) }
        binding.chartTicketOngoing.populateFadeUnderLine(
            rawData = listOf(ongoing),
            lineColors = listOf(R.color.light_blue),
            drawables = listOf(R.drawable.fade_light_blue)
        )
        binding.chartTicketOngoing.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                showChartChipPopup(
                    entry = e,
                    highlight = h,
                    anchor = binding.chartTicketOngoing,
                    chipColor = R.color.light_blue,
                    xLabel = formattedLabels
                )
            }

            override fun onNothingSelected() {
                chartSelectedChip.dismiss()
            }
        })
    }

    private fun updateChipPopupPosition(offsetX: Float, offsetY: Float) {

    }

    private fun showChartChipPopup(
        entry: Entry?,
        highlight: Highlight?,
        anchor: View,
        chipColor: Int,
        xLabel: List<String>,
        yLabelTitle: String = "%s",
        xLabelTitle: String = "Ticket: %s"
    ) {
        val marker = MarkerView(context, R.layout.item_selected_chart_dot).also {
            it.setOffset(-it.width / 2f, -it.height / 2f)
            it.rootView.findViewById<FrameLayout>(R.id.just_dot).backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), chipColor)
        }

        currentChartSelectedChipAnchor?.let {
            (currentChartSelectedChipAnchor as Chart<*>).highlightValue(null)
        }
        chartSelectedChip.dismiss()
        val dataIndex = highlight!!.x.toInt()
        val label = xLabel[dataIndex]
        chartSelectedChipBinding.tvDate.text = String.format(yLabelTitle, label)
        chartSelectedChipBinding.tvValue.text = String.format(xLabelTitle, entry!!.y.roundToInt().toString())
        chartSelectedChipBinding.viewDot.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), chipColor)
        currentChartSelectedChipAnchor = anchor
        (currentChartSelectedChipAnchor as Chart<*>).also {
            it.highlightValue(highlight)
            it.marker = marker
        }

        chartSelectedChip.showAsDropDown(
            anchor,
            highlight.xPx.toInt() - chartSelectedChip.contentView.width,
            -anchor.height + highlight.yPx.toInt(),
        )

    }

    override fun onPause() {
        super.onPause()
        chartSelectedChip.dismiss()
    }

    private fun parseDateChartChip(input: String): String? {
        val outputFormat = DateTimeFormat.forPattern("dd MMMM yyyy")
        val date = try {
            val inputFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
            LocalDate.parse(input, inputFormat)
        } catch (e: Exception) {
            val inputFormat = DateTimeFormat.forPattern("dd-MM-yyyy")
            LocalDate.parse(input, inputFormat)
        }
        return outputFormat.print(date)
    }

    private fun parseDateChartTroubleTicket(input: String): String? {
        val outputFormat = DateTimeFormat.forPattern("dd-MM")
        val date = try {
            val inputFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
            LocalDate.parse(input, inputFormat)
        } catch (e: Exception) {
            val inputFormat = DateTimeFormat.forPattern("dd-MM-yyyy")
            LocalDate.parse(input, inputFormat)
        }
        return outputFormat.print(date)
//        return input
    }

    private fun populateClosedTicketChart(closed: List<Int>, labels: List<String>) {
        val formattedLabels = labels.mapNotNull { parseDateChartChip(it) }
        binding.chartTicketClosed.populateFadeUnderLine(
            rawData = listOf(closed),
            lineColors = listOf(R.color.red),
            drawables = listOf(R.drawable.fade_red)
        )


        binding.chartTicketClosed.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(entry: Entry?, highlighted: Highlight?) {
                showChartChipPopup(
                    entry = entry,
                    highlight = highlighted,
                    anchor = binding.chartTicketClosed,
                    chipColor = R.color.red,
                    xLabel = formattedLabels
                )
            }

            override fun onNothingSelected() {
                chartSelectedChip.dismiss()
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun populateRateTicketChart(closed: List<Int>, new: List<Int>, labels: List<String>) {
        val formattedLabels = labels.mapNotNull { parseDateChartChip(it) }

        binding.chartTicketRate.populateFadeUnderLine(
            rawData = listOf(closed, new),
            lineColors = listOf(R.color.light_blue, R.color.dark_purple),
            drawables = listOf(R.drawable.fade_light_blue, R.drawable.fade_dark_purple)
        )

        binding.chartTicketRate.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                showChartChipPopup(
                    entry = e,
                    highlight = h!!,
                    anchor = binding.chartTicketRate,
                    chipColor = if (h.dataSetIndex == 0) R.color.light_blue else R.color.dark_purple,
                    xLabel = formattedLabels
                )
            }

            override fun onNothingSelected() {
                chartSelectedChip.dismiss()
            }

        })
    }

    private fun populateTicketComparison(data: TicketTrend) {
        val formattedLabels = data.labels.mapNotNull { parseDateChartChip(it) }
        binding.chartWeeklyTicketComparison.populateFadeUnderLine(
            rawData = listOf(data.new, data.closed, data.ongoing),
            lineColors = listOf(R.color.teal_200, R.color.pink, R.color.light_blue),
            drawables = listOf(
                R.drawable.fade_teal_200, R.drawable.fade_pink, R.drawable.fade_light_blue
            )
        )

        binding.chartWeeklyTicketComparison.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val dotColor = when (h!!.dataSetIndex) {
                    0 -> R.color.teal_200
                    1 -> R.color.pink
                    2 -> R.color.light_blue
                    else -> throw Error("Dataindex is unknown ${h.dataSetIndex}")
                }
                showChartChipPopup(
                    entry = e,
                    highlight = h,
                    anchor = binding.chartWeeklyTicketComparison,
                    chipColor = dotColor,
                    xLabel = formattedLabels
                )

            }

            override fun onNothingSelected() {
                chartSelectedChip.dismiss()
            }

        })

    }

    private fun populateTtTimeToClosedChart(data: TtTimeToClosed) {
        binding.layoutTtTimeToClosedNoDataLabel.isVisible = false
        val pieValues = data.pieChart.map { it.count }
        val labels = data.pieChart.map { it.label }
        val sliceColors = labels.map { colorGen.randomColor }
        val legends = labels.mapIndexed { index, value ->
            PieChartLegend(
                ContextCompat.getColor(requireContext(), sliceColors[index]), value
            )
        }
        binding.chartTtTimeToClosed.populateFullPie(
            rawData = pieValues, labels = labels, sliceColors = sliceColors
        )
        val adapter = PieChartLegendAdapter(legends)
        binding.rvTtTimeToClosedLegend.adapter = adapter
        binding.rvTtTimeToClosedLegend.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 3,
                spacing = 16,
                includeEdge = false
            )
        )

        binding.chartTtTimeToClosed.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val selectedLabel = (e as PieEntry).label
                adapter.highlight(selectedLabel)
            }

            override fun onNothingSelected() {
                adapter.removeHighlight()
                chartSelectedChip.dismiss()
            }

        })
    }

    private fun populateDailyTicketTrend(data: List<PlainValueLabel>) {
        val labels = data.mapNotNull { parseDateChartChip(it.label) }
        binding.chartDailyTicketTrend.populateDottedDashedSmoothLineChartWithLabel(
            rawData = listOf(data.map { it.count }),
            labels = data.mapNotNull { parseDateChartTroubleTicket(it.label) },
            dotColors = listOf(R.color.teal_200),
            lineColors = listOf(R.color.light_orange),
            labelRotation = 315f
        )

        binding.chartDailyTicketTrend.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                showChartChipPopup(
                    entry = e,
                    highlight = h,
                    anchor = binding.chartDailyTicketTrend,
                    chipColor = R.color.light_orange,
                    xLabel = labels
                )

            }

            override fun onNothingSelected() {
                chartSelectedChip.dismiss()
            }

        })
    }

    private fun populateTroubleTicketChart(ttTicketTrend: TicketTrend) {
        val labels = ttTicketTrend.labels.mapNotNull { parseDateChartTroubleTicket(it) }
        val colors = listOf(
            R.color.teal_200,
            R.color.pink,
            R.color.light_blue
        )
        binding.chartTroubleTicket.populateVerticalFadeTopGroupedChart(
            rawData = listOf(
                ttTicketTrend.new,
                ttTicketTrend.ongoing,
                ttTicketTrend.closed
            ),
            colors = colors,
            tColors = listOf(
                R.color.alpha_60_teal_200,
                R.color.alpha_60_pink,
                R.color.alpha_60_light_blue
            ),
            labels = labels
        )


        binding.chartTroubleTicket.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                h?.dataSetIndex.log("index")
                var yLabel = ""
                var chipColor = R.color.light_orange
                when (h?.dataSetIndex) {
                    0 -> {
                        yLabel = "New: %s"
                        chipColor = colors[0]
                    }

                    1 -> {
                        yLabel = "Ongoing: %s"
                        chipColor = colors[1]
                    }

                    2 -> {
                        yLabel = "Closed: %s"
                        chipColor = colors[2]
                    }
                }
                showChartChipPopup(
                    entry = e,
                    highlight = h,
                    anchor = binding.chartTroubleTicket,
                    chipColor = chipColor,
                    xLabel = labels,
                    xLabelTitle = yLabel
                )

            }

            override fun onNothingSelected() {
                chartSelectedChip.dismiss()
            }

        })

    }


}