package id.co.dif.base_project.data

import androidx.navigation.Navigator
import com.google.gson.annotations.SerializedName

data class Project(
    @SerializedName("asgn_project_id"   ) var asgnProjectId   : String? = null,
    @SerializedName("asgn_project_name" ) var asgnProjectName : String? = null,
    @SerializedName("id"                ) var id              : Int?    = null,

) : BaseData()
