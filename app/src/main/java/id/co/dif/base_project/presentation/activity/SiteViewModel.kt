package id.co.dif.base_project.presentation.activity

import androidx.lifecycle.MutableLiveData
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.TicketDetails

class SiteViewModel: BaseViewModel() {


    var responseDetilTicket = MutableLiveData<BaseResponse<TicketDetails>>()

}

