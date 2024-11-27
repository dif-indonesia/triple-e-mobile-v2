package id.co.dif.base_project.presentation.activity

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.centerPointOfIndonesia
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.LocationType
import id.co.dif.base_project.data.Note
import id.co.dif.base_project.databinding.ActivityMapsNotesBinding
import id.co.dif.base_project.presentation.dialog.NotePopupDialog
import id.co.dif.base_project.presentation.dialog.PopUpProfileDialog
import id.co.dif.base_project.presentation.fragment.TicketListPopupDialog
import id.co.dif.base_project.utils.TripleEMapClusterRenderer
import id.co.dif.base_project.utils.addValidItem
import id.co.dif.base_project.utils.animateToMeters
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.distanceTo
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.toDp
import id.co.dif.base_project.utils.urlTypeOf
import id.co.dif.base_project.utils.zoom
import id.co.dif.base_project.viewmodel.MapsViewModel
import kotlinx.coroutines.launch

class MapsNotesActivity : BaseActivity<MapsViewModel, ActivityMapsNotesBinding>(),
    OnMapReadyCallback, ClusterManager.OnClusterClickListener<Location>,
    ClusterManager.OnClusterItemClickListener<Location> {
    private var clusterManager: ClusterManager<Location>? = null
    override val layoutResId = R.layout.activity_maps_notes
    private lateinit var map: GoogleMap
    lateinit var notes: List<Note>
    var locations: List<Location> = mutableListOf()
    var zoomMap: () -> Unit = {}

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.imgRefresh.setOnClickListener {
            map.clear()
            setupClusterization()
            locations.forEach {
                clusterManager?.addValidItem(it)
            }
            zoomMap()
        }
    }

    private fun setupLocations() {
        val data = preferences.ticketDetails.value
        data?.let { opsTicket ->
            notes = opsTicket.tic_notes.orEmpty()
            val highlightedNote = preferences.highlightedNote
            val notesWithImage = notes.filter {
                urlTypeOf(it.file) == "image"
            }
            notesWithImage.joinToString("\n") { it.toString() }.log("dsfdfdsfsdfdsf")
            val siteInfo = opsTicket.site_info
            val siteLatitude = siteInfo?.technologyLatitude
            val siteLongitude = siteInfo?.technologyLongitude
            var siteLocation = preferences.selectedSite.value

            if (siteLocation == null) {
                siteLocation = Location(
                    latitude = siteLatitude.orDefault("0"),
                    longtitude = siteLongitude.orDefault("0"),
                    type = "site",
                    site_name = opsTicket.site_info?.siteName.orDefault("Site"),
                    site_addre_street = siteInfo?.siteAddressStreet.orDefault(),
                    site_end_customer = siteInfo?.siteEndCustomer,
                    site_contact_phone = siteInfo?.siteContactPhone
                )
            }
            siteLocation = if (opsTicket.site_info?.technologyLatitude == null || opsTicket.site_info?.technologyLatitude == null) {
                null
            } else {
                siteLocation
            }

            Log.d(TAG, "setupLocations loc: $siteLatitude $siteLongitude")
            // TODO: modify this hardcode
            val notesLocations = notesWithImage.mapNotNull {
                try {
                    val urlType = urlTypeOf(it.file)
                    val image = if (urlType == "image") {
                        it.file
                    } else {
                        null
                    }
                    it.log("maps location")
                    Location(
                        id = it.id,
                        latitude = it.latitude.orDefault("0"),
                        longtitude = it.longitude.orDefault("0"),
                        image = image,
                        type = LocationType.Note.name
                    )
                } catch (e: Exception) {
                    e.stackTrace.log("askljhgkjlafhlkahf")
                    null
                }
            }
            map.clear()
            locations = notesLocations
            if (siteLocation != null) locations += siteLocation
            locations.joinToString("\n") { it.position.toString() }.log("asdasdasdasd")
            val highlightedNoteLocation = notesLocations.find { it.id == highlightedNote.value?.id }
            highlightedNoteLocation?.let {
                zoomMap = {
                    if (siteLocation != null) {
                        val distance = it.position.distanceTo(siteLocation.position)
                        map.animateToMeters(clusterManager!!, distance, it.position)
                    } else {
                        map.zoom(clusterManager!!, locations)
                    }
                }
                zoomMap()
            }
            locations.forEach {
                clusterManager!!.addValidItem(it)
            }
        }
    }

    private fun setupClusterization() = activityContext?.let { context ->
        clusterManager = ClusterManager(this, map)
        clusterManager?.let {
            map.setOnCameraIdleListener(clusterManager)
            it.setOnClusterClickListener(this)
            it.setOnClusterItemClickListener(this)
            it.renderer = TripleEMapClusterRenderer(context, map, it)
            it.renderer
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupClusterization()
        map.setOnCameraIdleListener(clusterManager)
        map.setPadding(0, 80.toDp, 0, 20.toDp)

        try {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.google_map_style
                )
            )
        } catch (_: Resources.NotFoundException) {
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            centerPointOfIndonesia, 3.6f
        )
        map.moveCamera(cameraUpdate)

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        setupLocations()

    }

    override fun onClusterClick(cluster: Cluster<Location>): Boolean {
        map.zoom(clusterManager!!, cluster.items.toList()) {
            if (map.cameraPosition.zoom >= map.maxZoomLevel) {
                val onlyNotes = cluster.items.filter { it.type == LocationType.Note.name }
                val notesInCluster =
                    onlyNotes.mapNotNull { loc -> notes.first { note -> note.id == loc.id } }
                NotePopupDialog.newInstance(notesInCluster).show(
                    supportFragmentManager,
                    PopUpProfileDialog::class.java.name
                )
            }
        }
        return true
    }

    override fun onClusterItemClick(item: Location): Boolean {
        map.zoom(clusterManager!!, item) {
            lifecycleOwner.lifecycleScope.launch {
                if (item.type == LocationType.Note.name) {
                    val note = notes.first { note -> note.id == item.id }
                    NotePopupDialog.newInstance(listOf(note)).show(
                        supportFragmentManager,
                        TicketListPopupDialog::class.java.name
                    )
                }
            }
        }
        return false
    }


}