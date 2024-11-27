package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class TicketNumbers(
    @SerializedName("list") val list: List<TicketNumbersItem>,
) : BaseData()

data class TicketNumbersItem(
    @SerializedName("id_ticket") val idTicket: Int,
    @SerializedName("num_notes") val numNotes: Int,
    @SerializedName("num_upload") val numUpload: Int,
)


