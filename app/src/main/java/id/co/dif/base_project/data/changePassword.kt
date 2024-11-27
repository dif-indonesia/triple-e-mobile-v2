package id.co.dif.base_project.data

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class changePassword(

    var old_password: String? = null,
    var new_password: String? = null,
    var confirm_new_password: String? = null

) : BaseData()
