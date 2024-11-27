package id.co.dif.base_project.utils

import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt


// don't draw if value is 0.
// This is useful when we set the drawAboveBar label is false
// so the values don't overlap with the labels
class BarChartValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value > 0) value.roundToInt().toString() else ""
    }
}