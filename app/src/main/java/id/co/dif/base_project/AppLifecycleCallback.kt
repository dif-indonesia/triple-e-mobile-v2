package id.co.dif.base_project

import android.app.Activity
import android.app.Application
import android.os.Bundle

class AppLifecycleCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // Activity created
    }

    override fun onActivityStarted(activity: Activity) {
        // Activity started
    }

    override fun onActivityResumed(activity: Activity) {
        // Activity resumed (back in the foreground)
    }

    override fun onActivityPaused(activity: Activity) {
        // Activity paused (going into the background)
    }

    override fun onActivityStopped(activity: Activity) {
        // Activity stopped
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // Activity's state saved
    }

    override fun onActivityDestroyed(activity: Activity) {
        // Activity destroyed
    }
}

