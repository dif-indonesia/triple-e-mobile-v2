package id.co.dif.base_project.presentation.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.AccountSettings
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.Work
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ActivitySettingViewModel : BaseViewModel() {
    var responseAccountSetting = MutableLiveData<BaseResponse<Any?>>()
    var responseBasicInfoList = MutableLiveData<BaseResponse<BasicInfo>>()

    fun settingActivity(param: AccountSettings) {
        showLoading()
        viewModelJob?.cancel()
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            val response = apiServices.updateAccountSettings(
                bearerToken = "Bearer ${session?.token_access}",
                param = param
            )
            responseAccountSetting.postValue(response)
        }
    }

    fun getDetailedProfile(id: Int?) {
        viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getDetailProfile(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseBasicInfoList.postValue(response)
        }
    }
}
