package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class SparepartData(

    @SerializedName("limit") var limit: Int? = null,
    @SerializedName("list") var list: SparePart? = null,
    @SerializedName("page") var page: Int? = null,
    @SerializedName("total") var total: Int? = null

)

open class SparePart(
    @SerializedName("spreq_code", alternate = ["sp_product_code"]) val spreqCode: String? = null,
    @SerializedName("spreq_followup") val spreqFollowup: String? = null,
    @SerializedName("spreq_id") val spreqId: String? = null,
    @SerializedName("spreq_name", alternate = ["sp_product_name"]) val spreqName: String? = null,
    @SerializedName("spreq_nscluster") val spreqNscluster: String? = null,
    @SerializedName("spreq_project_id") val spreqProjectId: String? = null,
    @SerializedName("spreq_quantity") val spreqQuantity: String? = null,
    @SerializedName("spreq_treatment") val spreqTreatment: String? = null,
    @SerializedName("spreq_system") val spreqSystem: String? = null,
    @SerializedName("spreq_vendor") val spreqVendor: String? = null,
    @SerializedName("spreq_request_time") val spreqRequestTime: String? = null,
    @SerializedName("spreq_sn") val spreqSn: String? = null,
    @SerializedName("spreq_snf") val spreqSnf: String? = null,
    @SerializedName("spreq_status") val spreqStatus: String? = null,
    @SerializedName("spreq_ticket") val spreqTicket: String? = null,
    @SerializedName("spreq_update") val spreqUpdate: String? = null,

    @SerializedName("spare_category"   ) var spareCategory  : String? = null,
    @SerializedName("spare_code"       ) var spareCode      : String? = null,
    @SerializedName("spare_id"         ) var spareId        : Int?    = null,
    @SerializedName("spare_name"       ) var spareName      : String? = null,
    @SerializedName("spare_project_id" ) var spareProjectId : Int?    = null,
    @SerializedName("spare_system"     ) var spareSystem    : String? = null,
    @SerializedName("spare_treatment"  ) var spareTreatment : String? = null,
    @SerializedName("spare_vendor"     ) var spareVendor    : String? = null
) : BaseData()

class SparePartByCode : SparePart() {
    override fun toString(): String {
        return "${spareCode}";
    }
}

class SparePartByName : SparePart() {
    override fun toString(): String {
        return "${spareName}";
    }
}

