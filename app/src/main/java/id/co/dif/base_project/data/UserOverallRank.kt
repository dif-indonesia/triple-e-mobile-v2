package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class UserOverallRank(
    @SerializedName("area") val area: String ,
    @SerializedName("avg_login") val avgLogin: Int,
    @SerializedName("name") val name: String? = null,
    @SerializedName("photo") val photo: String? = null,
    @SerializedName("score") val score: Int,
    @SerializedName("score_detail") val scoreDetail: ScoreDetail,
    @SerializedName("site") val site: Int,
    @SerializedName("team") val team: Int,
    @SerializedName("tt") val tt: Int,
    @SerializedName("tt_file") val ttFile: Int,
    @SerializedName("tt_image") val ttImage: Int,
    @SerializedName("tt_notes") val ttNotes: Int,
    @SerializedName("tt_time_to_accepted") val ttTimeToAccepted: Int,
) : BaseData()

data class ScoreDetail(
    @SerializedName("score_accepted") val scoreAccepted: Int,
    @SerializedName("score_file") val scoreFile: Int,
    @SerializedName("score_image") val scoreImage: Int,
    @SerializedName("score_minutes") val scoreMinutes: Int,
    @SerializedName("score_notes") val scoreNotes: Int,
    @SerializedName("score_ticket") val scoreTicket: Int,
)
