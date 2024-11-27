package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.SiteData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MapSiteViewModel : BaseViewModel()  {

    var responseaGetSiteByid = MutableLiveData<BaseResponse<SiteData>>()

    fun getSiteById (id : Int? = null) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getSiteById(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            dissmissLoading()
            responseaGetSiteByid.postValue(response)
        }
    }

}
