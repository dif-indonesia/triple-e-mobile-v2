package id.co.dif.base_project.utils

sealed class Resource<T>(val data: T? = null, val message: String?) {
    class Success<T>(data: T?) : Resource<T>(data, message = "")
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(null, null)

}