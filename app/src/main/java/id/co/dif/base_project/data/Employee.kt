package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */
class Employee(
    @SerializedName("emp_name") val employeeName: String?,
    @SerializedName("emp_id") val employeeId: Int?
) {
    override fun toString(): String {
        return employeeName ?: ""
    }
}


