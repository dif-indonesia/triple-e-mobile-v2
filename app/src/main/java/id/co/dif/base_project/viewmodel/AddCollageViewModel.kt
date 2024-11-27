package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AddCollageViewModel : BaseViewModel() {

    var responseaddcollage = MutableLiveData<BaseResponse<MutableMap<String, Any?>>>()

    fun addeducation (param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.addeducation(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            dissmissLoading()
            responseaddcollage.postValue(response)
//            println(response.status)
//            println(response?.status == 200)
//            if (response?.status == 200) {
//                var experienceList = apiServices.putworklist(bearerToken = "Bearer ${session?.token_access}")
//                responseCollageList.postValue(experienceList)
//            }
        }
    }

}