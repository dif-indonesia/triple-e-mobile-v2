package id.co.dif.base_project.presentation.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProjectViewModel : BaseViewModel() {


    var responseListProject = MutableLiveData<BaseResponseList<Any>>()
    var responseUpdateProject = MutableLiveData<BaseResponse<Any>>()
    var selectedProject : String? = null
    fun getListProject() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getListProject(
                "Bearer ${session?.token_access}",
            )
            responseListProject.postValue(response)
            dissmissLoading()
        }
    }

    fun updateMyProject(project: String?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.updateMyProject(
                "Bearer ${session?.token_access}",
                project = project
            )
            responseUpdateProject.postValue(response)
            dissmissLoading()
        }
    }

}
