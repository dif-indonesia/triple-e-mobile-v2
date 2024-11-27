package id.co.dif.base_project.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import id.co.dif.base_project.ALARM_REQUEST_CODE
import id.co.dif.base_project.END_HOUR_LOCATION_SCHEDULE
import id.co.dif.base_project.END_MINUTE_LOCATION_SCHEDULE
import id.co.dif.base_project.START_HOUR_LOCATION_SCHEDULE
import id.co.dif.base_project.START_MINUTE_LOCATION_SCHEDULE
import id.co.dif.base_project.data.LocationScheduleCommand
import id.co.dif.base_project.persistence.Preferences
import id.co.dif.base_project.utils.calculateIntervalToClosestSchedules
import id.co.dif.base_project.utils.isTodayIsWeekend
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.M)
class LocationServiceStarterScheduleReceiver : BroadcastReceiver(), KoinComponent {
    private val preference: Preferences by inject()
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            LocationService.ACTION_STOP -> {}
            else -> {
                startNextAlarm(context)
                val type = when (preference.nextLocationScheduleCommand.value) {
                    LocationScheduleCommand.START -> LocationService.ACTION_STOP
                    LocationScheduleCommand.STOP -> LocationService.ACTION_START
                    null -> LocationService.ACTION_START
                }
                try{
                    Intent(context, LocationService::class.java).apply {
                        action = type
                        context.startService(this)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }


    }

    private fun startNextAlarm(context: Context) {
        var (interval, nextCommand) = calculateIntervalToClosestSchedules(
            startHour = START_HOUR_LOCATION_SCHEDULE,
            startMinute = START_MINUTE_LOCATION_SCHEDULE,
            endHour = END_HOUR_LOCATION_SCHEDULE,
            endMinute = END_MINUTE_LOCATION_SCHEDULE
        )

        if (isTodayIsWeekend()) nextCommand = LocationScheduleCommand.START
        preference.nextLocationScheduleCommand.value = nextCommand
        val alarmIntent = Intent(context, LocationServiceStarterScheduleReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val cal = Calendar.getInstance()
        cal.add(Calendar.SECOND, interval)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            pendingIntent
        )
    }

    companion object {
        var status = true
    }
}