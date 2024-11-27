package id.co.dif.base_project.data

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class Ticket(

    val tic_number:String?,
    val tic_number_assigned: String?,
    val tic_severety:String?,
    val tic_type: String?,
    val tic_assign_to: String?,
    val tic_field_engineer: String?,
    val tic_site_id: String?,
    val tic_site: String?,
    val tic_area: String?,
    val tic_person_in_charge:String?,
    val tic_status: String?,
    val tic_accepted: Int?,
    val tic_closed: Int?,
    val tic_sparepart: Int?,
    val tic_notes: String?



) : BaseData()
