package id.co.dif.base_project.utils

import android.annotation.SuppressLint
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*


/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 10:34
 *
 */


fun dateNow(): String? {
    return DateTimeFormat.forPattern("dd-MM-yyyy").print(DateTime.now())
}
fun timeMilis(): String? {
    return DateTimeFormat.forPattern("yyMMddHHmmssSSS").print(DateTime.now())
}
fun dateNowFormat(): String? {
    return DateTimeFormat.forPattern("dd MMMM yyyy").print(DateTime.now())
}
fun Date.format(format: String): String? {
    return DateTimeFormat.forPattern(format).print(time)
}
fun timeNow(): String? {
    return DateTimeFormat.forPattern("HH:mm").print(DateTime.now())
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SuppressLint("SimpleDateFormat")
fun String?.formatDate(from: String, to: String): String {
    return when {
        this != null -> {
            try {
                val sdfIn = SimpleDateFormat(from)
                val sdfOut = SimpleDateFormat(to)
                val dateIn = sdfIn.parse(this)
                sdfOut.format(dateIn)
            } catch (e: Exception) {
                this
            }
        }
        else -> {
            this ?: ""
        }
    }
}

fun String.formatDateBelitung(inputPattern: String, outputPattern: String): String? {
    return try {
        val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
        val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) }
    } catch (e: Exception) {
        null
    }
}



fun getDiffYears(first: Date?, last: Date?): Int {
    val a = getCalendar(first)
    val b = getCalendar(last)
    var diff = b[Calendar.YEAR] - a[Calendar.YEAR]
    if (a[Calendar.MONTH] > b[Calendar.MONTH] || a[Calendar.MONTH] === b[Calendar.MONTH] && a[Calendar.DATE] > b[Calendar.DATE]) {
        diff--
    }
    return diff
}

fun getCalendar(date: Date?): Calendar {
    val cal = Calendar.getInstance(Locale.US)
    cal.time = date
    return cal
}

