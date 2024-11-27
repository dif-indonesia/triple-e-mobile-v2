package id.co.dif.base_project.presentation.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.LocationType
import id.co.dif.base_project.databinding.ActivitySelectSiteBinding
import id.co.dif.base_project.presentation.dialog.SelectEngineerDialog
import id.co.dif.base_project.presentation.fragment.DetailFragment.Companion.SELECT_ENGINEER_REQUEST_CODE
import id.co.dif.base_project.presentation.fragment.MarkerPopupDialog
import id.co.dif.base_project.utils.*
import id.co.dif.base_project.viewmodel.SelectSiteViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 15:01
 *
 */
class SelectSiteActivity : BaseActivity<SelectSiteViewModel, ActivitySelectSiteBinding>(),
    OnMapReadyCallback,
    ClusterManager.OnClusterItemClickListener<Location>,
    ClusterManager.OnClusterClickListener<Location> {
    override val layoutResId = R.layout.activity_select_site
    private var clusterManager: ClusterManager<Location>? = null
    private var countDownTimer: CountDownTimer? = null
    private lateinit var map: GoogleMap
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        supportFragmentManager.findFragmentById(R.id.map)?.getMapAsync(this)
        binding.rootLayout.onActionButtonClicked = {
            selectSite(null)
        }


        viewModel.responseListLocation.observe(lifecycleOwner) {
            if (it.status == 200 && it.data.list.isNotEmpty()) {
                clusterManager?.let { clusterManager ->
                    val hashSet = HashSet<String>()
                    map.clear()
                    clusterManager.clearItems()
                    binding.rootLayout.title = getString(R.string.select_site)
                    it.data.list.forEach { location ->
                        clusterManager.addValidItem(location)
                        location.image?.let { hashSet.add(it) }
                    }
                    map.zoom(clusterManager, it.data.list, showToast = false)
                    val adapter: ArrayAdapter<Location> = ArrayAdapter<Location>(
                        this,
                        R.layout.item_spinner_dropdown,
                        it.data.list
                    )
                    if (binding.etSearch.text.isNotEmpty()) {
                        binding.etSearch.showDropDown()
                    }

                    binding.etSearch.setAdapter(adapter)
                    binding.etSearch.setOnItemClickListener { _, _, position, _ ->
                        hideSoftKeyboard(this)
                        binding.etSearch.clearFocus()
                        val site = adapter.getItem(position)
                        site?.let {
                            it.position.log("sdfdsfsdfdsfsdffsd")
                            map.zoom(clusterManager, site)
                        }
                    }
                }
            }
        }

        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etSearch.setText("")
                viewModel.getListSite(null)
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setPadding(0, 0, 0, 20.toDp)
        map.customMaps(this)

        viewModel.getListSite()

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        clusterizeMap()
    }

    private fun clusterizeMap() = activityContext?.let { context ->
        clusterManager = ClusterManager(this, map)
        map.setOnCameraIdleListener(clusterManager)
        clusterManager?.let { clusterManager ->
            clusterManager.setOnClusterClickListener(this)
            clusterManager.setOnClusterItemClickListener(this)
            clusterManager.renderer = TripleEMapClusterRenderer(context, map, clusterManager)
        }
    }

    override fun onClusterItemClick(location: Location): Boolean {
        when (LocationType.fromString(location.type)) {
            LocationType.TtMapAll -> {
                MarkerPopupDialog.newInstance(location = location).show(
                    supportFragmentManager,
                    MarkerPopupDialog::class.java.name
                ).log("debug1")
            }

            LocationType.TtSiteAll -> {
                clusterManager?.let { clusterManager ->
                    map.zoom(clusterManager, location, showToast = false) {
                        val popup = MarkerPopupDialog.newInstance(location = location)
                        popup.onSiteIsSelected = {
                            selectSite(it)
                        }
                        popup.isSiteSelectable = true
                        popup.show(
                            supportFragmentManager,
                            MarkerPopupDialog::class.java.name
                        ).log("debug2")
                    }
                }
            }

            else -> Unit
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        viewModel.selectedSite?.let {
            viewModel.getListSite()
        }
    }

    override fun onClusterClick(cluster: Cluster<Location>): Boolean {
        clusterManager?.let { clusterManager ->
            map.zoom(clusterManager, cluster.items.toList(), showToast = false) {
                binding.etSearch.clearFocus()
            }
        }
        return true
    }

    private fun selectSite(selectedSite: Location?) {
        val intent = Intent()
        intent.putExtra("selected_site", selectedSite)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, SelectSiteActivity::class.java)
        }
    }


}