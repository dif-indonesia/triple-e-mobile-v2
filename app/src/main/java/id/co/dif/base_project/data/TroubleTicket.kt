package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */
data class TroubleTicket(
    var status_ticket: Boolean = true,
    @SerializedName("images") var images: String? = null,
    @SerializedName("tic_accepted") var ticAccepted: Boolean? = null,
    @SerializedName("tic_area") var ticArea: String? = null,
    @SerializedName("tic_assign_to") var ticAssignTo: String? = null,
    @SerializedName("tic_closed") var ticClosed: Boolean? = null,
    @SerializedName("tic_field_engineer") var ticFieldEngineer: String? = null,
    @SerializedName("tic_closed_time") var ticClosedTime: String? = null,
    @SerializedName("tic_id") var ticId: String? = null,
    @SerializedName("tic_notes") var ticNotes: String? = null,
    @SerializedName("tic_number") var ticNumber: String? = null,
    @SerializedName("tic_number_assigned") var ticNumberAssigned: String? = null,
    @SerializedName("tic_updated") var ticUpdated: String? = null,
    @SerializedName("tic_person_in_charge") var ticPersonInCharge: String? = null,
    @SerializedName("tic_person_in_charge_emp_id") var ticPersonInChargeEmpId: Int? = null,
    @SerializedName("tic_severety") var ticSeverety: String? = null,
    @SerializedName("tic_site") var ticSite: String? = null,
    @SerializedName("tic_site_id") var ticSiteId: String? = null,
    @SerializedName("tic_sparepart") var ticSparepart: Boolean? = null,
    @SerializedName("tic_status") var ticStatus: String? = null,
    @SerializedName("tic_type") var ticType: String? = null,
    @SerializedName("tic_received") var ticReceived: String? = null,
    @SerializedName("site") var site: SiteInfo? = null,
//    @SerializedName("permit_status") var permitStatus: String? = null
    @SerializedName("permit_status") var permitStatus: TicketPermitStatus? = null,
    @SerializedName("submit_status") var submitStatus: SubmitTickettatus? = null,
    @SerializedName("tic_checkin_at") var ticCheckinAt: String? = null,
    @SerializedName("tic_checkout_at") var ticCheckoutAt: String? = null,
    @SerializedName("checkin_status") var checkinStatus: CheckinStatus? = null,
    @SerializedName("tic_read_at") var ticReadAt: String? = null
) : BaseData()

data class TicketPermitStatus(
    @SerializedName("permit_approved") val permitApproved: Boolean?,
    @SerializedName("permit_information") val permitInformation: String? = null,
    @SerializedName("permit_id") val permitId: String?
)

data class SubmitTickettatus(
    @SerializedName("submit_approved") val submitApproved: Boolean?,
    @SerializedName("submit_approve_information") val submitApprovedInformation: String?,
    @SerializedName("submit_approved_at") val submitApprovedAt: String?,
    @SerializedName("submit_id") val submitId: Int? = null,
    @SerializedName("submit_information") val submitInformation: String?
)

data class SiteInfo(
    @SerializedName("site_id") val siteId: Int?,
    @SerializedName("site_latitude") val siteLatitude: String? = null,
    @SerializedName("site_longtitude") val siteLangtitude: String? = null,
    @SerializedName("site_radius") val siteRadius: Int? = null
)

data class CheckinStatus(
    @SerializedName("checkin_approved") val checkinApproved: Boolean?,
    @SerializedName("checkin_id") val checkinId: Int? = null,
    @SerializedName("checkin_information") val checkinInformation: String? = null,
    @SerializedName("checkin_approved_at") val checkinApprovedAt: String? = null,
    @SerializedName("checkin_request_at") val checkinRequestAt: String? = null,
    @SerializedName("checkin_eviden") val checkinEviden: String? = null
)