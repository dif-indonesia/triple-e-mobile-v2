package id.co.dif.base_project.persistence

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import id.co.dif.base_project.utils.ifFalse
import id.co.dif.base_project.utils.ifTrue
import kotlin.reflect.KProperty

class LiveDataPreference<T>(
    val key: String,
    val method: PersistenceOperator,
    var isDetachedFromPersistence: Boolean = false
) :
    PreferenceOperator<T> {

    private val persistence = Preference<T>(key, method)
    private val liveData =
        object : MutableLiveData<T>(if (!isDetachedFromPersistence) persistence.get() else null) {
            override fun postValue(value: T?) {
                save(value)
                super.postValue(value)
            }

            override fun setValue(value: T?) {
                save(value)
                super.setValue(value)
            }
        }

    fun detachPersistence() {
        isDetachedFromPersistence = true
    }

    fun attachPersistence() {
        isDetachedFromPersistence = false
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): MutableLiveData<T> {
        return liveData
    }

    override fun save(data: T?) {
        if (doDetachmentCheck() == Detachment.DETACHED) return
        persistence.save(data)
    }

    override fun get(default: T): T {
        if (doDetachmentCheck() == Detachment.DETACHED) return default
        return persistence.get(default)
    }

    override fun get(): T? {
        if (doDetachmentCheck() == Detachment.DETACHED) return null
        return persistence.get()
    }

    override fun delete() {
        if (doDetachmentCheck() == Detachment.DETACHED) return
        persistence.delete()
        liveData.setValue(null)
    }

    override fun edit(default: T, block: (T) -> Unit) {
        if (doDetachmentCheck() == Detachment.DETACHED) return
        persistence.edit(default, block)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LiveDataPreference<*>) return false

        // Compare the key, method, and class types
        return key == other.key &&
                method == other.method &&
                javaClass == other.javaClass
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + javaClass.hashCode()
        return result
    }


    @SuppressLint("LogNotTimber")
    fun doDetachmentCheck(): Detachment {
        if (isDetachedFromPersistence) {
            Log.w(
                this::class.java.simpleName,
                "Persistence is detached. Cannot do any persistence operation!"
            )
            return Detachment.DETACHED
        }
        return Detachment.ATTACHED
    }

    enum class Detachment {
        DETACHED,
        ATTACHED
    }

}


