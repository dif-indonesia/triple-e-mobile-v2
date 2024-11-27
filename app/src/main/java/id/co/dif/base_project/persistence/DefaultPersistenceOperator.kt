package id.co.dif.base_project.persistence

import com.orhanobut.hawk.Hawk

class DefaultPersistenceOperator : PersistenceOperator {
    override fun <T>save(key: String, data: T?) {
        Hawk.put(key, data)
    }

    override fun <T> get(key: String, default: T): T {
        return Hawk.get(key, default)
    }

    override fun <T>get(key: String): T? {
        return Hawk.get(key, null)
    }

    override fun <T>edit(key: String, default: T, block: (T) -> Unit) {
        val data = get<T>(key)
        if (data == null) {
            block(default)
        } else {
            block(data)
        }
        return save(key, data)
    }

    override fun delete(key: String) {
        Hawk.delete(key)
    }

    override fun deleteAll() {
        Hawk.deleteAll()
    }
}