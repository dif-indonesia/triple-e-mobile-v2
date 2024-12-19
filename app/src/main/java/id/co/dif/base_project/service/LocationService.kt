package id.co.dif.base_project.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import id.co.dif.base_project.END_HOUR_LOCATION_SCHEDULE
import id.co.dif.base_project.END_MINUTE_LOCATION_SCHEDULE
import id.co.dif.base_project.LOCATION_UPDATE_INTERVAL
import id.co.dif.base_project.R
import id.co.dif.base_project.START_HOUR_LOCATION_SCHEDULE
import id.co.dif.base_project.START_MINUTE_LOCATION_SCHEDULE
import id.co.dif.base_project.data.LastLocation
import id.co.dif.base_project.data.LocationScheduleCommand
import id.co.dif.base_project.persistence.Preferences
import id.co.dif.base_project.utils.calculateIntervalToClosestSchedules
import id.co.dif.base_project.utils.constructLastLocation
import id.co.dif.base_project.utils.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.Calendar

class LocationService : Service(), KoinComponent {
    val apiServices by inject<ApiServices>()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val locationClient: LocationClient by inject()
    private val preferences: Preferences by inject()
    private val database = Firebase.database
    private var job: Job? = null
    private var userRef: DatabaseReference? = null
    private var hasLoggedOut = false
    private val locationPingListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Timber.d("PingLocation: " + preferences.myDetailProfile.value?.id + " " + snapshot)
            locationClient.getCurrentLocation { location ->
                location?.let {
                    saveLastLocation(it)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) = Unit
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val notification = buildForegroundNotification()
        startForeground(1, notification)
    }

    private fun updateLocation(lastLocation: LastLocation) {
        preferences.session.value?.let { session ->
            job = serviceScope.launch(CoroutineExceptionHandler { _, throwable ->
                job?.cancel()
                throwable.printStackTrace()
            }) {
                apiServices.putUpdateLocation(
                    bearerToken = "Bearer ${session.token_access}",
                    param = mutableMapOf(
                        "emp_latitude" to lastLocation.latitude,
                        "emp_longtitude" to lastLocation.longitude,
                    )
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action.log("service action")) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun start() {
        "location start".log("location update")
        val myProfile = preferences.myDetailProfile.value
        val rootRef = database.getReference("/")
        Timber.tag(TAG).d("onCreate: service started ")
        Timber.tag(TAG).d("start: " + myProfile?.id)
        userRef?.removeEventListener(locationPingListener)
        userRef = rootRef.child(myProfile?.id.toString())
        userRef?.addValueEventListener(locationPingListener)
        val notification = buildForegroundNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
        startForeground(1, notification)
        locationClient
            .getLocationUpdates(
                preferences.locationUpdateInterval.value ?: LOCATION_UPDATE_INTERVAL
            )
            .catch { e ->
                e.printStackTrace()
            }
            .onEach { location ->
                location.log("location update")
                val notification = buildForegroundNotification()
                notificationManager.notify(1, notification)
                saveLastLocation(location)
            }
            .launchIn(serviceScope)

        isServiceRunning = true
    }



    private fun buildForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, "location")
            .setContentTitle(getString(R.string.triple_e_is_running))
            .setContentText(getString(R.string.triple_e_is_running_in_the_background_in_working_hours))
            .setSmallIcon(R.drawable.logo_triple_e)
            .setOngoing(true).build()
    }

    private fun stop() {
        Timber.d("stop: Service Stopped")
        stopForeground(true)
        stopSelf()
    }

    fun saveLastLocation(location: Location) {
        Timber.tag(TAG).d("saveLastLocation: %s", location)
        preferences.lastLocation.let { lastLocation ->
            val newLastLocation = constructLastLocation(location)
            lastLocation.postValue(newLastLocation)
            updateLocation(newLastLocation)
        }
    }

    override fun onDestroy() {
        isServiceRunning = false
        Timber.d("stop: Service Destroyed")
        stopForeground(true)
        serviceScope.cancel()
        performLogoutIfNeeded("onDestroy")
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stop()
        performLogoutIfNeeded("onTaskRemoved")
    }

    private fun performLogoutIfNeeded(caller: String) {
        if (hasLoggedOut) {
            Timber.d("Logout sudah dilakukan, skip untuk $caller")
            return
        }
        hasLoggedOut = true // Tandai bahwa logout telah dilakukan

        preferences.session.value?.let { session ->
            GlobalScope.launch(CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                Timber.e("Logout gagal di $caller: ${throwable.message}")
            }) {
                try {
                    Timber.d("Logout request dimulai di $caller")
                    val response = apiServices.postSesionLog(
                        bearerToken = "Bearer ${session.token_access}",
                        "logout"
                    )
                    Timber.d("Logout berhasil di $caller: $response")
                } catch (e: Exception) {
                    Timber.e("Logout gagal di $caller: ${e.message}")
                }
            }
        }
    }

    companion object {
        var isServiceRunning = false
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        val TAG: String = this::class.java.name
    }
}