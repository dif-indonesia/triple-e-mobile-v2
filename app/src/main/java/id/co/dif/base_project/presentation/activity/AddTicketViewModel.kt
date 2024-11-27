package id.co.dif.base_project.presentation.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.SiteData
import id.co.dif.base_project.data.TroubleTicket
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class AddTicketViewModel : BaseViewModel() {
    var responseaddticket = MutableLiveData<BaseResponse<TroubleTicket>>()
    var responseGetSiteDetails = MutableLiveData<BaseResponse<SiteData>>()
    var responseeuploadfile = MutableLiveData<BaseResponse<Any>>()
    var responseNearestTechnician = MutableLiveData<BaseResponseList<Location>>()
    var selectedSite = MutableLiveData<Location?>()
    var selectedEngineer = MutableLiveData<Location?>()
    var responseSiteLocation = MutableLiveData<BaseResponseList<Location>>()
    var file: File? = null
    var sparepart: String? = null

    fun addTicket(param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.addticket(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            dissmissLoading()
            responseaddticket.postValue(response)
        }
    }

    fun getNearestTechnician(idSite: Int? = null) {
        showLoading()
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
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

    fun getSiteById(id: Int? = null) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getSiteById(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            dissmissLoading()
            responseGetSiteDetails.postValue(response)
        }
    }

    fun getListSite(search: String? = null) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            val response = apiServices.site(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            println(response)
            responseSiteLocation.postValue(response)
        }
    }

    fun uploadFile(id: String?, param: MutableList<MultipartBody.Part?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.d("TAG", "uploadfile: ${e.stackTraceToString()}")
            dissmissLoading()
        }) {
            showLoading()
            Log.d("TAG", "uploadfile: $param")
            apiServices.uploadfileticket(
                bearerToken = "Bearer ${session?.token_access}",
                id = id,
                param = param
            )
            dissmissLoading()
        }
    }


}
