package id.co.dif.base_project.utils

import android.graphics.Rect
import android.view.View
import androidx.compose.ui.window.isPopupLayout
import androidx.recyclerview.widget.RecyclerView

class LinearSpacingItemDecoration(
    val top: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
    val right: Int = 0,
    val topMost: Int? = null,
    val bottomMost: Int? = null
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter
        adapter?.let {
            val position = parent.getChildLayoutPosition(view)
            outRect.left = left.toDp
            outRect.right = right.toDp
            outRect.top = top.toDp
            outRect.bottom = bottom.toDp
            if (position == adapter.itemCount - 1) {
                bottomMost?.let { outRect.bottom = it.toDp }
            }
            if (position == 0) {
                topMost?.let{
                    outRect.top = topMost.toDp
                }
            }
        }
    }
}