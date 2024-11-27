package id.co.dif.base_project.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.SiteData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class PermitViewModel: BaseViewModel() {

    var responseRequestPermit = MutableLiveData<BaseResponse<Any?>>()
    var responseApprovePermit = MutableLiveData<BaseResponse<Any?>>()
    var responseCheckinCheckout = MutableLiveData<BaseResponse<Any?>>()
    var responseSubmitTicket = MutableLiveData<BaseResponse<Any?>>()
    var responseApproveSubmitTicket = MutableLiveData<BaseResponse<Any?>>()
    var responseTakeTicket = MutableLiveData<BaseResponse<Any?>>()

    var file: File? = null

    fun requestPermitById (id : String? = null) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.requestPermit(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            dissmissLoading()
            responseRequestPermit.postValue(response)
        }
    }


    fun approvePermit(id: String?, param: MutableMap<String, Any?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            dissmissLoading()
            responseApprovePermit.postValue(BaseResponse(status = 500, message = "Approval failed", data = null)) // Menangani kegagalan dengan data null
        }) {
            showLoading()
            try {
                val response = apiServices.approvePermit(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id,
                    param = param
                )
                responseApprovePermit.postValue(BaseResponse(status = response.status, message = response.message, data = response.data)) // Menggunakan data respons API
            } catch (e: Exception) {
                Log.e("PermitViewModel", "Approval failed: ${e.message}")
                responseApprovePermit.postValue(BaseResponse(status = 500, message = "Approval failed", data = null)) // Menggunakan data null untuk error
            } finally {
                dissmissLoading()
            }
        }
    }


    fun checkIn(id: String?, in_radius: Boolean) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("checkin1", "Error: ${e.localizedMessage}")
            dissmissLoading()
            responseCheckinCheckout.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null)) // Mengatur respons jika ada kesalahan
        }) {
            try {
                showLoading()
                Log.d("checkin2", "Sending request with parameters: $in_radius")

                // Logging untuk memastikan data yang dikirim
                Log.d("checkin_debug", "id: $id, in_radius: $in_radius")

                val response = apiServices.checkIn(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id,
                    inRadius = in_radius
                )

                // Logging respons setelah dipanggil
                Log.d("checkin_response", "Response: $response")

                responseCheckinCheckout.postValue(BaseResponse(status = response.status, message = response.message, data = response.data)) // Update LiveData dengan respons

                dissmissLoading()
            } catch (e: Exception) {
                Log.e("checkin3", "Exception occurred: ${e.localizedMessage}")
                responseCheckinCheckout.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null)) // Mengatur respons untuk error
            } finally {
                dissmissLoading()
            }
        }
    }

    fun checkInEviden(id: String?, in_radius: Boolean, param: MutableList<MultipartBody.Part?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("checkin1", "Error: ${e.localizedMessage}")
            dissmissLoading()
            responseCheckinCheckout.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null)) // Mengatur respons jika ada kesalahan
        }) {
            try {
                showLoading()
                Log.d("param","response: $param")
                Log.d("checkin_debug", "id: $id, in_radius: $in_radius")

                val response = apiServices.checkInEviden(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id,
                    inRadius = in_radius,
                    param = param
                )

                Log.d("checkin_response", "Response: $response")
                responseCheckinCheckout.postValue(BaseResponse(status = response.status, message = response.message, data = response.data)) // Update LiveData dengan respons

                dissmissLoading()
            } catch (e: Exception) {
                Log.e("checkin3", "Exception occurred: ${e.localizedMessage}")
                responseCheckinCheckout.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null)) // Mengatur respons untuk error
            } finally {
                dissmissLoading()
            }
        }
    }


    fun checkOut(id: String?) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("checkin1", "Error: ${e.localizedMessage}")
            dissmissLoading()
            responseCheckinCheckout.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null)) // Mengatur respons jika ada kesalahan
        }) {
            try {
                showLoading()
                // Logging untuk memastikan data yang dikirim
                Log.d("checkin_debug", "id: $id")

                val response = apiServices.checkOut(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id
                )

                // Logging respons setelah dipanggil
                Log.d("checkin_response", "Response: $response")

                responseCheckinCheckout.postValue(BaseResponse(status = response.status, message = response.message, data = response.data)) // Update LiveData dengan respons

                dissmissLoading()
            } catch (e: Exception) {
                Log.e("checkin3", "Exception occurred: ${e.localizedMessage}")
                responseCheckinCheckout.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null)) // Mengatur respons untuk error
            } finally {
                dissmissLoading()
            }
        }
    }

    fun approveCheckin(id: Int?, param: MutableMap<String, Any?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            dissmissLoading()
            responseCheckinCheckout.postValue(BaseResponse(status = 500, message = "Approval failed", data = null)) // Menangani kegagalan dengan data null
        }) {
            showLoading()
            try {
                val response = apiServices.approveCheckin(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id,
                    param = param
                )
                responseCheckinCheckout.postValue(BaseResponse(status = response.status, message = response.message, data = response.data)) // Menggunakan data respons API
            } catch (e: Exception) {
                Log.e("PermitViewModel", "Approval failed: ${e.message}")
                responseCheckinCheckout.postValue(BaseResponse(status = 500, message = "Approval failed", data = null)) // Menggunakan data null untuk error
            } finally {
                dissmissLoading()
            }
        }
    }

    fun submitTicket(id: String?) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("checkin1", "Error: ${e.localizedMessage}")
            dissmissLoading()
            responseSubmitTicket.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null)) // Mengatur respons jika ada kesalahan
        }) {
            try {
                showLoading()
                // Logging untuk memastikan data yang dikirim
                Log.d("checkin_debug", "id: $id")

                val response = apiServices.submitTicket(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id
                )

                // Logging respons setelah dipanggil
                Log.d("checkin_response", "Response: $response")

                responseSubmitTicket.postValue(BaseResponse(status = response.status, message = response.message, data = response.data)) // Update LiveData dengan respons

                dissmissLoading()
            } catch (e: Exception) {
                Log.e("checkin3", "Exception occurred: ${e.localizedMessage}")
                responseSubmitTicket.postValue(BaseResponse(status = 500, message = "Check-in failed", data = null)) // Mengatur respons untuk error
            } finally {
                dissmissLoading()
            }
        }
    }

    fun approveSubmitTicket(id: Int?, param: MutableMap<String, Any?>) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            dissmissLoading()
            responseApproveSubmitTicket.postValue(BaseResponse(status = 500, message = "Approval failed", data = null)) // Menangani kegagalan dengan data null
        }) {
            showLoading()
            try {
                val response = apiServices.approveSubmitTicket(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id,
                    param = param
                )
                responseApproveSubmitTicket.postValue(BaseResponse(status = response.status, message = response.message, data = response.data)) // Menggunakan data respons API
            } catch (e: Exception) {
                Log.e("PermitViewModel", "Approval failed: ${e.message}")
                responseApproveSubmitTicket.postValue(BaseResponse(status = 500, message = "Approval failed", data = null)) // Menggunakan data null untuk error
            } finally {
                dissmissLoading()
            }
        }
    }

    fun takeTicket(id: String?) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            dissmissLoading()
            responseTakeTicket.postValue(BaseResponse(status = 400, message = e.localizedMessage ?: "Take Ticket failed", data = null))
        }) {
            try {
                showLoading()
                val response = apiServices.takeTicket(
                    bearerToken = "Bearer ${session?.token_access}",
                    id = id
                )
                responseTakeTicket.postValue(BaseResponse(status = response.status, message = response.message, data = response.data))
                dissmissLoading()
            } catch (e: Exception) {
                Log.e("checkin3", "Exception occurred: ${e.localizedMessage}")
                responseTakeTicket.postValue(BaseResponse(status = 400, message = "You have a ticket that has been taken, please complete it first.", data = null)) // Mengatur respons untuk error
            } finally {
                dissmissLoading()
            }
        }
    }


}