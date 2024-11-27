package id.co.dif.base_project.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.utils.setupWindowBlur

class LoadingDialog(
    context: Context
) : Dialog(context) {
    var onBtnCancelClicked: () -> Unit = {}
    lateinit var btnCancel: TextView
    private val looper: Looper = Looper.getMainLooper()
    val handler = Handler(looper)
    private val hideLoadingRunnable = Runnable {
        btnCancel.isVisible = true
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )


        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window?.setDimAmount(0.4f)
            window?.setupWindowBlur(10)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        btnCancel = findViewById(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            onBtnCancelClicked()
        }
    }


    override fun show() {
        super.show()
        btnCancel.isVisible = false
        handler.removeCallbacks(hideLoadingRunnable)
        handler.postDelayed(hideLoadingRunnable, 2500)
    }


}