package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Location
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SelectEngineerItemViewModel : BaseViewModel() {
    var responsePingEngineerToSendTheirLocation = MutableLiveData<BaseResponse<Any>>()

    fun pingEngineerToSendTheirLocation(engineerId: Int?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.pingEngineerToSendTheirLocation(
                "Bearer ${session?.token_access}",
                userId = engineerId
            )
            responsePingEngineerToSendTheirLocation.postValue(response)
        }
    }
}