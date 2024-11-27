package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Education
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class EducationViewModel : BaseViewModel() {

    var responseEducationList = MutableLiveData<BaseResponseList<Education>>()
    var responseDelete = MutableLiveData<BaseResponse<Education>>()


    fun getEducationList(id: Int?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.getEducationList(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseEducationList.postValue(response)
            dissmissLoading()
        }
    }


    fun deleteEducation(it: Education) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.deleteEducation(
                "Bearer ${session?.token_access}",
                it.id
            )
            responseDelete.postValue(response)
        }
    }


}