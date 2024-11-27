package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class TtTimeToClosed(
    @SerializedName("accepted") val accepted: String,
    @SerializedName("closed") val closed: String,
    @SerializedName("pie_chart") val pieChart: List<PlainValueLabel>
) : BaseData()
