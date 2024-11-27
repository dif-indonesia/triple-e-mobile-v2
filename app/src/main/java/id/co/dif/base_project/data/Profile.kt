package id.co.dif.base_project.data

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class Profile(
    var id: String? = null,
    var emp_address: String? = null,
    var emp_alt_email: String? = null,
    var emp_birthdate: String? = null,
    var emp_bpjs: String? = null,
    var emp_bpjs_kes: String? = null,
    var emp_email: String? = null,
    var emp_gender: String? = null,
    var emp_latitude: String? = null,
    var emp_location: String? = null,
    var emp_longtitude: String? = null,
    var emp_mobile: String? = null,
    var emp_name: String? = null,
    var emp_nik: String? = null,
    var emp_npwp: String? = null,
    var emp_remarks: String? = null,
    var emp_skill: String? = null,
    var emp_update: String? = null

) : BaseData()
