package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.Location
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/***
 * Created by kikiprayudi
 * on Monday, 27/02/23 04:00
 *
 */
class HomeViewModel : BaseViewModel() {
    var responseSiteLocation = MutableLiveData<BaseResponseList<Location>>()
    var responseListMapSiteLocation = MutableLiveData<BaseResponseList<Location>>()
    var responseMapAlarm = MutableLiveData<BaseResponseList<Location>>()
    var responseBasicInfoList = MutableLiveData<BaseResponse<BasicInfo>>()
    var mapLoading = MutableLiveData<Boolean>()
    var markerItems = mutableListOf<Location>()
    var hasStarted = false
    lateinit var clusterManager: ClusterManager<Location>
    lateinit var map: GoogleMap

    fun getListSite(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            dismissMapLoading()
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            showMapLoading()
            val response = apiServices.site(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            println(response)
            responseSiteLocation.postValue(response)
            dismissMapLoading()
        }
    }

    fun getListMapSite(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            dismissMapLoading()
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            showMapLoading()
            val response = apiServices.site(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            println(response)
            responseListMapSiteLocation.postValue(response)
            dismissMapLoading()
        }
    }


    private fun showMapLoading() {
        mapLoading.postValue(true)
    }

    private fun dismissMapLoading() {
        mapLoading.postValue(false)
    }

    fun getMapAlarm(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            t.printStackTrace()
            dismissMapLoading()
            viewModelJob?.cancel()
        }) {
            showMapLoading()
            val response = apiServices.mapalarm(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            println(response)
            preferences.lastMapAlarm.value = response.data.list
            responseMapAlarm.postValue(response)
            dismissMapLoading()
        }
    }


}