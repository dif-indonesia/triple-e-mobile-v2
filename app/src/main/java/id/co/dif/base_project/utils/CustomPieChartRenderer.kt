package id.co.dif.base_project.utils

import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.renderer.PieChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CustomPieChartRenderer(
    chart: PieChart?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : PieChartRenderer(chart, animator, viewPortHandler) {

    /// MATH!!!!!!!!!!!!!
    override fun drawValue(c: Canvas?, valueText: String, x: Float, y: Float, color: Int) {
        val centerX = mChart.centerCircleBox.x
        val centerY = mChart.centerCircleBox.y

        val dx = x - centerX
        val dy = y - centerY

        val angle = atan2(dy, dx)

        val newX = centerX + mChart.radius * 0.8f * cos(angle)
        val newY = centerY + mChart.radius * 0.8f * sin(angle)
        c?.drawText(valueText, newX, newY, mValuePaint)
    }
}
