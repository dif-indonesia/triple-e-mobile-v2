package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Work
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class EditWorkPlaceViewModel : BaseViewModel() {

    var responseworkedit = MutableLiveData<BaseResponse<Work>>()

    fun putworkedit (id : String, param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            val response = apiServices.putworkedit(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
                param = param
            )
            responseworkedit.postValue(response)
        }
    }

}