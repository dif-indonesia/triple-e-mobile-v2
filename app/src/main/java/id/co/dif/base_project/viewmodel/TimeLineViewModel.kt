package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.TicketDetails
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class TimeLineViewModel : BaseViewModel() {


    var responseListTroubleTicket = MutableLiveData<BaseResponse<TicketDetails>>()
    var responseDelete = MutableLiveData<BaseResponse<Any>>()
    var responseDetilTicket = MutableLiveData<BaseResponse<TicketDetails>>()

    fun getListTimeLine(id: String? = null) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getTicketDetails(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            responseListTroubleTicket.postValue(response)
        }
    }

    fun deleteNote(param: Int?, ticketId: String?) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            e.printStackTrace()
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.deleteNote(
                "Bearer ${session?.token_access}",
                id = ticketId,
                note_id = param
            )
            responseDelete.postValue(response)
            dissmissLoading()
        }
    }

    fun getDetilTicket (id : String? = null) {

        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.getTicketDetails(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            dissmissLoading()
            responseDetilTicket.postValue(response)
        }
    }

}
