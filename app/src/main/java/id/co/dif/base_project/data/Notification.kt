package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class Notification(

    @SerializedName("emp_id") val empId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("notif_date") val notifDate: String,
    @SerializedName("notif_from") val notifFrom: String,
    @SerializedName("notif_from_id") val ticketId: String,
    @SerializedName("notif_message") val notifMessage: String,
    @SerializedName("notif_show") var notifShow: Boolean,
) : BaseData()
