package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class TicketInfo(
    @SerializedName("closed") val closed: Int,
    @SerializedName("on_progress") val onProgress: Int,
    @SerializedName("open") val open: Int,
    @SerializedName("total_ticket") val totalTicket: Int
)
