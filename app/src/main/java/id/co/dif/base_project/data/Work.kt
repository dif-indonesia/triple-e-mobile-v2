package id.co.dif.base_project.data

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class Work(
    var id: String? = null,
    var city: String? = null,
    var company: String? = null,
    var description: String? = null,
    var position: String? = null,
    var time_priode_from: String? = null,
    var time_priode_until: String? = null
) : BaseData()
