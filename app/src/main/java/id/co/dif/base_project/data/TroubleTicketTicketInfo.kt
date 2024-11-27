package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class TroubleTicketTicketInfo(
    @SerializedName("card1") val card: TroubleTicketTicketInfoCard,
    @SerializedName("closed") val closed: String,
    @SerializedName("new") val new: String,
    @SerializedName("onGoing", alternate = ["on-going"]) val onGoing: String,
    @SerializedName("rate") val rate: String
) : BaseData()

data class TroubleTicketTicketInfoCard(
    @SerializedName("portion") val portion: String,
    @SerializedName("team") val team: String,
    @SerializedName("my_ticket") val myTicket: String,
    @SerializedName("total_ticket") val totalTicket : String,
    @SerializedName("word") val word: String
)