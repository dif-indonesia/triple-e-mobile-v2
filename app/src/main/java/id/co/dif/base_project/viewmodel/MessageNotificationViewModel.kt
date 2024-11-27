package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.ChatNotification
import id.co.dif.base_project.data.MessageNotification
import id.co.dif.base_project.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MessageNotificationViewModel : BaseViewModel() {

    var responseMessageNotification =
        MutableLiveData<Resource<BaseResponseList<ChatNotification>>>()
    var responseDeleteMessage =
        MutableLiveData<BaseResponse<Any>>()
    var responseDeleteChat =
        MutableLiveData<BaseResponse<Any>>()
    var responseMessageAsRead =
        MutableLiveData<BaseResponse<Any>>()

    fun getMessageNotification(page: Int, limit: Int = 50) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            responseMessageNotification.postValue(Resource.Error(""))
            viewModelJob?.cancel()
        }) {
            responseMessageNotification.postValue(Resource.Loading(true))
            val response = apiServices.getChatNotification(
                bearerToken = "Bearer ${session?.token_access}",
                page = page,
                limit = limit
            )
            responseMessageNotification.postValue(Resource.Success(response))
        }
    }

    fun deleteChat(id: Int) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.deleteChat(
                bearerToken = "Bearer ${session?.token_access}",
                id = id
            )
            responseDeleteChat.postValue(response)
            dissmissLoading()
        }
    }

    fun markChatAsRead(id: Int) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.markChatAsRead(
                bearerToken = "Bearer ${session?.token_access}",
                param = mutableMapOf(
                    "id" to id
                )
            )
            responseMessageAsRead.postValue(response)
            dissmissLoading()
        }
    }

    fun deleteMessage(id: String) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.deleteMessage(
                bearerToken = "Bearer ${session?.token_access}",
                id = id
            )
            responseDeleteMessage.postValue(response)
            dissmissLoading()
        }
    }

    fun markMessageAsRead(id: String) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.markMessageAsRead(
                bearerToken = "Bearer ${session?.token_access}",
                param = mutableMapOf(
                    "id" to id
                )
            )
            responseMessageAsRead.postValue(response)
            dissmissLoading()
        }
    }

    fun cancelJob() {
        viewModelJob?.cancel()
    }


}
