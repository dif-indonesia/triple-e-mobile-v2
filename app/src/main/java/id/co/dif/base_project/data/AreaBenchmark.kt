package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class AreaBenchmark(
    val labels: List<String>,
    val values: AreaBenchmarkValues
) : BaseData()

data class AreaBenchmarkValues(
    @SerializedName("close") val closed: List<Int>,
    @SerializedName("open")  val onGoing: List<Int>,
    @SerializedName("tiket") val new: List<Int>
)
