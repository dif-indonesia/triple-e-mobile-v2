package id.co.dif.base_project.custom_view

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import id.co.dif.base_project.R
import java.lang.Exception

class LinearLayoutWithAppBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {
    var onBackButtonClicked: () -> Unit = {}
    var onActionButtonClicked: () -> Unit = {}
    var title: String = "Title"
        set(value) {
            titleTextView.text = value
            field = value
        }
    var titleTextView: TextView
    var barView: ViewGroup

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.custom_view_linear_layout_with_appbar, this, true)

        titleTextView = findViewById(R.id.tv_title)
        barView = findViewById(R.id.bar)
        val actionButton: ImageView = findViewById(R.id.btn_action)
        val backButton: ImageView = findViewById(R.id.btn_back)
        val tvAction: TextView = findViewById(R.id.tv_action)
        onBackButtonClicked = {
            (context as Activity).onBackPressed()
        }
        backButton.setOnClickListener {
            onBackButtonClicked()
        }
        actionButton.setOnClickListener {
            onActionButtonClicked()
        }
        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.LinearLayoutWithAppBar)
            val titleText = typedArray.getString(R.styleable.LinearLayoutWithAppBar_title)
            val actionText = typedArray.getString(R.styleable.LinearLayoutWithAppBar_actionButtonText)
            val actionButtonIcon = typedArray.getResourceId(
                R.styleable.LinearLayoutWithAppBar_actionButtonIcon,
                0
            )
            val backButtonIcon = typedArray.getResourceId(
                R.styleable.LinearLayoutWithAppBar_backButtonIcon,
                0
            )
            val actionButtonTint = typedArray.getColor(
                R.styleable.LinearLayoutWithAppBar_actionButtonTint,
                Color.BLACK
            )
            val backButtonTint = typedArray.getColor(
                R.styleable.LinearLayoutWithAppBar_backButtonTint,
                Color.BLACK
            )
            val barTint = typedArray.getColor(
                R.styleable.LinearLayoutWithAppBar_barTint,
                Color.WHITE
            )
            val titleTextColor = typedArray.getColor(
                R.styleable.LinearLayoutWithAppBar_titleTextColor,
                Color.BLACK
            )
            typedArray.recycle()
            titleTextView.text = titleText

            titleTextView.setTextColor(titleTextColor)
            barView.setBackgroundColor(barTint)
            tvAction.text = actionText
            if (actionButtonIcon == 0) {
                actionButton.isVisible = false
            } else {
                actionButton.setImageResource(actionButtonIcon)
                actionButton.setColorFilter(actionButtonTint)
            }

            if (backButtonIcon == 0) {
                backButton.isVisible = false
            } else {
                backButton.setImageResource(backButtonIcon)
                backButton.setColorFilter(backButtonTint)
            }
        }
    }
}