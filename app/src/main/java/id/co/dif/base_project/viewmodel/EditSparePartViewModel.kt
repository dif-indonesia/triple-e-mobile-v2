package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.base.DataList
import id.co.dif.base_project.data.EngineerWithinRadiusStatus
import id.co.dif.base_project.data.SparePart
import id.co.dif.base_project.data.SparePartByCode
import id.co.dif.base_project.data.SparePartByName
import id.co.dif.base_project.data.Work
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class EditSparePartViewModel : BaseViewModel() {

    var responseEditSparePart = MutableLiveData<BaseResponse<Any?>>()

    fun editSparePart(tic_id: String, spreq_id: String, param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            val response = BaseResponse<Any?>(
                data = Any(),
                message = "",
                status = 400
            )
            responseEditSparePart.postValue(response)
            dissmissLoading()
        }) {
            val response = apiServices.editSparePartID(
                bearerToken = "Bearer ${session?.token_access}",
                ticketId = tic_id,
                spreq_id = spreq_id,
                param = param
            )
            dissmissLoading()
            responseEditSparePart.postValue(response)
        }
    }

    var selecteSparepartName: String? = null
    var selecteSparepartCode: String? = null
    var responseGetEngineerIsWithinRadius =
        MutableLiveData<BaseResponse<EngineerWithinRadiusStatus>>()
    var responseGetSparePartByArticleName = MutableLiveData<BaseResponseList<SparePartByName>>()
    var responseGetSparePartByArticleCode = MutableLiveData<BaseResponseList<SparePartByCode>>()
    var responseaddSparePart = MutableLiveData<BaseResponse<Any?>>()

    fun addSparePart(tic_id: String, param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.addSparePartID(
                bearerToken = "Bearer ${session?.token_access}",
                ticketId = tic_id,
                param = param
            )
            responseaddSparePart.postValue(response)
            dissmissLoading()
        }
    }

    fun getSparePartByArticleName(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            val response = apiServices.searchSparePartByName(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            responseGetSparePartByArticleName.postValue(response)
        }
    }

    fun getSparePartByArticleCode(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            val response = apiServices.searchSparePartByCode(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            responseGetSparePartByArticleCode.postValue(response)

        }
    }

}
