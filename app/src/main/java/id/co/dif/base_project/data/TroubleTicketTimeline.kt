package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class TroubleTicketTimeline(
    @SerializedName("date"         ) var date       : String?    = null,
    @SerializedName("name"         ) var name       : String?    = null,
    @SerializedName("time"         ) var time       : String?    = null,
    @SerializedName("status"         ) var status       : String?    = null

) : BaseData()
