import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import id.co.dif.base_project.END_HOUR_LOCATION_SCHEDULE
import id.co.dif.base_project.END_MINUTE_LOCATION_SCHEDULE
import id.co.dif.base_project.START_HOUR_LOCATION_SCHEDULE
import id.co.dif.base_project.START_MINUTE_LOCATION_SCHEDULE
import id.co.dif.base_project.data.LastLocation
import id.co.dif.base_project.data.LocationScheduleCommand
import id.co.dif.base_project.persistence.Preferences
import id.co.dif.base_project.service.ApiServices
import id.co.dif.base_project.service.LocationClient
import id.co.dif.base_project.service.LocationService
import id.co.dif.base_project.utils.calculateIntervalToClosestSchedules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar


class LocationServiceBackupWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {
    private val TAG = "LocationWorker"
    val apiServices by inject<ApiServices>()
    private val locationClient: LocationClient by inject()
    private val preferences: Preferences by inject()
    var count = 0


    override suspend fun doWork(): Result {
        Log.d(TAG, "startAlarmManager doWork: ")
        val (_, nextCommand) = calculateIntervalToClosestSchedules(
            startHour = START_HOUR_LOCATION_SCHEDULE,
            startMinute = START_MINUTE_LOCATION_SCHEDULE,
            endHour = END_HOUR_LOCATION_SCHEDULE,
            endMinute = END_MINUTE_LOCATION_SCHEDULE
        )
        if (nextCommand == LocationScheduleCommand.START || LocationService.isServiceRunning)  return Result.success()
        locationClient.apply {
            getCurrentLocation { location ->
                location?.let {
                    val lastLocation = LastLocation(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        lastUpdate = Calendar.getInstance().timeInMillis,
                    )
                    preferences.lastLocation.postValue(lastLocation)
                    runBlocking {
                        updateLocation(lastLocation)
                    }
                }
            }
        }


        return Result.success()
    }

    private suspend fun updateLocation(lastLocation: LastLocation) {

        preferences.session.value?.let { session ->
            try {
                apiServices.putUpdateLocation(
                    bearerToken = "Bearer ${session.token_access}",
                    param = mutableMapOf(
                        "emp_latitude" to lastLocation.latitude,
                        "emp_longtitude" to lastLocation.longitude,
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
