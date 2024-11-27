package id.co.dif.base_project.base

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import id.co.dif.base_project.LOCATION_UPDATE_INTERVAL
import id.co.dif.base_project.R
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.LocationStats
import id.co.dif.base_project.data.Session
import id.co.dif.base_project.databinding.DialogOutOfRangeAlertBinding
import id.co.dif.base_project.presentation.dialog.LoadingDialog
import id.co.dif.base_project.persistence.Preferences
import id.co.dif.base_project.utils.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.util.Calendar

/***
 * Created by kikiprayudi
 * on Monday, 27/02/23 15:36
 *
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity(),
    KoinComponent {
    var activityContext by WeakContextDelegate<Context>()
    val preferences: Preferences by inject()
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var viewModel: VM
    lateinit var binding: VB
    abstract val layoutResId: Int
    var progress: LoadingDialog? = null
    private var alertDialog: Dialog? = null
    var session: Session? = null
        get() = viewModel.session
    var basiInfo: BasicInfo? = null
        get() = viewModel.basiInfo
    val lastLocationStatus: LocationStats
        get() {
            val lastLocation = preferences.lastLocation.value ?: return LocationStats.UNAVAILABLE
            val calendar = Calendar.getInstance()
            val millisNow = calendar.timeInMillis
            val duration = millisNow - lastLocation.lastUpdate
            if (duration > LOCATION_UPDATE_INTERVAL) {
                return LocationStats.EXPIRED
            }
            return LocationStats.VALID
        }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag(TAG).d("onCreate: ")
        activityContext = this
        window?.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.TRANSPARENT
        }
        super.onCreate(savedInstanceState)
        lifecycleOwner = this
        binding = DataBindingUtil.setContentView(this, layoutResId)
        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        viewModel.uiState.observe(lifecycleOwner) {
            if (progress == null) {
                progress = LoadingDialog(this)
            }
            progress?.onBtnCancelClicked = {
                viewModel.cancelAllJobs()
                viewModel.dissmissLoading()
            }
            if (it?.isShowLoading == true) {
                if (progress?.isShowing == false) {
                    progress?.show()
                }
            } else {
                if (progress?.isShowing == true) {
                    progress?.dismiss()
                }
            }
        }
        onViewBindingCreated(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityContext = null
        viewModel.cancelAllJobs()
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelAllJobs()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getViewModelClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
    }

    abstract fun onViewBindingCreated(savedInstanceState: Bundle?)
    open fun onBackPressedFragment() {


    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun needsImplementedToast(message: String) {
        Toast.makeText(this, "[NEEDS IMPLEMENTATION] $message", Toast.LENGTH_SHORT).show()
    }

    fun showAlert(
        context: Context = this,
        cancelable: Boolean = true,
        message: String,
        buttonPrimaryText: String? = null,
        buttonSecondaryText: String? = null,
        value: String? = "",
        @DrawableRes iconId: Int = R.drawable.ic_alert,
        @ColorRes buttonPrimaryColor: Int = R.color.purple,
        @ColorRes buttonSecondaryColor: Int = R.color.dark_grey,
        @ColorRes iconTint: Int = R.color.light_orange,
        onButtonSecondaryClicked: () -> Unit = {},
        onButtonPrimaryClicked: () -> Unit = {},
    ) {
        alertDialog?.dismiss()
        alertDialog = Dialog(context);
        alertDialog?.let { dialog ->
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            val li = LayoutInflater.from(context)
            val binding = DialogOutOfRangeAlertBinding.inflate(li)
            binding.tvContinue.setOnClickListener {
                dialog.dismiss()
                onButtonPrimaryClicked()
            }
            binding.tvCancel.setOnClickListener {
                dialog.dismiss()
                onButtonSecondaryClicked()
            }
            binding.tvContinue.isVisible = buttonPrimaryText != null
            binding.tvCancel.isVisible = buttonSecondaryText != null
            binding.tvContinue.setTextColor(buttonPrimaryColor.colorRes(context))
            binding.tvCancel.setTextColor(buttonSecondaryColor.colorRes(context))
            binding.imgAlert.background = iconId.drawableRes(context)
            binding.imgAlert.backgroundTintList = ColorStateList.valueOf(iconTint.colorRes(context))
            binding.tvContinue.text = buttonPrimaryText
            binding.tvCancel.text = buttonSecondaryText
            if (value.isNotNullOrEmpty()) {
                binding.tvValue.text = value
                binding.tvValue.isVisible = true
            } else {
                binding.tvValue.isVisible = false
            }
            binding.tvDescription.text = message

            dialog.setContentView(binding.root);
            dialog.setCancelable(cancelable);
            dialog.show()
            dialog.window?.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setBackgroundDrawable(null)
        }
    }

    fun getPermission(
        permission: String, onGranted: () -> Unit, onDenied: () -> Unit
    ) = activityContext?.let { context ->
        Dexter.withContext(this).withPermission(permission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    onGranted()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Log.d("TAG", "onPermissionDenied: denied")
                    onDenied()
                    p0?.let {
                        if (it.isPermanentlyDenied) {
                            showAlert(
                                context = context,
                                message = getString(R.string.you_denied_the_permission_permanently_go_to_app_settings_to_enable_it),
                                value = getPermissionName(permission).orDefault(""),
                                buttonPrimaryText = getString(R.string.app_settings)
                            ) {
                                openAppSettings()
                            }
                            return
                        }
                    }
                    showAlert(
                        context = context,
                        message = "You need to enable the permission in order the app to operate properly",
                        value = getPermissionName(permission).orDefault(""),
                        buttonPrimaryText = "OK"
                    ) {
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?, p1: PermissionToken?
                ) {
                    showAlert(
                        context = context,
                        message = getString(R.string.you_need_to_enable_the_permission_in_order_the_app_to_operate_properly),
                        value = getPermissionName(permission).orDefault(""),
                        buttonPrimaryText = "Continue"
                    ) {
                        p1?.continuePermissionRequest()
                    }
                }
            }).check()
    }


    companion object {
        val TAG: String = this::class.java.simpleName ?: ""
    }
}