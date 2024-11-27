package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class LatestAppVersion(
    @SerializedName("version-code") val versionCode: Int,
    @SerializedName("download-url") val downloadUrl: String
): BaseData()