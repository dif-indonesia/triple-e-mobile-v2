package id.co.dif.base_project.presentation.fragment

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.LocationType
import id.co.dif.base_project.data.TicketDetails
import id.co.dif.base_project.databinding.FragmentMapsTicketBinding
import id.co.dif.base_project.presentation.dialog.PopUpProfileDialog
import id.co.dif.base_project.utils.TripleEMapClusterRenderer
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.colorRes
import id.co.dif.base_project.utils.toDp
import id.co.dif.base_project.utils.zoom
import id.co.dif.base_project.utils.*
import id.co.dif.base_project.viewmodel.MapsTicketViewModel

class MapsTicketFragment : BaseFragment<MapsTicketViewModel, FragmentMapsTicketBinding>(),
    OnMapReadyCallback, ClusterManager.OnClusterClickListener<Location>,
    ClusterManager.OnClusterItemClickListener<Location> {
    override val layoutResId = R.layout.fragment_maps_ticket
    private var clusterManager: ClusterManager<Location>
        get() = viewModel.clusterManager
        set(value) {
            viewModel.clusterManager = value
        }
    var map: GoogleMap
        set(value) {
            viewModel.map = value
        }
        get() = viewModel.map
    private var sourceLoc: LatLng? = null
    private var destinationLoc: LatLng? = null
    var locations = listOf<Location>()
    var zoomMap: () -> Unit = {}
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.imgRefresh.setOnClickListener {
            map.clear()
            binding.layoutAction.isVisible = false
            setupClusterization()
            locations.forEach {
                clusterManager.addValidItem(it)
            }
            zoomMap()
        }
    }

    private fun dismissMapLoading() {
        binding.loadingOverlay.isVisible = false
    }

    private fun showMapLoading() {
        binding.loadingOverlay.isVisible = true
    }

    private fun setupLocations(ticketDetails: TicketDetails?) {
        ticketDetails?.let { data ->
            if (!requireContext().isDeviceOnline()) {
                loadMarkerOffline()
            } else {
                viewModel.getNearestTechnician(
                    data.site_info?.siteId,
                    onError = { dismissMapLoading() },
                    onLoading = { showMapLoading() },
                    onResult = { dismissMapLoading() },
                )
            }
        }
        viewModel.responseNearestTechnician.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                map.clear()
                setupClusterization()
                ticketDetails?.let { data ->
                    val fieldEngineer =
                        it.data.list.firstOrNull { loc ->
                            loc.id == data.tic_field_engineer_id?.toInt()
                        }
                    var site = ticketDetails.site_info?.toSiteLocation()
                    if (ticketDetails.tic_site_id == null) {
                        site = null
                    }
                    preferences.selectedSite.value = site
                    site?.image = data.site_info?.image
                    data.site_info?.image.log("jgjjgjgjggh")
                    site?.type = LocationType.Site.name
                    if ((session?.emp_security?:0) >= 2){
                        viewModel.getDetailProfile(data.tic_person_in_charge_id?.toInt())
                    }
                    locations = listOfNotNull(site, fieldEngineer)

                    locations.forEach { loc ->
                        clusterManager.addValidItem(loc)
                    }
                    map.setOnMapLoadedCallback {
                        zoomMap = {
                            map.zoom(clusterManager, locations)
                        }
                        zoomMap()
                    }
                }
            } else {
                currentActivity.showToast("Error while loading maps!")
            }
        }

        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                val location = Location(
                    id = it.data.id,
                    latitude = it.data.latitude.orDefault("0"),
                    longtitude = it.data.longtitude.orDefault("0"),
                    image = it.data.photo_profile,
                    name = it.data.fullname,
                    type = "technician"
                )
                locations = locations + location
                var technicianOnly = locations.filter { it.type == LocationType.Technician.name }
                technicianOnly = technicianOnly.distinctBy { it.id }
                val site = locations.filter { it.type == LocationType.Site.name }
                locations = technicianOnly + site
                clusterManager.clearItems()
                locations.forEach {
                    clusterManager.addValidItem(it)
                }
                clusterManager.cluster()

                map.zoom(clusterManager, locations)

            }
        }

        viewModel.responseDirection.observe(lifecycleOwner) {
            if (sourceLoc != null && destinationLoc != null) {
                try {
                    val src =
                        Location(
                            latitude = sourceLoc!!.latitude.toString(),
                            longtitude = sourceLoc!!.longitude.toString()
                        )
                    val dst = Location(
                        latitude = destinationLoc!!.latitude.toString(),
                        longtitude = destinationLoc!!.longitude.toString()
                    )
                    binding.layoutAction.isVisible = true
                    redrawMaps()
                    if (it.routes.size == 0){
                        showToast("Can't find route to destination")
                        return@observe
                    }
                    val routes = it.routes
                    val legs = routes[0].legs

                    // Menampilkan ETA (waktu tempuh total)
                    binding.layoutEtaDirection.isVisible = true
//                    binding.etaDirection.text = legs[0].duration.text
                    binding.etaDirection.text = "${legs[0].distance.text}\n${legs[0].duration.text}"
                    viewModel.getEstimateTimeDirection(
                        id = ticketDetails?.tic_id,
                        mutableMapOf(
                            "estimated_time" to legs[0].duration.value
                        )
                    )

                    val steps = legs[0].steps
                    val path: MutableList<List<LatLng>> = ArrayList()
                    for (i in 0 until steps.size) {
                        val points = steps[i].polyline.points
                        path.add(PolyUtil.decode(points))
                    }
                    for (i in 0 until path.size) {
                        map.addPolyline(
                            PolylineOptions().addAll(path[i]).width(20f)
                                .color(R.color.light_blue.colorRes(requireContext())).endCap(
                                    RoundCap()
                                )
                        )
                    }
                    val locations = listOf(src, dst) + path.flatten()
                        .map { Location(latitude = it.latitude.toString(), longtitude = it.longitude.toString()) }
                    map.zoom(clusterManager, locations) {
                    }
                } catch (e: Exception) {
                    binding.layoutAction.isVisible = false
                    e.printStackTrace()
                    redrawMaps()
                    currentActivity.showToast("Something went wrong!")
                }
            }
        }
        binding.imgCancel.setOnClickListener {
            binding.layoutAction.isVisible = false
            binding.layoutEtaDirection.isVisible = false
            redrawMaps()
        }

        binding.imgDirection.setOnClickListener {
            sourceLoc?.let { it1 -> destinationLoc?.let { it2 -> openMaps(it1, it2) } }
        }

    }

    fun loadMarkerOffline() {
        val data = preferences.ticketDetails.value
        var site: Location? = Location(
            type = "TT Site All",
            name = data?.site_info?.siteName,
            site_addre_street = data?.site_info?.siteAddressStreet,
            latitude = data?.site_info?.technologyLatitude.orDefault("0"),
            longtitude = data?.site_info?.technologyLongitude.orDefault("0"),
            image = data?.site_info?.image

        )
        if (data?.tic_site_id == null) {
            site = null
        }
        val technician = Location(
            type = "technician",
            name = preferences.myDetailProfile.value?.fullname,
            image = preferences.myDetailProfile.value?.photo_profile,
            latitude = preferences.myDetailProfile.value?.latitude.orDefault("0"),
            longtitude = preferences.myDetailProfile.value?.longtitude.orDefault("0")
        )
        locations = listOfNotNull(site, technician)
        redrawMaps()
        map.zoom(clusterManager, locations)
    }

    private fun openMaps(src: LatLng, dst: LatLng) {
        val uri =
            Uri.parse("http://maps.google.com/maps?saddr=${src.latitude},${src.longitude}&daddr=${dst.latitude},${dst.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context?.startActivity(intent)
    }

    private fun redrawMaps() {
        map.clear()
        setupClusterization()
        clusterManager.clearItems()
        locations.forEach { loc ->
            clusterManager.addValidItem(loc)
        }
        clusterManager.cluster()
    }

    private fun setupClusterization() = fragmentContext?.let { context ->
        clusterManager = ClusterManager(context, map)
        map.setOnCameraIdleListener(clusterManager)
        clusterManager.setOnClusterClickListener(this)
        clusterManager.setOnClusterItemClickListener(this)
        clusterManager.renderer = TripleEMapClusterRenderer(context, map, clusterManager)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setPadding(0, 80.toDp, 0, 20.toDp)

        try {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.google_map_style
                )
            )
        } catch (_: Resources.NotFoundException) {
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(-2.548926, 118.0148634), 3.6f
        )
        map.moveCamera(cameraUpdate)


        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        val ticketDetails = preferences.ticketDetails.value
        setupLocations(ticketDetails)
    }

    override fun onClusterClick(cluster: Cluster<Location>): Boolean {
        map.zoom(clusterManager, cluster.items.toList()) {
            if (map.cameraPosition.zoom < map.maxZoomLevel) {
                return@zoom
            }
            val techniciansOnly =
                cluster.items.filter { loc -> loc.type == LocationType.Technician.name }
            PopUpProfileDialog.newInstance(techniciansOnly, true) { getDirection(it) }.show(
                childFragmentManager,
                PopUpProfileDialog::class.java.name
            )
        }
        return true
    }

    override fun onClusterItemClick(item: Location): Boolean {
        map.zoom(clusterManager, item) {
            when (LocationType.fromString(item.type)) {
                LocationType.Site -> {
//                    MarkerPopupDialog.newInstance(item).show(
//                        childFragmentManager,
//                        MarkerPopupDialog::class.java.name
//                    )
                }

                LocationType.Technician -> {
                    val popup = PopUpProfileDialog.newInstance(item, true) {
                        getDirection(it)
                    }
                    popup.also {
                        it.show(
                            childFragmentManager,
                            PopUpProfileDialog::class.java.name
                        )
                    }
                }

                else -> {}
            }
        }
        return false
    }

    private fun getDirection(direction: Location) {
        val site = preferences.ticketDetails.value?.site_info
        val sitePosition = site?.toSiteLocation()?.position

        if (sitePosition != null && sitePosition.latitude != 0.0 && sitePosition.longitude != 0.0) {
            destinationLoc = sitePosition
            sourceLoc = direction.position
            viewModel.getDirection(sitePosition, direction.position)
        } else {
            currentActivity.showToast("Cannot create direction! Ticket doesn't have site associated.")
        }
    }

}

