package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class EditProfileViewModel : BaseViewModel() {
    var responseeditProfileList = MutableLiveData<BaseResponse<MutableMap<String, Any?>>>()
    var responseeuploadfile = MutableLiveData<BaseResponse<MutableMap<String, Any?>>>()
    var file: File? = null

    fun handleUpdateProfile(param: MutableMap<String, Any?>) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.editprofile(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            responseeditProfileList.postValue(response)
            dissmissLoading()
        }
    }

    fun updateprofile(param: MultipartBody.Part) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            dissmissLoading()
            e.printStackTrace()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.uploadphoto(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            responseeditProfileList.postValue(response)
            dissmissLoading()
        }
    }

    fun updatecover(param: MultipartBody.Part) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            viewModelJob?.cancel()
            e.printStackTrace()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.uploadcover(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            responseeditProfileList.postValue(response)
            dissmissLoading()
        }
    }

    fun uploadfile(param: MutableList<MultipartBody.Part?>) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
        }) {
            val response = apiServices.uploadfile(
                bearerToken = "Bearer ${session?.token_access}",
                param = param
            )
//            responseeuploadfile.postValue(response)
        }
    }

}