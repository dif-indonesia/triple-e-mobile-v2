package id.co.dif.base_project.service

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>
    fun getCurrentLocation(onLocationUpdate: (Location?) -> Unit)
    class LocationException(message: String): Exception()
}