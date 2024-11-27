package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */
data class BasicInfo(
    var id: Int? = null,
    var about: String? = null,
    var address: String? = null,
//    var Alt_Email: String? = null,
    var alt_email: String? = null,
    var emp_birthdate: String? = null,
    var bpjs: String? = null,
    var bpjs_kes: String? = null,
    var cover: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var fullname: String? = null,
    var joint: String? = null,
    var latitude: String? = null,
    var location: String? = null,
    var longtitude: String? = null,
    var nik: String? = null,
    var npwp: String? = null,
    var phone: String? = null,
    var photo_profile: String? = null,
    var position: String? = null,
    var birthday: String? = null,
    var age: String? = null,
    var cv: String? = null,
    var ktp: String? = null,
    var linkedin: String? = null,
    var bank : String? = null,
    var bank_account : String? = null,
    var marital: String? = null,
    var kids: Int? = null,
    var skill: Skill? = null,
    var asgn_project_id : String? = null,
    var emp_position : String? = null,
    var emp_security : Int? = null,
    var pgroup_nscluster: List<String>? = null,
    var project_name: String? = null,
    var pgroup_regional : List<String>? = null,
    var emp_manager: String? = null,
    @SerializedName("account_setting") var accountSettings: AccountSettings? = null
) : BaseData()
