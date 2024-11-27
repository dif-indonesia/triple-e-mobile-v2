package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

class SessionLogin (

    @SerializedName("log_end"  ) var logEnd  : String? = null,
    @SerializedName("log_id"   ) var logId   : String? = null,
    @SerializedName("log_time" ) var logTime : String? = null

) : BaseData()