package id.co.dif.base_project.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.DirectionsResponse
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.presentation.fragment.MapsTicketFragment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MapsTicketViewModel : BaseViewModel() {
    var responseNearestTechnician = MutableLiveData<BaseResponseList<Location>>()
    var responseDetailedProfile = MutableLiveData<BaseResponse<BasicInfo>>()
    var responseGetEstimateTimeEngineer = MutableLiveData<BaseResponse<Any?>>()
    lateinit var map: GoogleMap
    lateinit var clusterManager: ClusterManager<Location>

    fun getNearestTechnician(idSite: Int? = 0, onError: () -> Unit, onResult: () -> Unit, onLoading: () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            onError()
            viewModelJob?.cancel()
        }) {
            onLoading()
            val response = apiServices.getNearestTechnician(
                "Bearer ${session?.token_access}",
                idSite = idSite
            )
            responseNearestTechnician.postValue(response)
            onResult()
        }
    }

    fun getDetailProfile(id: Int?) {
        viewModelJob?.cancel()
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            e.printStackTrace()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getDetailProfile(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseDetailedProfile.postValue(response)
        }
    }

    var responseDirection = MutableLiveData<DirectionsResponse>()

    fun getDirection(site: LatLng, technician: LatLng) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            Toast.makeText(context, "Cannot create direction. There is something wrong!", Toast.LENGTH_SHORT).show()
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.getDirection(
                mutableMapOf(
                    "origin" to "${technician.latitude}, ${technician.longitude}",
                    "destination" to "${site.latitude}, ${site.longitude}"
                ),
                bearerToken = "Bearer ${session?.token_access}"
            )
            responseDirection.postValue(response)
            dissmissLoading()
        }
    }

    fun getEstimateTimeDirection(id: String? = null, param: MutableMap<String, Any?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            dissmissLoading()
            responseGetEstimateTimeEngineer.postValue(BaseResponse(status = 500, message = "Get Estimate failed", data = null)) // Menangani kegagalan dengan data null
        }) {
            showLoading()
            try {
                val response = apiServices.getEstimateTimeEngineer(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id,
                    param = param
                )
                responseGetEstimateTimeEngineer.postValue(BaseResponse(status = response.status, message = response.message, data = response.data)) // Menggunakan data respons API
            } catch (e: Exception) {
                Log.e("PermitViewModel", "Approval failed: ${e.message}")
                responseGetEstimateTimeEngineer.postValue(BaseResponse(status = 500, message = "Get Estimate failed", data = null)) // Menggunakan data null untuk error
            } finally {
                dissmissLoading()
            }
        }
    }

}