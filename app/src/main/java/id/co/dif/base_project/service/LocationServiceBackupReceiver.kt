package id.co.dif.base_project.service
import LocationServiceBackupWorker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


class LocationServiceBackupReceiver : BroadcastReceiver() {
    private val TAG = "MyReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called")

        val UNIQUE_WORK_NAME = "StartMyServiceViaWorker"

        val request: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            LocationServiceBackupWorker::class.java,
            16,
            TimeUnit.MINUTES
        ).setConstraints(
            Constraints(
                requiredNetworkType = NetworkType.CONNECTED
            )
        ).build()

        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}