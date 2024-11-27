package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class Note(
    var date: String? = null,
    var file: String? = null,
    var id: Int? = null,
    @SerializedName("id_user") var userId: String? = null,
    var note: String? = null,
    @SerializedName("longtitude") var longitude: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("is_within_radius") var isWithinRadius: String,
    var tic_status: String? = null,
    var time: String? = null,
    var username: String? = null,
    val tic_distance: Double? = null
): BaseData(){
    override fun toString(): String {
        return "{id: $id, date: $date, file: $file, note: $note, tic_status: $tic_status, time, $time, username: $username, tic_distance: $tic_distance, latitude: $latitude, longitude: $longitude}"
    }
}
