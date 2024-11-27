package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class MessageNotification(
    @SerializedName("id", alternate = ["chat_id"]) val id: Int,
    @SerializedName("mes_class") var mesClass: String? = null,
    @SerializedName("mes_content", alternate = ["chat_content"]) val mesContent: String,
    @SerializedName("mes_date", alternate = ["chat_time"]) val mesDate: String,
    @SerializedName("mes_receiver", alternate = ["chat_receiver"]) val mesReceiver: String,
    @SerializedName("mes_sender", alternate = ["chat_sender"]) val mesSender: String,
    @SerializedName("mes_receiver_id") var mesReceiverId: String? = null,
    @SerializedName("mes_sender_id") var mesSenderId: String? = null,
    @SerializedName("mes_reff_id") var mesReffId: Int? = null,
    @SerializedName("mes_site_name") var mesSiteName: String? = null,
    @SerializedName("mes_show", alternate = ["chat_show"]) var mesShow: Boolean,
    ) : BaseData()