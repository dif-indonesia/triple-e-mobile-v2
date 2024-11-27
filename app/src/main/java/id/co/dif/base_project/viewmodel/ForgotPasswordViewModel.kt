package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.ResponseForgetPassword
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : BaseViewModel() {

    var responseForgotPassword = MutableLiveData<BaseResponse<ResponseForgetPassword>>()
    var responseResetPassowrd = MutableLiveData<BaseResponse<Any>>()

    fun resetPassword (newPassword:String, confirmPassword:String, code:String) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            viewModelJob?.cancel()
            dissmissLoading()
            handleApiError(throwable)
        }) {
            delay(2000)
            val response = apiServices.resetPassword(
                mutableMapOf(
                    "new_password" to newPassword,
                    "confirm_password" to confirmPassword,
                    "code" to code
                )
            )
            dissmissLoading()
            responseResetPassowrd.postValue(response)
        }
    }

    fun forgotPassord (email:String) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            viewModelJob?.cancel()
            dissmissLoading()
            handleApiError(throwable)
        }) {
            delay(2000)
            val response = apiServices.forgetpassword(
                mutableMapOf(
                    "email" to email,
                )
            )
            dissmissLoading()
            responseForgotPassword.postValue(response)
        }
    }

}
