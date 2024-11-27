package id.co.dif.base_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.FirebaseApp
import com.orhanobut.hawk.Hawk
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import id.co.dif.base_project.di.koinModules
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named

/***
 * Created by kikiprayudi
 * on Tuesday, 28/02/23 10:47
 *
 */
class MainApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
        FirebaseApp.initializeApp(this)
        appContext = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        startKoin {
            androidLogger()
            androidContext(this@MainApp)
            modules(koinModules)
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener(this))
    }

    companion object {
        lateinit var appContext: Context
    }
}