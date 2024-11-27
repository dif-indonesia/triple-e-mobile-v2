package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class TicketQuality(
    @SerializedName("program") val program: Int,
    @SerializedName("progress") val progress: Int,
) : BaseData()
