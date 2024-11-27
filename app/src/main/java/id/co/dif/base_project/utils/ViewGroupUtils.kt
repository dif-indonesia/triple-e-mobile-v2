package id.co.dif.base_project.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible

object ViewGroupUtils {
    fun getParent(view: View): ViewGroup? {
        return view.getParent() as ViewGroup
    }

    fun removeView(view: View) {
        val parent = getParent(view)
        if (parent != null) {
            parent.removeView(view)
        }
    }

    fun replaceView(currentView: View, newView: View): View? {
        val parent = getParent(currentView) ?: return null
        val index = parent.indexOfChild(currentView)
        val lpCurr = currentView.layoutParams
        val lpNew = newView.layoutParams
        lpNew.width = lpCurr.width
        lpNew.height = lpCurr.height
        newView.layoutParams = lpNew
        currentView.isVisible = false
        parent.addView(newView, index)
        return currentView
    }
}