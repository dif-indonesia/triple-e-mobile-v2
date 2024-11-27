package id.co.dif.base_project.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.base_project.base.BaseResponse
import id.co.dif.base_project.base.BaseResponseList
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.base.DataList
import id.co.dif.base_project.data.PlainValueLabel
import id.co.dif.base_project.data.TopUser
import id.co.dif.base_project.data.UsersInfoDashboard
import id.co.dif.base_project.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.roundToInt

class TeamDashboardViewModel : BaseViewModel() {

    var hourlyUsageTrend = MutableLiveData<Resource<BaseResponseList<Int>>>()
    var usersInfoDashboard = MutableLiveData<BaseResponse<UsersInfoDashboard>>()
    var usageTrend = MutableLiveData<Resource<BaseResponseList<PlainValueLabel>>>()
    var topUsers = MutableLiveData<Resource<BaseResponseList<TopUser>>>()
    var areasUsageData = MutableLiveData<Resource<BaseResponseList<PlainValueLabel>>>()


    fun getAreasUsageData() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            areasUsageData.postValue(Resource.Error(e.message.toString()))
        }) {
            areasUsageData.postValue(Resource.Loading(true))
            val labels = listOf(
                "Palembang",
                "Sumatera Barat",
                "Padang Panjang",
                "Jambi",
                "Medan"
            )
            val values = mutableListOf<Int>()
            val random = Random()
            repeat(labels.size) {
                (random.nextFloat() * 100).roundToInt()
            }

            val data = values.mapIndexed { index, i ->
                PlainValueLabel(
                    count = i,
                    label = labels[index]
                )
            }

            val response = BaseResponseList(
                data = DataList(list = data, limit = data.size, page = 1, total = data.size),
                message = "",
                status = 200
            )
            areasUsageData.postValue(Resource.Success(response))
        }
    }

    fun getHourlyTeamUsageTrend() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            hourlyUsageTrend.postValue(Resource.Error(e.message.toString()))
        }) {
            hourlyUsageTrend.postValue(Resource.Loading(true))
            val response = apiServices.getHourlyTeamUsageTrend(
                bearerToken = "Bearer ${session?.token_access}",
            )
            hourlyUsageTrend.postValue(Resource.Success(response))
        }
    }

    fun getUsersInfoDashboard() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
        }) {
            val response = apiServices.getUsersInfoDashboard(
                bearerToken = "Bearer ${session?.token_access}",
            )
            usersInfoDashboard.postValue(response)
        }
    }

    fun getTopUsers() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
        }) {

            val data = mutableListOf<TopUser>()
            for (i in 1..5) {
                val hours = (Math.random() * 100).roundToInt()
                val minutes = (Math.random() * 60).roundToInt()
                val id = (Math.random() * Long.MAX_VALUE).toLong()
                data.add(
                    TopUser(
                        userPictureUrl = null,
                        username = "Mirza My Humayung #$id",
                        position = "Staff TO",
                        timeSpent = "${hours}h ${minutes}m"
                    )
                )
            }

            val response = BaseResponseList<TopUser>(
                200,
                "Success",
                DataList(data, limit = 0, page = 0, total = 0)
            )

            topUsers.postValue(Resource.Success(response))
        }
    }


    fun getUsageTrend(type: String) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
        }) {
            usageTrend.postValue(Resource.Loading(true))
            val response = apiServices.getUsageTrend(
                bearerToken = "Bearer ${session?.token_access}",
                type = type
            )
            usageTrend.postValue(Resource.Success(response))
        }
    }


    fun cancelJob() {
        viewModelJob?.cancel()
    }
}