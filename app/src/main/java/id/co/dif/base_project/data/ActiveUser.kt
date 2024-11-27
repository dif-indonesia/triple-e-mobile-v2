package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class ActiveUser(
    @SerializedName("foto") val image: String?,
    @SerializedName("name") val username: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("last_login") val lastLogin: String?
) : BaseData()