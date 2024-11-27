package id.co.dif.base_project.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Chat
import id.co.dif.base_project.data.Employee
import id.co.dif.base_project.data.UnreadNumber
import id.co.dif.base_project.databinding.LayoutNotificationTabBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class InboxViewModel : BaseViewModel() {

    var responseUnreadNumber = MutableLiveData<BaseResponse<UnreadNumber>>()
    var responseSendMessages = MutableLiveData<BaseResponse<Chat>>()
    val tabBindings: MutableList<LayoutNotificationTabBinding> = mutableListOf()
    var responseListName = MutableLiveData<BaseResponseList<Employee>>()
    fun getListName(context: Context?, search: String? = null) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            handleApiError(throwable)
        }) {
            val response = apiServices.listName(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
                page = null,
                limit = null
            )
            println(response)
            responseListName.postValue(response)
        }
    }


    fun sendMessages(param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
        }) {
            val response = apiServices.sendChat(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            dissmissLoading()
            responseSendMessages.postValue(response)
        }
    }

    fun getUnreadNumber() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            handleApiError(throwable)
        }) {
            val response = apiServices.getUnreadNumber(
                bearerToken = "Bearer ${session?.token_access}",
            )
            responseUnreadNumber.postValue(response)
        }
    }

    fun cancelJob() {
        viewModelJob?.cancel()
    }


}