package id.co.dif.base_project.presentation.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.Regional
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AreaAccesViewModel : BaseViewModel() {

    var responseAreaAcces = MutableLiveData<BaseResponseList<Regional>>()
    var responseMyAreaAcces = MutableLiveData<BaseResponseList<Regional>>()
    var responseUpdateMyAreaAcces = MutableLiveData<BaseResponse<Any>>()

    fun getAreaAcces() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getListRegional(
                "Bearer ${session?.token_access}"
            )
            responseAreaAcces.postValue(response)
            dissmissLoading()
        }
    }

    fun getMyAreaAcces() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getMyListRegional(
                "Bearer ${session?.token_access}",
                session?.id
            )
            responseMyAreaAcces.postValue(response)
            dissmissLoading()
        }
    }

    fun updateMyAreaAcces(param: HashMap<String, MutableList<String>>) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.updateMyListRegional(
                "Bearer ${session?.token_access}",
                param
            )
            responseUpdateMyAreaAcces.postValue(response)
            dissmissLoading()
        }
    }

}
