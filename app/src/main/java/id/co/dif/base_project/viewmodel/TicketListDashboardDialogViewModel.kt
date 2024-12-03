package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.base.DataList
import id.co.dif.base_project.data.TroubleTicket
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.isDeviceOnline
import id.co.dif.base_project.utils.str
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class TicketListDashboardDialogViewModel : BaseViewModel() {

    var filterIsOn: Boolean = false
    var responseAllTroubleTickets = MutableLiveData<Resource<BaseResponseList<TroubleTicket>>>()

    var sortBy: String = "tic_updated,desc"

    var fromDate: String = ""
    var untilDate: String = ""
    var search: String = ""
    var severety: String = ""

    fun getListTroubleTicket(status: String, page: Int, limit: Int = 10)
    {
        if (!context.isDeviceOnline()) {
            if (page == 1) {
                loadAllTroubleTicket()
            }
            return
        }
        viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            responseAllTroubleTickets.postValue(Resource.Error(t.localizedMessage.str))
            viewModelJob?.cancel()
        }) {
            responseAllTroubleTickets.postValue(Resource.Loading(true))
            val response = apiServices.getListTroubleTicket(
                bearerToken = "Bearer ${session?.token_access}",
                page = page,
                limit = limit,

                param = mutableMapOf(
                    "severety" to severety,
                    "search" to search,
                    "sortBy" to sortBy,
                    "start_date" to fromDate,
                    "end_date" to untilDate,
                    "status" to if (status.lowercase() == "status") "" else status
                )
            )
            responseAllTroubleTickets.postValue(Resource.Success(response))
        }
    }

    private fun loadAllTroubleTicket() {
        val allTroubleTickets = preferences.allTroubleTickets.value ?: mutableListOf()
        val response = BaseResponseList(
            data = DataList(
                list = allTroubleTickets,
                limit = allTroubleTickets.size,
                page = 1,
                total = allTroubleTickets.size
            ),
            message = "",
            status = 200
        )
        responseAllTroubleTickets.postValue(Resource.Success(response))
    }

}
