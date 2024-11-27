package id.co.dif.base_project.persistence

import androidx.lifecycle.MutableLiveData

class LiveDataPreferenceFactory(private val entities: MutableSet<LiveDataPreference<*>>) {
    fun <T> createPreference(
        key: String,
        method: PersistenceOperator,
        isDetachedToPersistence: Boolean = false
    ): MutableLiveData<T> {
        val preference = LiveDataPreference<T>(key, method, isDetachedToPersistence)
        val value by preference
        entities.add(preference)
        return value
    }
}