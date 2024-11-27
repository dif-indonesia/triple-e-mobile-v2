package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class UsersInfoDashboard(
    @SerializedName("registered_users") val registeredUsers: Int,
    @SerializedName("internal_users") val internalUsers: Int,
    @SerializedName("external_users") val externalUsers: Int,
    @SerializedName("online_users") val onlineUsers: Int,
) : BaseData()
