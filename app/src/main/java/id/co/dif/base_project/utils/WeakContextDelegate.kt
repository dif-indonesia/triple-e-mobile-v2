package id.co.dif.base_project.utils

import android.content.Context
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

class WeakContextDelegate<T : Context> {
    private var weakReference: WeakReference<T>? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return weakReference?.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        weakReference = if (value != null) {
            WeakReference(value)
        } else {
            null
        }
    }
}