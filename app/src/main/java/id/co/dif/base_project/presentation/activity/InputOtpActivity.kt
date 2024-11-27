package id.co.dif.base_project.presentation.activity

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.databinding.ActivityInputOtpactivityBinding
import id.co.dif.base_project.databinding.DialogPopupPasteBinding
import id.co.dif.base_project.service.LocationClient
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.dimBehind
import id.co.dif.base_project.utils.getClipboard
import id.co.dif.base_project.utils.hasLocationPermission
import id.co.dif.base_project.utils.hasPermission
import id.co.dif.base_project.utils.ifTrue
import id.co.dif.base_project.utils.isNotNullOrEmpty
import id.co.dif.base_project.utils.showSoftKeyboard
import id.co.dif.base_project.viewmodel.InputOTPViewModel
import org.koin.core.component.inject

class InputOtpActivity : BaseActivity<InputOTPViewModel, ActivityInputOtpactivityBinding>() {
    override val layoutResId = R.layout.activity_input_otpactivity
    private val locationClient: LocationClient by inject()
    lateinit var alertBinding: DialogPopupPasteBinding
    private lateinit var alertWindow: PopupWindow
    private var currentAlertAnchor: View? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // Assuming you have a list of EditTexts that you want to set focus on
        val editTextList = listOf(
            binding.otp1,
            binding.otp2,
            binding.otp3,
            binding.otp4,
            binding.otp5,
            binding.otp6
        )
        val textWatchers = mapOf<Int, TextWatcher?>()


        editTextList.forEach {
            it.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) return@OnFocusChangeListener
                val clipboardText = getClipboard()
                if (clipboardText.isNullOrEmpty()) return@OnFocusChangeListener
                if (!isOtpValid(clipboardText)) return@OnFocusChangeListener
                showAlertPopup(
                    anchor = v,
                    message = "Paste",
                    dimBehind = false,
                    onClicked = {
                        clipboardText.forEachIndexed { index, value ->
                            editTextList[index].run {
                                setText(value.toString())
                                if (length() > 0) setSelection(1)
                            }
                        }
                    }
                )
            }
        }
        // Set up key listener for each EditText
        editTextList.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun afterTextChanged(s: Editable?) {
                    if (index < editTextList.lastIndex && !s.isNullOrEmpty()) {
                        editTextList[index + 1].run {
                            requestFocus()
                            if (length() > 0) {
                                setSelection(1)
                            }
                        }
                    }
                    if (s.isNullOrEmpty() && index > 0) {
                        editTextList[index - 1].run {
                            requestFocus()
                            if (length() > 0) {
                                setSelection(1)
                            }
                        }
                    }
                }

            })
        }

        binding.btSendCode.setOnClickListener {
            val responseLogin = preferences.loginData.value
            requestNotificationPermission()
            responseLogin?.let {
                preferences.myDetailProfile.value = BasicInfo(id = it.id)
                requestLocationPermission(
                    onDenied = {
                        showToast(getString(R.string.something_went_wrong_check_gps_is_active))
                    },
                    onGranted = {
                        viewModel.showLoading()
                        locationClient.getCurrentLocation { location ->
                            if (location == null) {
                                showToast(getString(R.string.something_went_wrong_check_gps_is_active))
                                viewModel.dissmissLoading()
                                return@getCurrentLocation
                            }
                            val otp = editTextList.joinToString(
                                separator = "",
                                transform = {
                                    it.text.toString()
                                }
                            )
                            viewModel.dissmissLoading()
                            viewModel.postOtp(
                                otp = otp,
                                latitude = location.latitude,
                                longtitude = location.longitude,
                                id = it.id
                            )
                        }
                    }
                )
            }
        }

        viewModel.responseOtp.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                preferences.session.value = it.data
                viewModel.getDetailedProfile(it.data.id?.toInt())
            } else {
                showToast("Something went wrong!")
            }
        }

        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                preferences.myDetailProfile.value = it.data
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }


        binding.resendOtp.setOnClickListener {
            if (isDateTimeAutomatic()) {
                openOtpApp()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setMessage("Turn on auto time zone for resend otp")
                .setPositiveButton(
                    "Go Setting"
                ) { dialog, _ ->
                    dialog.dismiss()
                    startActivityForResult(
                        Intent(Settings.ACTION_DATE_SETTINGS),
                        0
                    )
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
        setupChartSelectedChip()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermission(onDenied: () -> Unit = {}, onGranted: () -> Unit = {}) {
        if (hasLocationPermission()) {
            onGranted()
            return
        }
        getPermission(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            onDenied = {
                onDenied()
            },
            onGranted = {
                onGranted()
            }
        )
    }

    private fun requestNotificationPermission(onDenied: () -> Unit = {}, onGranted: () -> Unit = {}) {
        if (hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            onGranted()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPermission(
                android.Manifest.permission.POST_NOTIFICATIONS,
                onDenied = {
                    onDenied()
                },
                onGranted = {
                    onGranted()
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 0) return
        if (!isDateTimeAutomatic()) return
        openOtpApp()
    }

    private fun openOtpApp() {
        val otpPackage = "com.google.android.apps.authenticator2"
        try {
            val intent = packageManager.getLaunchIntentForPackage(otpPackage)
            startActivity(intent)
        } catch (e: Exception) {
            AlertDialog.Builder(this)
                .setMessage("You must install google authenticator first.")
                .setPositiveButton(
                    "Install"
                ) { dialog, _ ->
                    dialog.dismiss()
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$otpPackage")
                            )
                        )
                    } catch (anfe: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$otpPackage")
                            )
                        )
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
    }

    private fun isDateTimeAutomatic(): Boolean {
        return try {
            val autoTimeSettings = Settings.Global.getInt(
                applicationContext.contentResolver,
                Settings.Global.AUTO_TIME
            )
            val autoTimeZoneSettings = Settings.Global.getInt(
                applicationContext.contentResolver,
                Settings.Global.AUTO_TIME_ZONE
            )
            autoTimeSettings == 1 && autoTimeZoneSettings == 1
        } catch (e: Settings.SettingNotFoundException) {
            // Handle the exception (e.g., log, fallback to default behavior)
            false
        }
    }

    private fun showAlertPopup(
        anchor: View,
        dimBehind: Boolean? = true,
        message: String?,
        onClicked: () -> Unit = {}
    ) {
        currentAlertAnchor = anchor
        alertWindow.dismiss()
        alertBinding.text.text = message
        alertWindow.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val xOff = alertWindow.contentView.measuredWidth - anchor.width / 2
        val yOff = (-anchor.height - alertWindow.contentView.measuredHeight)

        alertBinding.root.setOnClickListener {
            onClicked()
            alertWindow.dismiss()
        }
        alertWindow.showAsDropDown(anchor, xOff, yOff)
        dimBehind.ifTrue {
            alertWindow.dimBehind()
            alertWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertWindow.isOutsideTouchable = true
            alertWindow.isFocusable = false
            alertWindow.update()

            alertWindow.contentView.setOnTouchListener { view, event ->
                (event.action == MotionEvent.ACTION_OUTSIDE) ifTrue {
                    alertWindow.dismiss()
                }
                view.performClick()
            }
        }

    }

    private fun setupChartSelectedChip() {
        binding.root.setOnClickListener {
            alertWindow.dismiss()
        }
        val li = LayoutInflater.from(this)
        alertBinding =
            DialogPopupPasteBinding.inflate(li)
        alertWindow = PopupWindow(
            alertBinding.root,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        binding.root.viewTreeObserver.addOnScrollChangedListener {
            currentAlertAnchor?.let {
                alertWindow.dismiss()
                currentAlertAnchor = null
            }
        }
        // Dismiss if the chip goes out of the visible screen

    }

    override fun onResume() {
        super.onResume()
        alertWindow.dismiss()
    }

    private fun isOtpValid(otpCode: String): Boolean {
        val otpPattern = Regex("\\d{6}")
        return otpPattern.matches(otpCode)
    }
}
