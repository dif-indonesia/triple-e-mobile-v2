package id.co.dif.base_project.data

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import id.co.dif.base_project.utils.orDefault
import java.io.Serializable

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */
class Location(
    @SerializedName("foto", alternate = ["image"]) var image: String? = null,
    val site_name: String = "",
    val tic_site: String = "",
    val countdown: Long? = null,
    @SerializedName("latitude", alternate = ["site_latitude"])
    val latitude: String? = null,
    @SerializedName("longtitude", alternate = ["site_longtitude"]) val longtitude: String? = null,
    val site_id: Int? = null,
    var site_id_customer: String? = null,
    val site_addre_street: String? = null,
    val site_contact_name: String? = null,
    val mobile: String? = null,
    val name: String? = null,
    val address: String = "",
    val site_address_kelurahan: String? = null,
    val site_provider: String? = null,
    @SerializedName("req_location") val isRequestingLocation: Boolean? = null,
    @SerializedName("is_within_site_radius") val isWithinSiteRadius: Boolean? = null,
    val site_end_customer: String? = null,
    var type: String = "",
    @SerializedName("location_is_updated") var locationIsUpdated: Boolean? = null,
    val pgroup_nscluster: String? = null,
    val tic_area: String? = null,
    @SerializedName("site_contact_phone") val site_contact_phone: String? = null,
    val id: Int? = null,
    val load: Int = 0,
    var distance: Float = 0f,
    val completed: String? = null,
    val old: String? = null,
) : MarkerData(), ClusterItem, Serializable {
    override fun toString(): String {
        if (type == "technician") {
            return name ?: ""
        } else {
            return "$site_id_customer - $site_name"
        }
    }
    override fun getPosition(): LatLng {
        return LatLng(latitude.orDefault("0").toDouble(), longtitude.orDefault("0").toDouble())
    }

    override fun getTitle(): String? {
        if (type == "technician") {
            return name
        } else {
            return site_name
        }
    }

    override fun getSnippet(): String? {
        if (type == "technician") {
            return "Technician"
        } else {
            return pgroup_nscluster
        }
    }

    override fun getZIndex(): Float? {
        return 0f
    }
//    override fun toString(): String {
//        return "Location(image=$image, site_name='$site_name', tic_site='$tic_site', " +
//                "countdown=$countdown, latitude=$latitude, longtitude=$longtitude, " +
//                "site_id=$site_id, site_id_customer='$site_id_customer', " +
//                "site_addre_street='$site_addre_street', mobile='$mobile', name='$name', " +
//                "address='$address', site_address_kelurahan='$site_address_kelurahan', " +
//                "isRequestingLocation=$isRequestingLocation, isWithinSiteRadius=$isWithinSiteRadius, " +
//                "site_end_customer='$site_end_customer', type='$type', " +
//                "locationIsUpdated=$locationIsUpdated, pgroup_nscluster='$pgroup_nscluster', " +
//                "tic_area='$tic_area', site_contact_phone='$site_contact_phone', " +
//                "id=$id, load=$load, distance=$distance, completed='$completed', old='$old')"
//    }


}


