package id.co.dif.base_project.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.SearchengineerData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SelectSiteViewModel : BaseViewModel() {
    var pingEnginer: Location? = null
    var selectedSite: Location? = null
    var responseListLocation = MutableLiveData<BaseResponseList<Location>>()
    var responseNearestTechnician = MutableLiveData<BaseResponseList<Location>>()
    var responsePingEngineerToSendTheirLocation = MutableLiveData<BaseResponse<Any>>()
    var lastLocationZoomed: Location? = null
    var zoomMaps = true

    fun pingEngineerToSendTheirLocation(engineerId: Int?) {
        showLoading()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            dissmissLoading()
            e.printStackTrace()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.pingEngineerToSendTheirLocation(
                "Bearer ${session?.token_access}",
                userId = engineerId
            )
            dissmissLoading()
            responsePingEngineerToSendTheirLocation.postValue(response)
        }
    }

    fun getListSite(search: String? = null) {
        Log.d("TAG", "gdffdetListSite: ")
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.site(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            responseListLocation.postValue(response)
            dissmissLoading()
        }
    }

    fun getNearestTechnician(idSite: Int? = 0) {
        showLoading()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getNearestTechnician(
                "Bearer ${session?.token_access}",
                idSite = idSite
            )
            responseNearestTechnician.postValue(response)
            dissmissLoading()
        }
    }

}