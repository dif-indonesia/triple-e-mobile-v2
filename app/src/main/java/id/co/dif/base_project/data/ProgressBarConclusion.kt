package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class ProgressBarConclusion(
    @SerializedName("added_notes") val addedNotes: AddedNotes,
    @SerializedName("file_upload") val fileUploads: FileUploads,
    @SerializedName("photo_upload") val photoUploads: PhotoUploads
):BaseData()


data class AddedNotes(
    @SerializedName("greater_than_100") val greaterThan100: Int,
    @SerializedName("month") val month: Trend,
    @SerializedName("week") val week: Trend,
)

data class FileUploads(
    @SerializedName("all_ticket") val allTicket: Int,
    @SerializedName("month") val month: Trend,
    @SerializedName("week") val week: Trend,
)

data class PhotoUploads(
    @SerializedName("all_ticket") val allTicket: Int,
    @SerializedName("month") val month: Trend,
    @SerializedName("week") val week: Trend,
)

data class Trend(
    @SerializedName("icon") val icon: String,
    @SerializedName("num_percent") val numPercent: Int
)