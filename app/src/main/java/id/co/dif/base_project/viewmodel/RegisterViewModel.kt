package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Session
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class RegisterViewModel : BaseViewModel() {

    var responseRegister = MutableLiveData<BaseResponse<Session>>()

    fun postRegister (param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            viewModelJob?.cancel()
            dissmissLoading()
            handleApiError(throwable)
        }) {
            val response = apiServices.postRegister(
            param
            )
            dissmissLoading()
            responseRegister.postValue(response)
        }
    }

}