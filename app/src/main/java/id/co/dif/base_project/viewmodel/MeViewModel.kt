package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.PlainValueLabel
import id.co.dif.base_project.data.Session
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


class MeViewModel : BaseViewModel() {

    var responseDetailedProfile = MutableLiveData<BaseResponse<BasicInfo>>()
    var responseeditProfileList = MutableLiveData<BaseResponse<MutableMap<String, Any?>>>()
    var responseGetOtp = MutableLiveData<BaseResponse<Session>>()
    var responseUserActivityLog = MutableLiveData<BaseResponseList<PlainValueLabel>>()


    fun getDetailProfile(id: Int?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getDetailProfile(
                bearerToken = "Bearer ${session?.token_access}",
                id = id
            )
            dissmissLoading()
            responseDetailedProfile.postValue(response)
        }
    }

    fun getUserActivityLog(id: Int?) {
        viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
        }) {
            val response = apiServices.getUserActivityLog(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            responseUserActivityLog.postValue(
                response
            )
        }
    }
}