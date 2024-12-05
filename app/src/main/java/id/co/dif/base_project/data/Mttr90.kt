package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class Mttr90Data(
    @SerializedName("list") val list: Mttr90,
) : BaseData()

data class Mttr90(
    @SerializedName("mttr") val mttr: String,
) : BaseData()
