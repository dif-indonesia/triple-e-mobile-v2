package id.co.dif.base_project.utils

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.core.content.ContextCompat
import carbon.internal.ResourcesCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.model.GradientColor
import com.github.mikephil.charting.utils.ColorTemplate
import id.co.dif.base_project.R

fun Int.toChartXIndexedEntry(index: Int): Entry {
    return Entry(index.toFloat(), this.toFloat())
}

fun Int.toChartXIndexedBarEntry(index: Int): BarEntry {
    return BarEntry(index.toFloat(), this.toFloat())
}

fun Int.toChartPieEntry(label: String): PieEntry {
    return PieEntry(this.toFloat(), label)
}


fun LineChart.populateDottedCurvedLine(
    rawData: List<List<Int>>, dotColors: List<Int>, lineColors: List<Int>
) {

    val datasets = rawData.mapIndexed { index, list ->
        val entries = list.mapIndexed { i, int -> int.toChartXIndexedEntry(i) }
        LineDataSet(entries, "").also {
            it.setDrawValues(false)
            it.circleColors = listOf(ContextCompat.getColor(context, R.color.white))
            it.lineWidth = 3f
            it.mode = LineDataSet.Mode.CUBIC_BEZIER
            it.color = ContextCompat.getColor(context, lineColors[index])
            it.circleHoleColor = ContextCompat.getColor(context, dotColors[index])
            it.circleHoleRadius = 3f
            it.circleRadius = 5f
            it.setDrawHorizontalHighlightIndicator(false)
            it.enableDashedHighlightLine(10f, 4f, 0f)
            it.highLightColor = Color.BLACK
            it.highlightLineWidth = 1f
        }
    }


    val lineData = LineData(datasets)
    data = lineData
    animateY(1400, Easing.EaseInOutQuad)
    legend.isEnabled = false
    xAxis.isEnabled = false
    setVisibleXRangeMaximum(14f)
    axisLeft.isEnabled = false
    axisRight.isEnabled = false
    description.isEnabled = false
    setVisibleXRangeMaximum(14f)
    isDragYEnabled = false
    setViewPortOffsets(0f, 0f, 0f, 0f)
    invalidate()

}

fun LineChart.populateFadeUnderLine(
    rawData: List<List<Int>>, drawables: List<Int>, lineColors: List<Int>
) {

    val datasets = rawData.mapIndexed { index, list ->
        val entries = list.mapIndexed { i, int -> int.toChartXIndexedEntry(i) }
        LineDataSet(entries, "").also {
            it.mode = LineDataSet.Mode.CUBIC_BEZIER
            it.setDrawValues(false)
            it.lineWidth = 2f
            it.setDrawCircles(false)
            it.color = ContextCompat.getColor(context, lineColors[index])
            it.setDrawFilled(true)
            it.fillDrawable = ContextCompat.getDrawable(context, drawables[index])
            it.setDrawHorizontalHighlightIndicator(false)
            it.enableDashedHighlightLine(10f, 4f, 0f)
            it.highLightColor = Color.BLACK
            it.highlightLineWidth = 1f
        }
    }
    val lineData = LineData(datasets)
    data = lineData
    animateY(1400, Easing.EaseInOutQuad)
    legend.isEnabled = false
    xAxis.isEnabled = false
    axisLeft.isEnabled = false
    axisRight.gridColor = Color.parseColor("#0F000000")
    description.isEnabled = false
    setViewPortOffsets(0f, 0f, 0f, 0f)
    invalidate()

}

fun PieChart.populateFullPie(rawData: List<Int>, labels: List<String>, sliceColors: List<Int>) {

    val valueSet = rawData.mapIndexed { index, i -> i.toChartPieEntry(labels[index]) }
    val pieDataSet = PieDataSet(valueSet, "").also {
        it.valueTextSize = 14f
        it.colors = sliceColors.map { col -> ContextCompat.getColor(context, col) }
        it.sliceSpace = 3f
        it.valueTypeface = Typeface.DEFAULT_BOLD
        it.valueLineColor = ColorTemplate.COLOR_NONE
        it.valueTextColor = Color.WHITE
        it.valueTypeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
    }

    val pieData = PieData(pieDataSet).also {
        it.setDrawValues(true)
        it.setValueFormatter(PercentValueFormatter())
    }

    data = pieData
    legend.isEnabled = false
    description.isEnabled = false
    isRotationEnabled = true
    isDrawHoleEnabled = false
    setUsePercentValues(true)
    animateY(1400, Easing.EaseInOutQuad)
    setHoleColor(Color.WHITE)
    isRotationEnabled = false
    renderer = CustomPieChartRenderer(this, animator, viewPortHandler)
    setDrawEntryLabels(false)
    invalidate()

}

fun LineChart.populateDottedDashedSmoothLineChartWithLabel(
    rawData: List<List<Int>>,
    labels: List<String>,
    lineColors: List<Int>,
    dotColors: List<Int>,
    labelRotation: Float
) {
    val xLabels = labels.map { it }
    val datasets = rawData.mapIndexed { dataIndex, ints ->
        val valueSet = ints.mapIndexed { index, i -> i.toChartXIndexedEntry(index) }
        LineDataSet(valueSet, "").also {
            it.circleColors = listOf(ContextCompat.getColor(context, dotColors[dataIndex]))
            it.lineWidth = 3f
            it.color = ContextCompat.getColor(context, lineColors[dataIndex])
            it.setDrawCircleHole(false)
            it.circleRadius = 5f
            it.setDrawValues(false)
            it.mode = LineDataSet.Mode.CUBIC_BEZIER
            it.enableDashedLine(10f, 5f, 0f)
            it.enableDashedHighlightLine(10f, 4f, 0f)
            it.highLightColor = Color.BLACK
            it.highlightLineWidth = 1f

        }
    }


    val lineData = LineData(datasets)

    data = lineData
    xAxis.valueFormatter = XAxisValueFormatter(xLabels)
    xAxis.granularity = 1f
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.labelCount = labels.size - 1
    xAxis.setDrawGridLines(false)
    xAxis.labelRotationAngle = labelRotation
    extraBottomOffset = 20f
    legend.isEnabled = false
    setVisibleXRangeMaximum(14f)
    isDragYEnabled = false
    axisRight.isEnabled = false
    description.isEnabled = false
    axisLeft.gridColor = Color.parseColor("#0F000000")

    animateY(1000, Easing.EaseInOutQuad)
    invalidate()
}

fun BarChart.populateNakedBarChart(rawData: List<Int>) {

    val valueSet = rawData.mapIndexed { index, i -> i.toChartXIndexedBarEntry(index) }

    val dataset = BarDataSet(valueSet, null).also { it.setDrawValues(false) }
    val barData = BarData(dataset)
    data = barData
    animateXY(1000, 1000, Easing.EaseOutQuad)
    legend.isEnabled = false
    description.isEnabled = false
    axisLeft.isEnabled = false
    axisRight.isEnabled = false
    xAxis.isEnabled = false
    setViewPortOffsets(0f, 0f, 0f, 0f)
    setScaleEnabled(false)
    invalidate()
}

fun BarChart.populateVerticalFadeTopGroupedChart(
    rawData: List<List<Int>>,
    colors: List<Int>,
    tColors: List<Int>,
    labels: List<String>
) {
//

    val dataSets = rawData.mapIndexed { index, ints ->
        val valueSet = ints.mapIndexed { indexValues, i -> i.toChartXIndexedBarEntry(indexValues) }
        BarDataSet(valueSet, labels[index]).also {
            it.gradientColors = listOf(
                GradientColor(
                    ContextCompat.getColor(context, tColors[index]),
                    ContextCompat.getColor(context, colors[index])
                )
            )
            it.setDrawValues(false)
        }
    }


    val groupSpace = 0.55f
    val barWidth = 0.1f
    val barSpace = 0.05f

    // barSpace*dataCount + barWidth*dataCount + groupSpace  = 1.00
    data = BarData(dataSets)
    legend.isEnabled = false
    axisLeft.axisMinimum = 0f

    data.barWidth = barWidth
    xAxis.valueFormatter = IndexAxisValueFormatter(labels)
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.granularity = 1f
    xAxis.setCenterAxisLabels(true)
    xAxis.isGranularityEnabled = true
    xAxis.labelRotationAngle = 315f
    xAxis.axisMinimum = 0f
    xAxis.axisMaximum =
        0 + barData.getGroupWidth(groupSpace, barSpace) * labels.size
    xAxis.labelCount = 13

    isDragYEnabled = false
    legend.isEnabled = false
    axisRight.isEnabled = false
    description.isEnabled = false
    axisLeft.gridColor = Color.parseColor("#0F000000")
    xAxis.setDrawGridLines(false)
    axisRight.isEnabled = false
    groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping
    invalidate()

}

fun BarChart.populateHorizontalRoundedGroupedChart(
    rawData: List<List<Int>>,
    colors: List<Int>,
    labels: List<String>
) {
    if(labels.isEmpty()) return
    val dataSets = rawData.mapIndexed { index, ints ->
        val valueSet = ints.mapIndexed { indexValues, i -> i.toChartXIndexedBarEntry(indexValues) }
        BarDataSet(valueSet, "").also {
            it.color = ContextCompat.getColor(context, colors[index])
            it.valueTextColor = Color.WHITE
            it.valueTextSize = 12f
            it.valueTypeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
            it.valueFormatter = BarChartValueFormatter()
        }
    }

    val barSpace = 0.083f
    val barWidth = 0.2f
    val groupSpace = 0.15f

    // barSpace*dataCount + barWidth*dataCount + groupSpace  = 1.00
    data = BarData(dataSets)
    legend.isEnabled = false
    axisLeft.axisMinimum = 0f
    renderer = HorizontalRoundedBarChartRenderer(
        this,
        animator,
        viewPortHandler,
        20
    )
    data.barWidth = barWidth
    xAxis.valueFormatter = IndexAxisValueFormatter(labels)
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.granularity = 1f
    xAxis.setCenterAxisLabels(true)
    xAxis.isGranularityEnabled = true
    xAxis.axisMinimum = 0f
    xAxis.axisMaximum =
        0 + barData.getGroupWidth(groupSpace, barSpace) * labels.size
    xAxis.labelCount = 13
    xAxis.typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
    xAxis.setDrawAxisLine(false)
    legend.isEnabled = false
    setDrawValueAboveBar(false)

    axisRight.isEnabled = false
    description.isEnabled = false
    axisLeft.isEnabled = false
    xAxis.setDrawGridLines(true)
    axisRight.isEnabled = false
    groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping
    invalidate()

}


