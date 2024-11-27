package id.co.dif.base_project.persistence

interface PreferenceOperator<DataType> {

    fun save(data: DataType?)
    fun get(default: DataType): DataType
    fun get(): DataType?
    fun edit(default: DataType, block: (DataType) -> Unit)
    fun delete()
}