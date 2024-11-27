package id.co.dif.base_project.base

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class BaseResponseListBoolean<T>(
    val status : Boolean,
    val message : String,
    val data : DataList<T>
)
