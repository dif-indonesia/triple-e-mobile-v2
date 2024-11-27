package id.co.dif.base_project.presentation.fragment

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.DataList
import id.co.dif.base_project.centerPointOfIndonesia
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.LocationType
import id.co.dif.base_project.databinding.FragmentHomeBinding
import id.co.dif.base_project.presentation.dialog.PopUpProfileDialog
import id.co.dif.base_project.utils.TripleEMapClusterRenderer
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.addValidItem
import id.co.dif.base_project.utils.animateToMeters
import id.co.dif.base_project.utils.base64ImageToBitmap
import id.co.dif.base_project.utils.closestDistance
import id.co.dif.base_project.utils.hideSoftKeyboard
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.toDp
import id.co.dif.base_project.utils.zoom
import id.co.dif.base_project.viewmodel.HomeViewModel
import org.koin.core.component.KoinComponent

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(), OnMapReadyCallback,
    ClusterManager.OnClusterItemClickListener<Location>,
    ClusterManager.OnClusterClickListener<Location>, KoinComponent {
    override val layoutResId = R.layout.fragment_home
    var zoomMap: () -> Unit = {}
    var map: GoogleMap
        set(value) {
            viewModel.map = value
        }
        get() = viewModel.map
    private var clusterManager: ClusterManager<Location>
        set(value) {
            viewModel.clusterManager = value
        }
        get() = viewModel.clusterManager

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        viewModel.mapLoading.observe(lifecycleOwner){
            binding.loadingOverlay.isVisible = it
        }
        viewModel.getListSite(null)
        setAutoCompleteSearch(preferences.savedAllSite.value ?: listOf())
        binding.imgRefresh.setOnClickListener {
            map.clear()
            setupClusterization()
            viewModel.markerItems.forEach {
                clusterManager.addValidItem(it)
            }
            zoomMap()
        }

        viewModel.responseSiteLocation.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                preferences.savedAllSite.value = it.data.list
                setAutoCompleteSearch(it.data.list)
            }
        }

        viewModel.responseMapAlarm.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                populateMapMe(it.data.list)
            }else{
                it.log("sdflkjsdlkfjlfjsd")
            }
        }

        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Log.d("TAG", "onViewBindingsdCreated: sdkads ${preferences.savedAllSite.value?.size}")
                binding.etSearch.setText("")
                populateMapSite(preferences.savedAllSite.value ?: listOf())
            }
        }
        viewModel.hasStarted = true
    }

    private fun populateMapSite(locations: List<Location>) {
        map.clear()
        viewModel.markerItems.clear()
        setupClusterization()
        clusterManager.clearItems()
        locations.forEach { location ->
            clusterManager.addValidItem(location)
            viewModel.markerItems.add(location)
        }
        zoomMap = {
            map.zoom(clusterManager, locations, showToast = false)
        }
        zoomMap()
    }

    private fun populateMapMe(locationList: List<Location>) {
        locationList.size.log("testgilanggg")
        map.clear()
        viewModel.markerItems.clear()
        setupClusterization()
        clusterManager.clearItems()
        locationList.forEachIndexed { index, location ->
            clusterManager.addValidItem(location)
            viewModel.markerItems.add(location)
        }
        clusterManager.cluster()

        zoomMap = {
            map.zoom(clusterManager, locationList, showToast = false)
        }
        zoomMap()
    }

    private fun setAutoCompleteSearch(list: List<Location>) {
        val adapter: ArrayAdapter<Location> = ArrayAdapter<Location>(
            currentActivity,
            R.layout.item_spinner_dropdown,
            list
        )

        binding.etSearch.setAdapter(adapter)
        binding.etSearch.setOnItemClickListener { _, _, position, _ ->
            hideSoftKeyboard(currentActivity)
            binding.etSearch.clearFocus()
            val site = adapter.getItem(position)
            site?.let {
                zoomMap = {
                    map.zoom(clusterManager, site) {
                        onClusterItemClick(site)
                    }
                }
                zoomMap()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setPadding(0, 80.toDp, 0, 20.toDp)
        map.setOnCameraChangeListener{}
        try {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.google_map_style
                )
            )
        } catch (_: NotFoundException) {
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            centerPointOfIndonesia, 3.6f
        )
        map.moveCamera(cameraUpdate)

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        populateMapSite(preferences.savedAllSite.value ?: listOf())
        val mapMe = preferences.lastMapAlarm.value ?: listOf()
        val response = BaseResponseList<Location>(
            data = DataList(list = mapMe, limit = mapMe.size, page = 1, total = mapMe.size),
            message = "",
            status = 200
        )
        viewModel.responseMapAlarm.value = response
        viewModel.getMapAlarm()
    }

    fun showMapLoading(){
        binding.loadingOverlay.isVisible = true
    }

    fun dismissMapLoading(){
        binding.loadingOverlay.isVisible = false
    }

    private fun setupClusterization() = fragmentContext?.let { context ->
        clusterManager = ClusterManager(context, map)
        map.setOnCameraIdleListener { clusterManager.cluster() }
        clusterManager.setOnClusterClickListener(this)
        clusterManager.setOnClusterItemClickListener(this)
        clusterManager.renderer = TripleEMapClusterRenderer(context, map, clusterManager)
    }

    override fun onClusterItemClick(location: Location): Boolean {
        binding.etSearch.clearFocus()
        map.zoom(clusterManager, location) {
            when (LocationType.fromString(location.type)) {
                LocationType.Technician -> {
                    val technician = Location(id = session?.id?.toInt())
                    PopUpProfileDialog.newInstance(listOf(technician)).show(
                        childFragmentManager,
                        PopUpProfileDialog::class.java.name
                    )
                }

                LocationType.TtMapAll -> TicketListPopupDialog.newInstance(location).show(
                    childFragmentManager,
                    TicketListPopupDialog::class.java.name
                )

                LocationType.TtSiteAll -> {
                    MarkerPopupDialog.newInstance(location).show(
                        childFragmentManager,
                        MarkerPopupDialog::class.java.name
                    )
                }

                LocationType.Site -> MarkerPopupDialog.newInstance(location).show(
                    childFragmentManager,
                    MarkerPopupDialog::class.java.name
                )

                LocationType.Note -> Unit
                else -> {}
            }
        }
        return true

    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.etSearch.setText("")
        viewModel.getMapAlarm()
    }

    fun resetViewport(): Boolean {
        val isReady = viewModel.responseMapAlarm.value != null
        if (isReady) {
            viewModel.responseMapAlarm.let { it.value = it.value }
        }
        return isReady
    }

    override fun onClusterClick(cluster: Cluster<Location>): Boolean {
        hideSoftKeyboard(requireActivity())
        binding.etSearch.clearFocus()
        map.zoom(clusterManager, cluster.items.toList(), showToast = false)
        return true
    }
}