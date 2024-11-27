package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.LoginData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SessionLoginViewModel : BaseViewModel () {

    var responseSessionLogin = MutableLiveData<BaseResponse<LoginData?>>()

    fun postSessionLogin(email: String, password: String) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            viewModelJob?.cancel()
            dissmissLoading()
            val response = BaseResponse<LoginData?>(
                data = null,
                message = throwable.localizedMessage ?: "",
                status = 400
            )
            responseSessionLogin.postValue(response)
            handleApiError(throwable)
        }) {
            delay(2000)
            val response = apiServices.postLogin(
                mutableMapOf(
                    "email" to email,
                    "password" to password
                )
            )
            dissmissLoading()
            responseSessionLogin.postValue(response)
        }
    }

}