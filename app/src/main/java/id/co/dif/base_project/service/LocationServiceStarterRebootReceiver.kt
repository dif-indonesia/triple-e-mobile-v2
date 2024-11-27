package id.co.dif.base_project.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import id.co.dif.base_project.data.LocationScheduleCommand
import id.co.dif.base_project.persistence.Preferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationServiceStarterRebootReceiver : BroadcastReceiver(), KoinComponent {
    val preferences: Preferences by inject()
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("LocationUpdate", "startAlarmManager Location Service Reboot")
        val type = when (preferences.nextLocationScheduleCommand.value) {
            LocationScheduleCommand.START -> LocationService.ACTION_STOP
            LocationScheduleCommand.STOP -> LocationService.ACTION_START
            null -> LocationService.ACTION_START
        }
        Intent(context, LocationService::class.java).apply {
            action = type

            context.startService(this)
        }
    }
}