package id.co.dif.base_project.data

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class Technician(
    val id: Int?,
    val name: String,
    val site_addre_street: String?,
    val site_address_kelurahan: String?,
    val site_end_customer: String?,
    val site_id: Int?,
    val site_name: String,
    val load: Int,
    val distance: String,
    val address: String?,
    val completed: String?,
    val foto: String?,
    val old: String?,
    val latitude: Double,
    val longtitude: Double,
    val type: String?,
//    val tic_status: <>

) : MarkerData()

