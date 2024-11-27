package id.co.dif.base_project.data

data class UsageRank(
    val profilePicUrl: String? = null,
    val organization : String,
    val profileCompletion: String,
    val username: String,
    val position: Int,
): BaseData()
