package id.co.dif.base_project.persistence


interface PersistenceOperator {
    fun <T>save(key: String, data: T?)
    fun <T>get(key: String, default: T): T
    fun <T>get(key: String): T?
    fun <T>edit(key: String, default: T, block: (T) -> Unit)
    fun delete(key: String)
    fun deleteAll()
}