package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.AreaBenchmark
import id.co.dif.base_project.data.Employee
import id.co.dif.base_project.data.PlainValueLabel
import id.co.dif.base_project.data.ProgressBarConclusion
import id.co.dif.base_project.data.TicketNumbers
import id.co.dif.base_project.data.TicketTrend
import id.co.dif.base_project.data.TroubleTicketTicketInfo
import id.co.dif.base_project.data.TtTimeToClosed
import id.co.dif.base_project.data.UserOverallRank
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.str
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TTDashboardViewModel : BaseViewModel() {
    var ttTimeToClosedData = MutableLiveData<Resource<BaseResponse<TtTimeToClosed>>>()
    var dailyTicketTrend = MutableLiveData<Resource<BaseResponseList<PlainValueLabel>>>()
    var ticketTrends = MutableLiveData<Resource<BaseResponse<TicketTrend>>>()
    var ttTicketInfo = MutableLiveData<Resource<BaseResponse<TroubleTicketTicketInfo>>>()
    var ticketNumbers = MutableLiveData<Resource<BaseResponse<TicketNumbers>>>()
    val areaBenchmarkData = MutableLiveData<Resource<BaseResponse<AreaBenchmark>>>()
    val overallRank = MutableLiveData<Resource<BaseResponseList<UserOverallRank>>>()
    val progressBarConclusion = MutableLiveData<Resource<BaseResponse<ProgressBarConclusion>>>()
    var responseListName = MutableLiveData<BaseResponseList<Employee>>()

    private suspend fun getOverallRank(page: Int = 1, limit: Int = 10, ticArea: String? = null) {
        apiCallList(overallRank) {
            apiServices.getUserOverallRank(
                "Bearer ${session?.token_access}",
                page = page,
                limit = limit,
                ticArea = ticArea
            )
        }
    }

    fun getListName(search: String? = null, limit: Int? = null, page: Int? = null) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            handleApiError(throwable)
        }) {
            val response = apiServices.listName(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
                limit = limit,
                page = page
            )
            println(response)
            responseListName.postValue(response)
        }
    }

    suspend fun <D> apiCall(resource: MutableLiveData<Resource<BaseResponse<D>>>, block: suspend () -> BaseResponse<D>) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            t.printStackTrace()
            resource.postValue(Resource.Error(message = t.localizedMessage.str))
        }) {
            resource.postValue(Resource.Loading())
            val response = block()
            resource.postValue(Resource.Success(response))
        }
    }

    suspend fun <D> apiCallList(resource: MutableLiveData<Resource<BaseResponseList<D>>>, block: suspend () -> BaseResponseList<D>) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            t.printStackTrace()
            resource.postValue(Resource.Error(message = t.localizedMessage.str))
        }) {
            resource.postValue(Resource.Loading())
            val response = block()
            resource.postValue(Resource.Success(response))
        }
    }

    suspend fun getAreaBenchmarkData(id: Int?) {
        apiCall<AreaBenchmark>(areaBenchmarkData) {
            apiServices.getAreaBenchmarkData(
                "Bearer ${session?.token_access}",
                id
            )
        }
    }

    suspend fun getNumberOfTickets(id: Int?) {
        apiCall(ticketNumbers) {
            apiServices.getNumberOfTickets(
                "Bearer ${session?.token_access}",
                id = id
            )
        }
    }

    suspend fun getProgressBarConclusion(id: Int? = null) {
        apiCall(progressBarConclusion) {
            apiServices.getProgressBarConclusion(
                "Bearer ${session?.token_access}",
                id = id
            )
        }
    }

    suspend fun getTtTimeToClosedData(id: Int? = null, timeFrame: String = "yearly") {
        apiCall(ttTimeToClosedData) {
            apiServices.getTtTimeToClosed(
                "Bearer ${session?.token_access}",
                id = id,
                filter = timeFrame
            )
        }
    }

    suspend fun getDailyTicketTrend(id: Int? = null, days: Int?) {
        apiCallList(dailyTicketTrend) {
            apiServices.getDailyTicketTrend(
                "Bearer ${session?.token_access}",
                id = id,
                days = days
            )
        }
    }

    suspend fun getTtTicketInfo(id: Int?) {
        apiCall(ttTicketInfo) {
            apiServices.getTtTicketInfo(
                "Bearer ${session?.token_access}",
                id = id
            )
        }
    }

    suspend fun getTicketTrends(id: Int?) {
        apiCall(ticketTrends) {
            apiServices.getTicketTrends(
                "Bearer ${session?.token_access}"
            )
        }
    }

    fun fetchChartsData(userId: Int?) {
        cancelAllJobs()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            getTtTicketInfo(userId)
            getAreaBenchmarkData(userId)
            getTtTimeToClosedData(userId)
            getAreaBenchmarkData(userId)
            getProgressBarConclusion(userId)
            getNumberOfTickets(userId)
            getTicketTrends(userId)
            getDailyTicketTrend(userId, 30)
//            getOverallRank()
        }

    }

}