package id.co.dif.base_project.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import id.co.dif.base_project.BuildConfig
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.data.LatestAppVersion
import id.co.dif.base_project.databinding.ActivityMainBinding
import id.co.dif.base_project.presentation.fragment.DashboardFragment
import id.co.dif.base_project.presentation.fragment.HomeFragment
import id.co.dif.base_project.presentation.fragment.InboxFragment
import id.co.dif.base_project.presentation.fragment.MeFragment
import id.co.dif.base_project.presentation.fragment.TroubleTicketFragment
import id.co.dif.base_project.utils.AndroidDownloader
import id.co.dif.base_project.utils.DownloadCompletedReceiver
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.drawableRes
import id.co.dif.base_project.utils.eval
import id.co.dif.base_project.utils.hasLocationPermission
import id.co.dif.base_project.utils.hasPermission
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.startLocationServiceScheduler
import id.co.dif.base_project.viewmodel.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), KoinComponent {
    private val dashboardFragment: DashboardFragment = DashboardFragment()
    private val meFragment: MeFragment = MeFragment()
    private val inboxFragment: InboxFragment by inject()
    private val troubleTicketFragment: TroubleTicketFragment by inject()
    private val homeFragment: HomeFragment by inject()
    private val downloadManager: AndroidDownloader by inject()
    override val layoutResId = R.layout.activity_main

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ObsoleteSdkInt")
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.bottomNavigation.setOnItemSelectedListener {
            onNavigationItemSelected(it)
        }
        if (!viewModel.periodicOfflineConnectivityIsRunning) {
            startListenForConnectivity()
        }

        binding.onOff.setOnClickListener {
            if (binding.bottomNavigation.selectedItemId == R.id.menu_map) {
                homeFragment.resetViewport()
            } else {
                toggleMapButton(true)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.fragment_container_view, homeFragment, "HOME")
                    binding.bottomNavigation.selectedItemId = R.id.menu_map
                    binding.iconHome.setColorFilter(ContextCompat.getColor(this@MainActivity,R.color.white))
                }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment_container_view, homeFragment)
            }
        }
        val profileId = intent.getIntExtra("open_profile", -1)
        if (profileId != -1) {
            openProfileDetail(profileId)
        }
        binding.locationUnavailableAlert.isVisible = !hasLocationPermission()

        requestLocationPermission(
            onGranted = {
                try {
                    startLocationServiceScheduler()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                binding.locationUnavailableAlert.visibility = View.GONE
                requestNotificationPermission()
            },
            onDenied = {
                binding.locationUnavailableAlert.visibility = View.VISIBLE
                requestNotificationPermission()
            }
        )

        viewModel.responseCheckLatestAppVersion.observe(this) {
            consumeCheckLatestApp(it)
        }
        viewModel.getLatestAppVersion()
    }

    private fun consumeCheckLatestApp(response: BaseResponse<LatestAppVersion>?) {
        if (response?.status in StatusCode.SUCCESS) {
            response?.data?.let { data ->
                val latestVersion = data.versionCode
                val downloadUrl = data.downloadUrl
                val currentVersionCode = BuildConfig.VERSION_CODE
                val mimeType = "application/vnd.android.package-archive"
                val versionName = latestVersion.toString().split("").joinToString(".")
                if (currentVersionCode < latestVersion) {
                    showAlert(
                        context = this,
                        message = "Latest app is available!",
                        value = "Download latest app to stay up-to-date",
                        onButtonPrimaryClicked = {
                            downloadManager.downloadFile(
                                title = "Latest Triple-E app V$versionName",
                                mimeType = mimeType,
                                url = downloadUrl
                            )
                        },
                        iconId = R.drawable.baseline_info_24,
                        iconTint = R.color.light_blue,
                        buttonPrimaryText = "Download",
                        buttonSecondaryText = "Later"
                    )
                }
            }
        }
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        val colorStateList = ContextCompat.getColorStateList(this, R.color.selector_bottom_nav_item)

        binding.bottomNavigation.itemTextColor = colorStateList
        binding.bottomNavigation.itemIconTintList = colorStateList
        (item.itemId == R.id.menu_map).not() && toggleMapButton(false)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            when (item.itemId) {
                R.id.menu_dashboard -> replace(R.id.fragment_container_view, dashboardFragment)
                R.id.menu_me -> replace(R.id.fragment_container_view, meFragment)
                R.id.menu_notification -> replace(
                    R.id.fragment_container_view,
                    inboxFragment
                )

                R.id.menu_trouble_ticket -> replace(
                    R.id.fragment_container_view,
                    troubleTicketFragment,
                )

                R.id.menu_map -> {
                    toggleMapButton(true)
                    replace(R.id.fragment_container_view, homeFragment, "HOME")

                }
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermission(onDenied: () -> Unit, onGranted: () -> Unit) {
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

    private fun toggleMapButton(active: Boolean): Boolean {
        binding.onOff.background =
            active.eval(R.drawable.bg_purple_gradient, R.drawable.bg_map_button_disabled)
                .drawableRes(this)
        binding.iconHome.setColorFilter(ContextCompat.getColor(this@MainActivity,R.color.blue_belitung))
        return active
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }

    override fun onBackPressedFragment() {
        super.onBackPressedFragment()
        binding.bottomNavigation.selectedItemId = R.id.menu_dashboard
    }

    fun openProfileDetail(userId: Int) {
        val info = preferences.myDetailProfile.value
        info?.let {
            if (it.id == userId) {
                binding.bottomNavigation.selectedItemId = R.id.menu_me
            } else {
                needsImplementedToast("Go to other profile detail")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        session?.let {
            it.id?.let { id ->
                preferences.selectedProfileId.value = id.toInt()
            }
        }
    }

    private fun startListenForConnectivity() {
        Log.d(TAG, "startListenForConnectivity: ")
        viewModel.periodicOfflineConnectivityIsRunning = true
        viewModel.startPeriodicOfflineNotesUpload()
    }


}


