package id.co.dif.base_project

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import id.co.dif.base_project.data.Session
import id.co.dif.base_project.persistence.Preferences
import id.co.dif.base_project.service.ApiServices
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Date

class AppLifecycleListener(mainApp: MainApp) : DefaultLifecycleObserver, KoinComponent {

    val apiServices by inject<ApiServices>()
    val preferences: Preferences by inject()
    var session: Session? = null
        get() = preferences.session.value
    var lastLogin: String? = null
        get() = preferences.lastLogin.value

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

        val dateNow = DateTime.now().toString("YYYYMMdd")

        if (session != null && lastLogin != dateNow) {
            owner.lifecycleScope.launch {
                try {
                    val response = apiServices.postSesionLog(
                        bearerToken = "Bearer ${session?.token_access}",
                        "login"
                    )
                    preferences.lastLogin.value = dateNow
                } catch (e: Exception) {

                }
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (session != null)
            owner.lifecycleScope.launch {
                try {
                    val response = apiServices.postSesionLog(
                        bearerToken = "Bearer ${session?.token_access}",
                        "logout"
                    )
                } catch (e: Exception) {

                }
            }

    }


}
