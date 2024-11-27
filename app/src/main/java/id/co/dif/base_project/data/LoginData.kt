package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("name") val name: String,
    @SerializedName("secret_key") val secretKey: String,
    @SerializedName("user_id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("email_message") val emailMessage: String? = null,
    @SerializedName("password_message") val passwordMessage: String? = null,
) : BaseData()
