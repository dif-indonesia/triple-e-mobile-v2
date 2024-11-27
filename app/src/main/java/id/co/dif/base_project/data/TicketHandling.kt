package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class TicketHandling(
    @SerializedName("ticket_handling") val ticketHandling: Int
) : BaseData()
