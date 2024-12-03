package id.co.dif.base_project.data

data class RcaResponse(
    val limit: Int,
    val list: Rca,
    val page: Int,
    val total: Int
)

class Rca (
    val mrca_owner: String,
    val mrca_rc_category: String,
    val mrca_rc1: String,
    val mrca_rc2: String,
    val rca_notes: String,
    val rca_rc_category: String,
    val rca_rc_tier1: String,
    val rca_rc_tier2: String,
    val rca_remarks: String,
    val rca_resolution_action: String,
    val rca_responsible_party: String,
    val rca_rh_start: String,
    val rca_rh_start_time: String,
    val rca_rh_stop: String,
    val rca_rh_stop_time: String
) : BaseData()