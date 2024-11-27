package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class SentMessage(
    @SerializedName("date") val date: String,
    @SerializedName("mes_receiver") val receiverName: String,
    @SerializedName("mes_sender") val senderId: String,
    @SerializedName("mes_id") val messageId: String
): BaseData()
