package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.base.DataList
import id.co.dif.base_project.data.BaseData
import id.co.dif.base_project.data.SparePart
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SparePartViewModel : BaseViewModel() {

    var responseGetListSparePart = MutableLiveData<BaseResponseList<SparePart>>()
    var errorWhileLoadingData = false
    var responseDeleteSparePart = MutableLiveData<BaseResponse<Any?>>()


    fun deleteSparePart (tic_id : String, spreq_id: String ) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            val response = apiServices.deleteSparePartID(
                bearerToken = "Bearer ${session?.token_access}",
                ticketId = tic_id,
                spreq_id = spreq_id
            )
            dissmissLoading()
            responseDeleteSparePart.postValue(response)
        }
    }
    fun getListSparePart(id: String? = null) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            val response = BaseResponseList<SparePart>(
                data = DataList(
                    limit = 0,
                    list = listOf(),
                    page = 1,
                    total = 0
                ),
                message = "",
                status = 400,
            )
            e.printStackTrace()
            responseGetListSparePart.postValue(response)
            handleApiError(e)
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getListSparePart(
                bearerToken = "Bearer ${session?.token_access}",
                ticketId = id,
            )
            responseGetListSparePart.postValue(response)
            dissmissLoading()
        }
    }
}