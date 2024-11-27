package id.co.dif.base_project.base

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.Session
import id.co.dif.base_project.data.TicketDetails
import id.co.dif.base_project.presentation.activity.OnBoardingActivity
import id.co.dif.base_project.service.ApiServices
import id.co.dif.base_project.state.BaseUiState
import id.co.dif.base_project.persistence.Preferences
import id.co.dif.base_project.utils.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.joda.time.DateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.HttpException

/***
 * Created by kikiprayudi
 * on Monday, 27/02/23 04:06
 *
 */
open class BaseViewModel : ViewModel(), KoinComponent {
    val apiServices by inject<ApiServices>()
    val context by inject<Context>()
    val gson by inject<Gson>()
    val preferences: Preferences by inject()
    val uiState = MutableLiveData<BaseUiState>()
    var responseTicketDetails = MutableLiveData<BaseResponse<TicketDetails>>()
    var offlineUploadJob: Job? = null
    var session: Session? = null
        get() = preferences.session.value
    var basiInfo: BasicInfo? = null
        get() = preferences.myDetailProfile.value
    var accessToken: String? = session?.token_access
        get() = "Bearer ${session?.token_access}"
    protected var viewModelJob: Job? = null
        set(value) {
            field = value
            activeJobs.add(field)
        }
    val activeJobs = mutableListOf<Job?>()

    fun cancelAllJobs() {
        Log.w(this::class.java.name, "Canceling All Jobs")
        activeJobs.forEach { it?.cancel() }
        activeJobs.clear()
    }

    init {
        uiState.value = BaseUiState()
        if (preferences.session.value == null) {
            preferences.session.value =
                Session(token_access = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTY4NDkwMzc5MSwianRpIjoiYmI1YjE4YjYtMTkwYS00NDk4LTgwZGItNGNiNjJlNDYzNmVkIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6eyJpZCI6IjI4IiwibmFtZSI6InJpbmEgbm9pc2UiLCJlbWFpbCI6InJpbmFAZ21haWwuY29tIiwiYXNnbl9wcm9qZWN0X2lkIjoiMSJ9LCJuYmYiOjE2ODQ5MDM3OTEsImV4cCI6MTY4NDk5MDE5MX0.IBOkdabn3qVLABQmkekeajqfHRpkcIG103DwOZJr0II")
        }

    }

    override fun onCleared() {
        super.onCleared()
        cancelAllJobs()
    }

    fun showLoading() {
        uiState.value?.apply {
            isShowLoading = true

            uiState.postValue(this)
        }
    }

    fun dissmissLoading() {
        uiState.value?.apply {
            isShowLoading = false
            uiState.postValue(this)
        }
    }

    fun handleApiError(throwable: Throwable, message: String? = null) {
        if (throwable is HttpException) {
            try {
                val type = object : TypeToken<BaseRes>() {}.type
                val errorResponse = gson.fromJson<BaseRes>(
                    throwable.response()?.errorBody()?.charStream(),
                    type
                )
                if (errorResponse.status == 401) {
                    preferences.wipe()
                    context.startActivity(
                        Intent(context, OnBoardingActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        "${message ?: errorResponse.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "An error occurred. Cannot connect to server.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun uploadFileOffline(listener:suspend ()->Unit) {
        val requestUploadOffline = preferences.requestUpload.value ?: listOf()
        val requestData = requestUploadOffline.firstOrNull()
        requestData?.let { request ->
            val requestParam = mutableListOf<MultipartBody.Part?>()
            request.second.forEach {
                requestParam.add(makeMultipartData(it.key, it.value))
            }
            val response = apiServices.uploadfileticket(
                bearerToken = "Bearer ${session?.token_access}",
                id = request.first,
                param = requestParam
            )
            preferences.requestUpload.value = requestUploadOffline.toMutableList().apply { remove(firstOrNull()) }
            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
        }
        listener()
    }

    fun getTicketDetails(id: String? = null) {
        if (!context.isDeviceOnline()) {
            loadTicketDetailsOffline(id)
            return
        }
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            val response = BaseResponse<TicketDetails>(
                status = 400,
                message = "Something went wrong!",
                data = TicketDetails()
            )
            dissmissLoading()
            responseTicketDetails.postValue(response)
            viewModelJob?.cancel()
            e.stackTraceToString().log("memek")
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.getTicketDetails(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            dissmissLoading()
            responseTicketDetails.postValue(response)
        }
    }

    fun startPeriodicOfflineNotesUpload() {
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            startPeriodicOfflineNotesUpload()
        }) {
            if (context.isDeviceOnline()) {
                uploadFileOffline {
                    delay(3000)
                    startPeriodicOfflineNotesUpload()
                }
            } else {
                delay(3000)
                startPeriodicOfflineNotesUpload()
            }
        }
    }

    private fun loadTicketDetailsOffline(ticketId: String?) {
        val allTicketDetailsOffline = preferences.allTicketsDetails.value
        var ticketDetails = TicketDetails()
        var status = 200
        var message = "Ticket details is loaded from local"
        if (allTicketDetailsOffline != null) {
            val ticketDetailsFromLocal = allTicketDetailsOffline[ticketId]
            if (ticketDetailsFromLocal != null) {
                ticketDetails = ticketDetailsFromLocal.log("site info") { it.site_info }
            } else {
                message = "No ticket details ($ticketId) found saved offline!"
                status = 400
            }
        } else {
            message = "No ticket details ($ticketId) found saved offline!"
            status = 400
        }
        val response = BaseResponse(
            status,
            message,
            ticketDetails
        )
        responseTicketDetails.postValue(
            response
        )

    }
}