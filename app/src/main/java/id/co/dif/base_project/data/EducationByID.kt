package id.co.dif.base_project.data

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class EducationByID(

    var id: Int = 0,
    var school: String? = null,
    var graduate: String? = null,
    var description: String? = null,
    var connection1: String? = null,
    var connection2: String? = null,
    var connection3: String? = null,
    var attended_for: String? = null,
    var time_priode_from: String? = null,
    var time_priode_until: String? = null

) : BaseData()
