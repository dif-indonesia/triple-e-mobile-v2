package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

class PendingStatus (
    @SerializedName("issuer") val issuer: Issuer?,
    @SerializedName("pnd_approved") val pdnApproved: Boolean?,
    @SerializedName("pnd_approved_at") val pdnApprovedAt: String?,
    @SerializedName("pnd_id") val pndId: Int?,
    @SerializedName("pnd_information") val pdnInformation: String?,
    @SerializedName("pnd_reason") val pdnReason: String?,
    @SerializedName("pnd_request_at") val pdnRequestAt: String?,
    @SerializedName("pnd_canceled") val pndCanceled: Boolean?,
    @SerializedName("pnd_canceled_date") val pndCanceledAt: String?
): BaseData()

data class Issuer(
    @SerializedName("emp_id") val empId: Int?,
    @SerializedName("emp_name") val empName: String? = null
)
