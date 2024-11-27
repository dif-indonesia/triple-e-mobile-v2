package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.CompletedProfile
import id.co.dif.base_project.data.Education
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class OverviewViewModel : BaseViewModel() {

    var responseBasicInfoList = MutableLiveData<BaseResponse<BasicInfo>>()
    var geteducationresponse = MutableLiveData<BaseResponseList<Education>>()
    var responseCompletedProfile = MutableLiveData<BaseResponse<CompletedProfile>>()
    var responseSkillList = MutableLiveData<BaseResponse<BasicInfo>>()


    fun skilllist(id: Int?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.getDetailProfile(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseSkillList.postValue(response)
            dissmissLoading()
        }
    }
    fun getDetailedProfile(id: Int?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getDetailProfile(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseBasicInfoList.postValue(response)
            dissmissLoading()
        }
    }

    fun getCompletedProfile(id: Int? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
        }) {
            val response = apiServices.getCompletedProfile(
                bearerToken = "Bearer ${session?.token_access}",
                id = id
            )
            responseCompletedProfile.postValue(
                response
            )
        }
    }


    fun getEducationList(id: Int) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            t.printStackTrace()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getEducationList(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            geteducationresponse.postValue(response)
        }
    }



}
