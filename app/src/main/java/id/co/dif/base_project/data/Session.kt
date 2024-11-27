package id.co.dif.base_project.data

data class Session(
    var id: String? = null,
    var asgn_project_id: String? = null,
    var email: String? = null,
    var emp_position: String? = null,
    var emp_security: Int? = null,
    var expire: String? = null,
    var expire_refresh_token: String? = null,
    var name: String? = null,
    var join_team: String? = null,
    var permission: List<Any>? = null,
    var project_name: String? = null,
    var role: Role? = null,
    var secret_key: String? = null,
    var token_access: String? = null,
    var token_refresh: String? = null,
    var pgroup_nscluster: List<String>? = null,

    var tmp_token_access: String? = null,
)  : BaseData()