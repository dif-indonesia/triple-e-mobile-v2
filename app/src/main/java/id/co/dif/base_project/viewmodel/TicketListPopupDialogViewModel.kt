package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.TroubleTicket
import id.co.dif.base_project.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class TicketListPopupDialogViewModel : BaseViewModel() {

    var responseaGetSiteByid = MutableLiveData<Resource<BaseResponseList<TroubleTicket>>>()

//    fun getSiteById(id: Int? = null) {
//        showLoading()
//        viewModelJob?.cancel()
//        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
//            dissmissLoading()
//            viewModelJob?.cancel()
//        }) {
//            val response = apiServices.getSiteById(
//                id = id,
//                bearerToken = "Bearer ${session?.token_access}",
//            )
//            dissmissLoading()
//            responseaGetSiteByid.postValue(response)
//        }
//    }

    fun getTicketsBySiteById(siteId: Int, page: Int, limit: Int = 10) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            t.printStackTrace()
            viewModelJob?.cancel()
        }) {
            responseaGetSiteByid.postValue(Resource.Loading(true))
            val response = apiServices.getTicketBySiteId(
                id = siteId,
                bearerToken = "Bearer ${session?.token_access}",
                page = page,
                limit = limit,
            )
            responseaGetSiteByid.postValue(Resource.Success(response))
        }

    }

    fun cancelJob() {
        viewModelJob?.cancel()
    }


}
