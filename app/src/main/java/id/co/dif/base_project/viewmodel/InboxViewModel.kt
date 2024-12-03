package id.co.dif.base_project.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Chat
import id.co.dif.base_project.data.Employee
import id.co.dif.base_project.data.UnreadNumber
import id.co.dif.base_project.databinding.LayoutNotificationTabBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class InboxViewModel : BaseViewModel() {

    var currentPage = 1
    val limit = 10 // Jumlah item per halaman, sesuai respons API
    val isLoading = MutableLiveData(false) // Status loading
    var hasMoreData = true // Menentukan apakah masih ada data untuk dimuat

    var responseUnreadNumber = MutableLiveData<BaseResponse<UnreadNumber>>()
    var responseSendMessages = MutableLiveData<BaseResponse<Chat>>()
    val tabBindings: MutableList<LayoutNotificationTabBinding> = mutableListOf()
    var responseListName = MutableLiveData<BaseResponseList<Employee>>()

    fun getListName(context: Context?, search: String? = null, append: Boolean = false) {
        if (isLoading.value == true || !hasMoreData) return // Jangan memuat jika sedang loading atau tidak ada data lagi

        isLoading.value = true
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            handleApiError(throwable)
            isLoading.value = false
        }) {
            val response = apiServices.listName(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
                page = currentPage,
                limit = limit
            )
            isLoading.value = false

            if (response.data.list.isNullOrEmpty()) {
                hasMoreData = false // Tidak ada data lagi untuk dimuat
            } else {
                hasMoreData = response.data.list.size == limit // Jika data kurang dari limit, tidak ada data lagi
                if (append) {
                    // Salin data lama, tambahkan data baru, dan perbarui `responseListName`
                    val currentList = responseListName.value?.data?.list?.toMutableList() ?: mutableListOf()
                    currentList.addAll(response.data.list) // Tambahkan data baru jika ada
                    val updatedData = responseListName.value?.data?.copy(list = currentList) ?: response.data
                    val updatedResponse = responseListName.value?.copy(data = updatedData) ?: response
                    responseListName.postValue(updatedResponse)
                } else {
                    // Jika tidak menambahkan, langsung set data baru
                    responseListName.postValue(response)
                }
            }
        }
    }



//    fun getListName(context: Context?, search: String? = null) {
//        viewModelJob?.cancel()
//        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
//            handleApiError(throwable)
//        }) {
//            val response = apiServices.listName(
//                bearerToken = "Bearer ${session?.token_access}",
//                search = search,
//                page = null,
//                limit = null
//            )
//            println(response)
//            responseListName.postValue(response)
//        }
//    }


    fun sendMessages(param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
        }) {
            val response = apiServices.sendChat(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            dissmissLoading()
            responseSendMessages.postValue(response)
        }
    }

    fun getUnreadNumber() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            handleApiError(throwable)
        }) {
            val response = apiServices.getUnreadNumber(
                bearerToken = "Bearer ${session?.token_access}",
            )
            responseUnreadNumber.postValue(response)
        }
    }

    fun cancelJob() {
        viewModelJob?.cancel()
    }


}