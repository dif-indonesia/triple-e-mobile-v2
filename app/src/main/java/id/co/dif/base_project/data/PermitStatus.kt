package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

class PermitStatus (
    @SerializedName("permit_approved") val permitApproved: Boolean?,
    @SerializedName("permit_information") val permitInformation: String?,
    @SerializedName("permit_id") val permitId: Int?
): BaseData(){
    override fun toString(): String {
        return "{permit_approved: $permitApproved, permit_information: $permitInformation}"
    }
}
