package id.co.dif.base_project.presentation.activity

import GridSpacingItemDecoration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.PieChartLegend
import id.co.dif.base_project.data.PlainValueLabel
import id.co.dif.base_project.data.UsageRank
import id.co.dif.base_project.databinding.ActivityTeamDashboardBinding
import id.co.dif.base_project.presentation.adapter.PieChartLegendAdapter
import id.co.dif.base_project.presentation.adapter.TopUsersAdapter
import id.co.dif.base_project.presentation.adapter.UsageRankAdapter
import id.co.dif.base_project.utils.ColorGenerator
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.TrendType
import id.co.dif.base_project.utils.XAxisValueFormatter
import id.co.dif.base_project.utils.populateFullPie
import id.co.dif.base_project.viewmodel.TeamDashboardViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Random

class TeamDashboardActivity : BaseActivity<TeamDashboardViewModel, ActivityTeamDashboardBinding>(),
    KoinComponent {
    override val layoutResId: Int = R.layout.activity_team_dashboard
    private val random = Random()

    private val colorGen: ColorGenerator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getHourlyTeamUsageTrend()
        viewModel.getUsersInfoDashboard()
        viewModel.getUsageTrend(TrendType.DAILY)
        viewModel.getTopUsers()
        viewModel.getAreasUsageData()
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.rootLayout.onBackButtonClicked =  {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.areasUsageData.observe(lifecycleOwner) {
            if (it is Resource.Success) {
                it.data?.let { res ->
                    if (res.status in StatusCode.SUCCESS) {
                        populateAreasUsageChart(res.data.list)
                    }
                }
            }
        }

        viewModel.hourlyUsageTrend.observe(lifecycleOwner) {
            when (it) {
                is Resource.Error -> {
//                    setUsageTrendLoading(false)
                }

                is Resource.Loading -> {
//                    setUsageTrendLoading(true)
                }

                is Resource.Success -> {
                    it.data?.let { res ->
                        if (res.status in StatusCode.SUCCESS) {
                            populateHourlyUsageChart(res.data.list)
                        }
                    }
//                    setUsageTrendLoading(false)
                }
            }

        }
        viewModel.topUsers.observe(lifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { res ->
                        if (res.status in StatusCode.SUCCESS) {
                            binding.rvTopUsers.adapter = TopUsersAdapter(res.data.list)
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.usersInfoDashboard.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                binding.usersInfoDashboard = it.data
            }

        }

        viewModel.usageTrend.observe(lifecycleOwner) {

            when (it) {
                is Resource.Error -> {
                    setUsageTrendLoading(false)
                }

                is Resource.Loading -> {
                    setUsageTrendLoading(true)
                }

                is Resource.Success -> {
                    it.data?.let { res ->
                        if (res.status in StatusCode.SUCCESS) {
                            populateUsageTrendChart(res.data.list)
                        }
                    }
                    setUsageTrendLoading(false)
                }
            }

        }

        binding.radioGroupUsageTrend.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_item_daily -> viewModel.getUsageTrend(TrendType.DAILY)
                R.id.radio_item_monthly -> viewModel.getUsageTrend(TrendType.MONTHLY)
                R.id.radio_item_yearly -> viewModel.getUsageTrend(TrendType.YEARLY)
            }
        }

        binding.radioGroupDailyTicketTrend.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_item_yearly_daily_ticket_trend -> populateDailyTicketTrend(mutableListOf())
                R.id.radio_item_monthly_daily_ticket_trend -> populateDailyTicketTrend(mutableListOf())
            }
        }



        populateUsageRank()
        populateDailyTicketTrend(mutableListOf())
    }


    private fun setUsageTrendLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerLoadingUsageTrendChart.visibility = View.VISIBLE
            binding.chartUsageTrend.visibility = View.GONE
        } else {
            binding.shimmerLoadingUsageTrendChart.visibility = View.GONE
            binding.chartUsageTrend.visibility = View.VISIBLE
        }
    }

    private fun populateDailyTicketTrend(data: MutableList<Int>) {
        repeat(50) {
            data.add(random.nextInt())
        }
        val valueSet = data.mapIndexed { index, value ->
            Entry(
                index.toFloat(), value.toFloat()
            )
        }

        val lineDataset1 = LineDataSet(valueSet, "").also {
            it.mode = LineDataSet.Mode.CUBIC_BEZIER;
            it.setDrawValues(false)
            it.lineWidth = 3f
            it.setDrawCircles(false)
            it.color = ContextCompat.getColor(application, R.color.light_blue)
            it.setDrawFilled(true)
            it.fillDrawable = ContextCompat.getDrawable(application, R.drawable.fade_light_blue);

        }

        val datasets = listOf(lineDataset1)
        val lineData = LineData(datasets)
        binding.chartDailyTicketTrend.also {
            it.data = lineData
            it.animateY(1400, Easing.EaseInOutQuad)
            it.legend.isEnabled = false
            it.xAxis.isEnabled = false
            it.axisLeft.isEnabled = false
            it.axisRight.gridColor = Color.parseColor("#0F000000")
            it.description.isEnabled = false
            it.setTouchEnabled(false)
            it.setViewPortOffsets(0f, 0f, 0f, 0f);
            it.invalidate()
        }
    }


    private fun populateUsageTrendChart(data: List<PlainValueLabel>) {
        val xLabels = mutableListOf<String>()
        val values = mutableListOf<Int>()
        data.forEach {
            xLabels.add(it.label)
            values.add(it.count)
        }

        val valueSet = values.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val lineDataset1 = LineDataSet(valueSet, "").also {
            it.circleColors = listOf(Color.parseColor("#4FDFCE"))
            it.lineWidth = 3f
            it.color = Color.parseColor("#FFB959")
            it.setDrawCircleHole(false)
            it.circleRadius = 5f
            it.setDrawValues(false)
            it.mode = LineDataSet.Mode.CUBIC_BEZIER
            it.enableDashedLine(10f, 5f, 0f)

        }

        val datasets = listOf(lineDataset1)
        val lineData = LineData(datasets)
        binding.chartUsageTrend.also {
            it.data = lineData
            it.xAxis.valueFormatter = XAxisValueFormatter(xLabels)
            it.xAxis.granularity = 0f
            it.xAxis.position = XAxis.XAxisPosition.BOTTOM
            it.xAxis.labelCount = data.size - 1
            it.xAxis.setDrawGridLines(false)
            it.legend.isEnabled = false
            it.axisRight.isEnabled = false
            it.description.isEnabled = false
            it.axisLeft.gridColor = Color.parseColor("#0F000000")
            it.isDragEnabled = false
            it.setScaleEnabled(false)
            it.isDragEnabled = false
            it.animateY(1000, Easing.EaseInOutQuad)
            it.invalidate()
        }
    }

    private fun populateUsageRank() {
        val data = mutableListOf<UsageRank>()
        for (i in 0..10) {
            val id = (Math.random() * Long.MAX_VALUE).toLong()
            data.add(
                UsageRank(
                    username = "Mirza My Humayung #$id",
                    organization = "Teleglobal",
                    position = i + 1,
                    profileCompletion = (Math.random() * 2000).toInt().toString()
                )
            )
        }
        val adapter = UsageRankAdapter(data)
        binding.rvUsageRank.adapter = adapter
    }

    private fun populateHourlyUsageChart(data: List<Int>) {
        val valueSet = data.mapIndexed { index, value ->
            Entry(
                index.toFloat(), value.toFloat()
            )
        }
        val lineDataset1 = LineDataSet(valueSet, "").also {
            it.setDrawValues(false)
            it.circleColors = listOf(ContextCompat.getColor(application, R.color.white))
            it.lineWidth = 3f
            it.color = ContextCompat.getColor(application, R.color.light_green)
            it.setDrawCircleHole(true)
            it.circleHoleColor = ContextCompat.getColor(application, R.color.light_orange)
            it.circleRadius = 4f
        }

        val datasets = listOf(lineDataset1)
        val lineData = LineData(datasets)
        binding.chartDailyUsageTrend.also {
            it.data = lineData
            it.animateY(1400, Easing.EaseInOutQuad)
            it.legend.isEnabled = false
            it.xAxis.isEnabled = false
            it.axisLeft.isEnabled = false
            it.axisRight.gridColor = Color.parseColor("#0F000000")
            it.description.isEnabled = false
            it.setScaleEnabled(false)
            it.setViewPortOffsets(0f, 0f, 0f, 0f);
            it.invalidate()
        }
    }


    private fun populateAreasUsageChart(data: List<PlainValueLabel>) {
        val labels = data.map { it.label }
        val pieColors = labels.map { colorGen.getColor(it) }
        val values = data.map { it.count }

        binding.chartAreasUsage.populateFullPie(
            sliceColors = pieColors,
            rawData = values,
            labels = labels
        )

        val legendsData = labels.mapIndexed { index, label ->
            PieChartLegend(
                color = pieColors[index],
                label = label
            )
        }
        val adapter = PieChartLegendAdapter(legendsData)
        binding.rvAreasUsageLegend.adapter = adapter
        binding.rvAreasUsageLegend.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 3,
                spacing = 16,
                includeEdge = false
            )
        )

    }
}