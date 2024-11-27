package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.MessageNotification
import id.co.dif.base_project.data.Notification
import id.co.dif.base_project.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class NotificationViewModel : BaseViewModel() {

    var responseNotification = MutableLiveData<Resource<BaseResponseList<MessageNotification>>>()
    var responseMarkNotificationAsRead =
        MutableLiveData<BaseResponse<Any>>()

    fun getNotification(page: Int = 1, limit: Int = 50) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            responseNotification.postValue(Resource.Error(""))
            viewModelJob?.cancel()
        }) {
            responseNotification.postValue(Resource.Loading(true))
            val response = apiServices.getNotification(
                bearerToken = "Bearer ${session?.token_access}",
                page = page,
                limit = limit
            )
            responseNotification.postValue(Resource.Success(response))
        }
    }

    fun cancelJob() {
        viewModelJob?.cancel()
    }

    fun markNotificationAsRead(id: Int) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            viewModelJob?.cancel()
        }) {
            val response = apiServices.markNotificationAsRead(
                bearerToken = "Bearer ${session?.token_access}",
                param = mutableMapOf(
                    "id" to id
                )
            )
            responseMarkNotificationAsRead.postValue(response)
        }
    }

}
