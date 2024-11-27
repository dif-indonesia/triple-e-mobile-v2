package id.co.dif.base_project.data

import android.graphics.Bitmap

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */

open class MarkerData : BaseData() {
    val limit: Int = 0
    val list: LocationListTypes? = null
}

open class MarkerItem : MarkerData() {
    val image: String? = null
    val latitude: Double = 0.0
    val longtitude: Double = 0.0
    val site_addre_street: String? = null
    val tic_id: Int = 0
    val tic_pos: String? = null
    val tic_site: String? = null
    val type: String? = "tower"
    var bitmap: Bitmap? = null
//    val tic_type: String? = "tower"
//
//    val tic_pos: String? = null
//    val tic_site: String? = null
//    val tic_id: String = ""
//    val id: Int = 0
//    val lat: Double = 0.0
//    val lng: Double = 0.0
//    val latitude: Double = 0.0
//    val longitude: Double = 0.0
//    val type: String = ""
//    val image: String? = null
//    var bitmap: Bitmap? = null
}

data class LocationListTypes(
    val ttMapAll: Array<MarkerItem> = emptyArray()
) : BaseData()
