package id.co.dif.base_project.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.EngineerWithinRadiusStatus
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.SiteData
import id.co.dif.base_project.data.SiteDetails
import id.co.dif.base_project.data.SparepartData
import id.co.dif.base_project.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class DetailViewModel : BaseViewModel() {

    var responseGetEngineerIsWithinRadius = MutableLiveData<BaseResponse<EngineerWithinRadiusStatus>>()
    var responseeditticket = MutableLiveData<BaseResponse<SiteData>>()
    var responseUploadfile = MutableLiveData<Resource<BaseResponse<Any>>>()
    var responseReOpenTicket = MutableLiveData<Resource<BaseResponse<Any>>>()
    var responseEditNotes = MutableLiveData<BaseResponse<MutableMap<String, Any?>>>()
    var responseaddsparepart = MutableLiveData<BaseResponse<SparepartData>>()
    var responseCheckSparepart  = MutableLiveData<BaseResponse<Any?>>()
    var responseGetSiteDetails = MutableLiveData<BaseResponse<SiteData>>()
    var responseNearestTechnician = MutableLiveData<BaseResponseList<Location>>()
    var responseSiteLocation = MutableLiveData<BaseResponseList<Location>>()
    var responseRequestPending = MutableLiveData<BaseResponse<Any?>>()

    var selectedSite = MutableLiveData<Location?>()
    var selectedEngineer = MutableLiveData<Location?>()

    var file: File? = null
    var articleName = ""
    var engineerIsWithinRadius = false
    var sparepart: String? = null
    var requestPending: Boolean? = false

    fun editTicket(id: Int?, param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.editdetailticket(
                bearerToken = "Bearer ${session?.token_access}",
                param = param,
                id = id
            )
            dissmissLoading()
            responseeditticket.postValue(response)
        }
    }

    fun editNotes(id: Int?, param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.editNotes(
                bearerToken = "Bearer ${session?.token_access}",
                id = id,
                param
            )
            dissmissLoading()
            responseEditNotes.postValue(response)
        }
    }

    fun getEngineerIsWithinRadius(engineerId: Int?, siteId: Int?) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getEngineerIsWithinRadius(
                bearerToken = "Bearer ${session?.token_access}",
                engineerId = engineerId,
                siteId = siteId
            )
            dissmissLoading()
            responseGetEngineerIsWithinRadius.postValue(response)
        }
    }


    fun addSparePart(param: MutableMap<String, Any?>) {
//        showLoading()
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
//            dissmissLoading()
//            viewModelJob?.cancel()
        }) {
            val response = apiServices.requestSparePart(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
//            dissmissLoading()
            responseaddsparepart.postValue(response)
        }
    }

    fun uploadfile(id: String?, param: MutableList<MultipartBody.Part?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            responseUploadfile.postValue(Resource.Error("Something went wrong"))
            e.printStackTrace()
            viewModelJob?.cancel()
        }) {
            responseUploadfile.postValue(Resource.Loading(true))
            val response = apiServices.uploadfileticket(
                bearerToken = "Bearer ${session?.token_access}",
                id = id,
                param = param

            )
            responseUploadfile.postValue(Resource.Success(response))
        }
    }

    fun reOpenTicket(id: String?, param: MutableList<MultipartBody.Part?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            responseUploadfile.postValue(Resource.Error("Something went wrong"))
            e.printStackTrace()
            viewModelJob?.cancel()
        }) {
            responseUploadfile.postValue(Resource.Loading(true))
            val response = apiServices.uploadfileticket(
                bearerToken = "Bearer ${session?.token_access}",
                id = id,
                param = param

            )
            responseReOpenTicket.postValue(Resource.Success(response))
        }
    }

    fun checkSparepart(query: String, snSparepart: String) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            delay(2000)
            val response = apiServices.checkSparepart(
                bearerToken = "Bearer ${session?.token_access}",
                query = query,
                sn = snSparepart
            )

            dissmissLoading()
            responseCheckSparepart.postValue(response)
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

    fun requestPending(id: String?, param: MutableMap<String, Any?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            dissmissLoading()
            responseRequestPending.postValue(
                BaseResponse(
                    status = 500,
                    message = "Request Pending failed",
                    data = null
                )
            ) // Menangani kegagalan dengan data null
        }) {
            showLoading()
            try {
                val response = apiServices.requestPending(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id,
                    param = param
                )
                responseRequestPending.postValue(
                    BaseResponse(
                        status = response.status,
                        message = response.message,
                        data = response.data
                    )
                ) // Menggunakan data respons API
            } catch (e: Exception) {
                Log.e("PermitViewModel", "Request Pending failed: ${e.message}")
                responseRequestPending.postValue(
                    BaseResponse(
                        status = 500,
                        message = "Request Pending failed",
                        data = null
                    )
                ) // Menggunakan data null untuk error
            } finally {
                dissmissLoading()
            }
        }
    }

}
