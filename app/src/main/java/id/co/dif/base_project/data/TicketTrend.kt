package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class TicketTrend(
    @SerializedName("on_going") val ongoing: List<Int>,
    @SerializedName("closed") val closed: List<Int>,
    @SerializedName("new_tickets") val new: List<Int>,
    @SerializedName("labels") val labels: List<String>

) : BaseData()
