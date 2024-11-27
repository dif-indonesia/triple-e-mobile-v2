package id.co.dif.base_project.utils

import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class PercentValueFormatter: ValueFormatter() {

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {

        return "${(value * 10).roundToInt() / 10f}%"
    }
}