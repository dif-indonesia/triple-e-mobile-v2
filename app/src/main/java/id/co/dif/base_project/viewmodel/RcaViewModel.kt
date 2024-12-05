package id.co.dif.base_project.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseRca
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Rca
import id.co.dif.base_project.data.RcaResponse
import id.co.dif.base_project.data.RcaResponseImage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class RcaViewModel : BaseViewModel() {

    var selectedOwner: String? = null
    var selectedCategory: String? = null
    var selectedRc1: String? = null

    var responseMasterOwner = MutableLiveData<BaseRca>()
    var responseMasterCategory = MutableLiveData<BaseRca>()
    var responseMasterRc1 = MutableLiveData<BaseRca>()
    var responseMasterRc2 = MutableLiveData<BaseRca>()
    var responseMasterResolutionAction = MutableLiveData<BaseRca>()
    var responseSubmitRca = MutableLiveData<BaseResponse<Any?>>()
    var responseGetRca = MutableLiveData<BaseResponse<RcaResponse>>()
    var responseGetRcaImage = MutableLiveData<BaseResponse<RcaResponseImage>>()
    var responseSubmitRcaPhoto = MutableLiveData<BaseResponse<Any?>>()

    var filter: String = ""
    var owner: String = ""
    var category: String = ""
    var rc1: String = ""

    var file: File? = null

    fun submitRca(id: String?, param: MutableMap<String, Any?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            dissmissLoading()
            responseSubmitRca.postValue(BaseResponse(status = 500, message = "Submit RCA failed", data = null)) // Menangani kegagalan dengan data null
        }) {
            showLoading()
            try {
                val response = apiServices.submitRca(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id,
                    param = param
                )
                responseSubmitRca.postValue(BaseResponse(status = response.status, message = response.message, data = response.data)) // Menggunakan data respons API
            } catch (e: Exception) {
                Log.e("RcaViewMode", "Submit RCA failed: ${e.message}")
                responseSubmitRca.postValue(BaseResponse(status = 500, message = "Submit RCA failed", data = null)) // Menggunakan data null untuk error
            } finally {
                dissmissLoading()
            }
        }
    }

    fun getRca(id: String?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            e.printStackTrace()
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getRca(
                bearerToken = "Bearer ${session?.token_access}",
                id = id
            )
            responseGetRca.postValue(response)
            dissmissLoading()
        }
    }

    fun getRcaImage(id: String?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            e.printStackTrace()
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getRcaImage(
                bearerToken = "Bearer ${session?.token_access}",
                id = id
            )
            responseGetRcaImage.postValue(response)
            dissmissLoading()
        }
    }

    fun submitRcaPhoto(id: String?, param: MutableList<MultipartBody.Part?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("checkin1", "Error: ${e.localizedMessage}")
            dissmissLoading()
            responseSubmitRcaPhoto.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null)) // Mengatur respons jika ada kesalahan
        }) {
            try {
                showLoading()
                Log.d("param","response: $param")
                Log.d("checkin_debug", "id: $id")

                val response = apiServices.submitRcaPhoto(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id,
                    param = param
                )
                Log.d("checkin_response", "Response: $response")
                responseSubmitRcaPhoto.postValue(BaseResponse(status = response.status, message = response.message, data = response.data))
                dissmissLoading()
            } catch (e: Exception) {
                Log.e("checkin3", "Exception occurred: ${e.localizedMessage}")
                responseSubmitRcaPhoto.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null))
            } finally {
                dissmissLoading()
            }
        }
    }


    fun getMasterOwner() {
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
        }) {
            try {
                val response = apiServices.getMasterOwner(
                    bearerToken = "Bearer ${session?.token_access}"
                )
                if (response.status == 200) {
                    responseMasterOwner.postValue(response)
                } else {
                    Log.e("RcaViewModel", "Error: ${response.message}")
                }
            } catch (e: Exception) {
                dissmissLoading()
                Log.e("RcaViewModel", "Error fetching data", e)
            }
        }
    }


    fun getMasterCategory(owner: String){
        print("owner$owner")
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
        }) {
            val response = apiServices.getMasterCategory(
                bearerToken = "Bearer ${session?.token_access}",
                owner = owner
            )
            responseMasterOwner.postValue(response)
        }
    }

    fun getMasterRc1 (owner: String, category: String){
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
        }) {
            val response = apiServices.getMasterRc1(
                bearerToken = "Bearer ${session?.token_access}",
                owner = owner,
                category = category

            )
            responseMasterRc1.postValue(response)
        }
    }

    fun getMasterRc2 (owner: String, category: String, rc1: String){
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
        }) {
            val response = apiServices.getMasterRc2(
                bearerToken = "Bearer ${session?.token_access}",
                owner = owner,
                category = category,
                rc1 = rc1

            )
            responseMasterRc2.postValue(response)
        }
    }


}