package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class TicketDetails(

    @SerializedName(value="site_info", alternate= ["info_site"])
    var site_info: SiteDetails? = null,
    var site_history: List<SiteDetails>? = null,
    var tic_accepted: Boolean? = null,
    var tic_area: String? = null,
    var tic_assign_to: String? = null,
    var tic_changelog: List<changelog>? = null,
    var tic_closed: Boolean? = null,
    var tic_field_engineer: String? = null,
    var tic_field_engineer_id: String? = null,
    var tic_id: String? = null,
    var tic_notes: List<Note>? = null,
    var tic_number: String? = null,
    var tic_number_assigned:String? = null,
    var tic_person_in_charge: String? = null,
    var tic_person_in_charge_id: String? = null,
    var tic_received: String? = null,
    var tic_severety: String? = null,
    var tic_site: String? = null,
    var tic_site_id: String? = null,
    var tic_sparepart: Boolean? = null,
    var tic_status: String? = null,
    var tic_type: String? = null,
    var tower: String? = null,
    var tic_detail_sparePart: List<SparePart>? = null,
    var tic_system: String? = null,
    var tic_system_sub: String? = null,
    var status_ticket: Boolean? = null,
    var tic_update_at: String? = null,
    var permit_status: PermitStatus? = null


) : BaseData()
