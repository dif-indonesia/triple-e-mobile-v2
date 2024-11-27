package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.ActiveUser
import id.co.dif.base_project.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class UserActivityViewModel : BaseViewModel() {
    var usersListActivity = MutableLiveData<Resource<BaseResponseList<ActiveUser>>>()
    fun getListOfActiveUsers(page: Int = 1, limit: Int = 30) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
        }) {
            usersListActivity.postValue(Resource.Loading(true))
            val response = apiServices.getActiveUsers(
                limit = limit, page = page,
                bearerToken = "Bearer ${session?.token_access}",
            )
            usersListActivity.postValue(Resource.Success(response))
        }


    }

    fun cancelJob() {
        viewModelJob?.cancel()
    }
}