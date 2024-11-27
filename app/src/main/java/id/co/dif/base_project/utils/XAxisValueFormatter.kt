package id.co.dif.base_project.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisValueFormatter(private val labels: List<String>) : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val position = value.toInt()
        return if (position < labels.size) labels[position] else ""
    }
}