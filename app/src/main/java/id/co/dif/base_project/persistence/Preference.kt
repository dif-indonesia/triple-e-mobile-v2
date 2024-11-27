package id.co.dif.base_project.persistence

import kotlin.reflect.KProperty

class Preference<DataType>(
    private val key: String,
    var method: PersistenceOperator,
) : PreferenceOperator<DataType> {

    override fun save(data: DataType?) {
        method.save(key, data)
    }

    override fun get(default: DataType): DataType {
        return method.get(key, default)
    }

    override fun get(): DataType? {
        return method.get(key)
    }

    override fun edit(default: DataType, block: (DataType) -> Unit) {
        val data = get()
        if (data == null) {
            block(default)
        } else {
            block(data)
        }
        save(data)
    }

    override fun delete() {
        method.delete(key)
    }


    operator fun getValue(thisRef: Any?, property: KProperty<*>): DataType? {
        return get()
    }


    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: DataType?
    ) {
        save(value)
    }


}