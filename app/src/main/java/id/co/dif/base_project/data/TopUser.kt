package id.co.dif.base_project.data

data class TopUser(
    val userPictureUrl: String? = null,
    val username: String,
    val position: String,
    val timeSpent: String
) : BaseData()
