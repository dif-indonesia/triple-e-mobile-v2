package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.LatestAppVersion
import id.co.dif.base_project.data.NotificationUnreadStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {
    var periodicOfflineConnectivityIsRunning: Boolean = false
    var responseNotificationUnreadStatus = MutableLiveData<BaseResponse<NotificationUnreadStatus>>()
    var responseCheckLatestAppVersion = MutableLiveData<BaseResponse<LatestAppVersion>>()


    fun getNotificationUnreadStatus() {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getNotificationUnreadStatus(
                bearerToken = "Bearer ${session?.token_access}",
            )
            responseNotificationUnreadStatus.postValue(response)
        }
    }

    fun getLatestAppVersion() {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getLatestAppVersion(
                url = "https://api-assets.triple-e.id:5051/api/v1/assets/check_version",
                bearerToken = "Bearer ${session?.token_access}",
            )
            responseCheckLatestAppVersion.postValue(response)
        }
    }





}