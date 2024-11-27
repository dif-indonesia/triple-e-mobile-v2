package id.co.dif.base_project.utils

import android.R.attr
import android.content.Context
import android.content.res.Resources
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.SphericalUtil
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.base_project.MainApp
import id.co.dif.base_project.R
import id.co.dif.base_project.data.Location

fun GoogleMap.zoom(
    clusterManager: ClusterManager<Location>,
    locations: List<Location>,
    showToast: Boolean = true,
    onIdle: () -> Unit = {}
) {
    val latLngBounds = LatLngBounds.builder()
    val validLocations = locations.filter { it.markerIsValid() }
    val someLocationIsNotValid = validLocations.size != locations.size

    if (someLocationIsNotValid && showToast) {
        Toast.makeText(
            MainApp.appContext,
            "Some locations are not available!",
            Toast.LENGTH_SHORT
        ).show()
    }

    validLocations.forEach { location ->
        latLngBounds.include(
            location.position
        )
    }
    if (validLocations.isNullOrEmpty()) {
    }
    val cameraUpdate = if (validLocations.isNullOrEmpty()) {
        CameraUpdateFactory.newLatLngZoom(
            LatLng(-2.548926, 118.0148634), 3.6f
        )
    } else {
        val width: Int = Resources.getSystem() .getDisplayMetrics().widthPixels
        val height: Int = Resources.getSystem().getDisplayMetrics().heightPixels
        val padding = (width * 0.12).toInt() // offset from edges of the map 12% of screen


        CameraUpdateFactory.newLatLngBounds(
            latLngBounds.build(),
            width,
            height,
            padding
        )
    }
    animateCamera(cameraUpdate)
    setOnCameraIdleListener {
        onIdle()
        setOnCameraIdleListener {
            clusterManager.cluster()
        }
        clusterManager.cluster()
    }

}

fun GoogleMap.zoom(
    clusterManager: ClusterManager<Location>,
    loc: Location,
    showToast: Boolean = true,
    onIdle: () -> Unit = {}

) {
    zoom(clusterManager, listOf(loc),showToast,  onIdle)
}

fun GoogleMap.animateToMeters(
    clusterManager: ClusterManager<Location>,
    meters: Float,
    ll: LatLng,
    onIdle: () -> Unit = {}
) {
    val latLngBounds = ll.calculateBounds(meters.toDouble());
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
        latLngBounds,
        70
    )
    animateCamera(cameraUpdate);
    setOnCameraIdleListener {
        onIdle()
        setOnCameraIdleListener {
            clusterManager.cluster()
        }
        clusterManager.cluster()
    }
}

fun LatLng.calculateBounds(radius: Double): LatLngBounds {
    return LatLngBounds.Builder()
        .include(SphericalUtil.computeOffset(this, radius, 0.0))
        .include(SphericalUtil.computeOffset(this, radius, 90.0))
        .include(SphericalUtil.computeOffset(this, radius, 180.0))
        .include(SphericalUtil.computeOffset(this, radius, 270.0))
        .build();
}

fun GoogleMap.customMaps(context: Context) {
    try {
        setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context, R.raw.google_map_style
            )
        )
    } catch (_: Resources.NotFoundException) {
    }
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
        LatLng(-2.548926, 118.0148634), 3.6f
    )
    animateCamera(cameraUpdate)
}

fun Fragment.getMapAsync(callback: OnMapReadyCallback) {
    val mapFragment = this as SupportMapFragment
    mapFragment.getMapAsync(callback)
}