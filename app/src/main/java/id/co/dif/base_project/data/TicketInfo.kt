package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class TicketInfo(
    @SerializedName("closed_ticket_area") val closedTicketArea: Int,
    @SerializedName("my_closed_ticket") val myClosedTicket: Int,
    @SerializedName("my_open_ticket") val myOpenTicket: Int,
    @SerializedName("open_ticket_area") val openTicketArea: Int
)
