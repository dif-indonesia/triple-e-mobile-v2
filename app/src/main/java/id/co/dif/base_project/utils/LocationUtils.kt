package id.co.dif.base_project.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Looper
import android.provider.Settings
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 10:09
 *
 */


fun checkLocationServices(context: Context, onEnable: () -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var gpsEnabled = false
    var networkEnabled = false
    try {
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    if (!gpsEnabled && !networkEnabled) {
        context.startActivity(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        )
    } else {
        onEnable()
    }
}


@SuppressLint("MissingPermission")
fun locationUpdate(context: Context, onLocationUpdate: (Location) -> Unit) {
    val locationProvider = LocationServices.getFusedLocationProviderClient(
        context
    )
    locationProvider.lastLocation.addOnSuccessListener { location ->
        onLocationUpdate(location)
    }
    locationProvider.requestLocationUpdates(
        LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        },
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val location = result.locations.firstOrNull()
                if (location != null) {
                    onLocationUpdate(location)
                }
            }

        }, Looper.getMainLooper()
    )
}

@SuppressLint("MissingPermission")
fun fixLocation(context: Context, onHaveLocation: (Location?) -> Unit) {
    val locationProvider = LocationServices.getFusedLocationProviderClient(
        context
    )
    locationProvider.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onHaveLocation(location)
        } else {
            locationProvider.requestLocationUpdates(
                LocationRequest.create().apply {
                    interval = 5000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                },
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        val location = result.locations.firstOrNull()
                        if (location != null) {
                            locationProvider.removeLocationUpdates(this)
                            onHaveLocation(location)
                        } else {
                            onHaveLocation(null)
                        }
                    }

                }, Looper.getMainLooper()
            )
        }
    }

}

fun addressFromLatLong(context: Context, latLng: LatLng): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addressList = mutableListOf<Address>()
    geocoder.getFromLocation(
        latLng.latitude,
        latLng.longitude,
        1
    )?.let {
        addressList.addAll(
            it
        )
    }
    return if (addressList.isNotEmpty()) {
        addressList[0].getAddressLine(0)
    } else {
        "-"
    }
}


fun zoomLatLng(lat1: Double?, lng1: Double?, lat2: Double?, lng2: Double?): Float {
    val loc1 = Location("loc1").apply {
        latitude = lat1 ?: 0.0
        longitude = lng1 ?: 0.0
    }
    val loc2 = Location("loc2").apply {
        latitude = lat2 ?: 0.0
        longitude = lng2 ?: 0.0
    }
    val distanceInMeters = loc1.distanceTo(loc2)
    return when {
        distanceInMeters >= 18489298.45 -> 1f
        distanceInMeters >= 9244649.22 -> 2f
        distanceInMeters >= 4622324.61 -> 3f
        distanceInMeters >= 2311162.30 -> 4f
        distanceInMeters >= 1155581.15 -> 5f
        distanceInMeters >= 577790.57 -> 6f
        distanceInMeters >= 288895.28 -> 7f
        distanceInMeters >= 144447.64 -> 8f
        distanceInMeters >= 72223.82 -> 9f
        distanceInMeters >= 36111.91 -> 10f
        distanceInMeters >= 18055.95 -> 11f
        distanceInMeters >= 9027.97 -> 12f
        distanceInMeters >= 4513.98 -> 13f
        distanceInMeters >= 2256.99 -> 14f
        distanceInMeters >= 1128.49 -> 15f
        else -> 16f
    }
}

fun zoomByDistance(distanceInMeters: Float): Float {
    return when {
        distanceInMeters >= 18489298.45 -> 1f
        distanceInMeters >= 9244649.22 -> 2f
        distanceInMeters >= 4622324.61 -> 3f
        distanceInMeters >= 2311162.30 -> 4f
        distanceInMeters >= 1155581.15 -> 5f
        distanceInMeters >= 577790.57 -> 6f
        distanceInMeters >= 288895.28 -> 7f
        distanceInMeters >= 144447.64 -> 8f
        distanceInMeters >= 72223.82 -> 9f
        distanceInMeters >= 36111.91 -> 10f
        distanceInMeters >= 18055.95 -> 11f
        distanceInMeters >= 9027.97 -> 12f
        distanceInMeters >= 4513.98 -> 13f
        distanceInMeters >= 2256.99 -> 14f
        distanceInMeters >= 1128.49 -> 15f
        else -> 16f
    }
}

fun distanceLatLong(lat1: Double?, lng1: Double?, lat2: Double?, lng2: Double?): String {
    val loc1 = Location("loc1").apply {
        latitude = lat1 ?: 0.0
        longitude = lng1 ?: 0.0
    }
    val loc2 = Location("loc2").apply {
        latitude = lat2 ?: 0.0
        longitude = lng2 ?: 0.0
    }
    val distanceInMeters = loc1.distanceTo(loc2)
    return if (distanceInMeters < 100) {
        "${String.format("%.2f", distanceInMeters)} m"
    } else {
        "${String.format("%.2f", distanceInMeters / 1000)} km"
    }
}

fun LatLng.distanceTo(location: LatLng): Float {
    val fromLocation = this
    val loc1 = Location("loc1").apply {
        latitude = fromLocation.latitude
        longitude = fromLocation.longitude
    }
    val loc2 = Location("loc2").apply {
        latitude = location.latitude
        longitude = location.longitude
    }
    return loc1.distanceTo(loc2)
}

fun LatLng.closestDistance(locations: List<LatLng>): Float {
    val distances = locations.map { latLng -> latLng.distanceTo(this) }
    return distances.minOrNull() ?: 0f
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371e3 // Radius bumi dalam meter
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return earthRadius * c
}
