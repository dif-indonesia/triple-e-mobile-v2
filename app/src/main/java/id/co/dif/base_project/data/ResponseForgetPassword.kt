package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class ResponseForgetPassword(
    @SerializedName("email") val email: String,

) : BaseData()
