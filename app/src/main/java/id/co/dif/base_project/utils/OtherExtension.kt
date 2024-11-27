package id.co.dif.base_project.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.base_project.centerPointOfIndonesia
import timber.log.Timber
import java.lang.Integer.min

fun Int.drawableRes(context: Context): Drawable? {
    return AppCompatResources.getDrawable(context, this)
}

fun Int.stringRes(context: Context): String {
    return context.getString(this)
}

fun Int.colorRes(context: Context): Int {
    return ContextCompat.getColor(context, this)
}

val Boolean.int
    get() = this.compareTo(false)
val Int.bool
    get() = this == 1

fun <T> Boolean.eval(optionIfTrue: T, optionIfFalse: T): T {
    return if (this) {
        optionIfTrue
    } else {
        optionIfFalse
    }
}

val Boolean.asVisibility
    get() = listOf(View.GONE, View.VISIBLE)[this.int]
val String?.str
    get() = this.toString()
val Long.megaBytes
    get() = this * 1024 * 1024
val Long.kiloBytes
    get() = this * 1024

fun Drawable.asBitmap(): Bitmap? {
    val bitmap = Bitmap.createBitmap(
        this.intrinsicWidth,
        this.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

fun Bitmap.circularCrop(): Bitmap {
    val squareBitmap = this.centerCrop()
    val size = squareBitmap.width.coerceAtMost(this.height)
    val circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(circularBitmap)
    val paint = Paint()
    paint.isAntiAlias = true
    val radius = size / 2f
    canvas.drawCircle(radius, radius, radius, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(
        squareBitmap,
        (size - squareBitmap.width) / 2f,
        (size - squareBitmap.height) / 2f,
        paint
    )
    return Bitmap.createScaledBitmap(circularBitmap, squareBitmap.width, squareBitmap.height, false)
}

fun Bitmap.centerCrop(): Bitmap {
    val sourceWidth = width
    val sourceHeight = height
    val targetSize = min(sourceHeight, sourceWidth)
    val startX = (sourceWidth - targetSize) / 2
    val startY = (sourceHeight - targetSize) / 2
    return Bitmap.createBitmap(
        this,
        startX,
        startY,
        targetSize,
        targetSize
    )
}

fun String?.orDefault(default: String = "-"): String {
    return if (isNullOrEmpty()) default else this
}

fun Boolean?.orDefault(default: Boolean = false): Boolean {
    return this ?: default
}

fun AppCompatActivity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun PopupWindow.dimBehind() {
    val container = contentView.rootView
    val context = contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val p = container.layoutParams as WindowManager.LayoutParams
    p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    p.dimAmount = 0.3f
    wm.updateViewLayout(container, p)
}

fun <T : ClusterItem> ClusterManager<T>.addValidItem(item: T): Boolean {
    val isValid = item.markerIsValid()
    if (isValid) {
        this.addItem(item)
    }
    return isValid
}

fun ClusterItem.markerIsValid(): Boolean {
    // indonesia
    val distance = centerPointOfIndonesia.distanceTo(this.position)
    return distance < 5 * 1000 * 1000
}

@SuppressLint("LogNotTimber")
@Deprecated(
    message = "Debugging purposes. This method logs the value of the object and return itself",
    level = DeprecationLevel.WARNING
)
fun <T> T?.logNullable(title: String = "Log"): T? {
    Log.d("LOGGGGG", "$title: $this")
    return this
}

@SuppressLint("LogNotTimber")
@Deprecated(
    message = "Debugging purposes. This method logs the value of the object and return itself",
    level = DeprecationLevel.WARNING
)
fun <T> T.log(title: String = "Log"): T {
    Log.d("LOGGGGG", "$title: $this")
    return this
}

@SuppressLint("LogNotTimber")
@Deprecated(
    message = "Debugging purposes. This method logs the value of the object and return itself",
    level = DeprecationLevel.WARNING
)
fun <T> T.log(title: String = "Log", block: (T) -> Any?): T {
    Log.d("LOGGGGG", "$title: ${block(this)}")
    return this
}

@SuppressLint("LogNotTimber")
@Deprecated(
    message = "Debugging purposes. This method logs the value of the object and return itself",
    level = DeprecationLevel.WARNING
)
fun log(vararg data: Any?) {
    Log.d("LOGGGGG", "${data.joinToString { it.toString() }}")
}

@Deprecated(
    message = "Debugging purposes. This method logs the value of the object and return itself",
    level = DeprecationLevel.WARNING
)
fun <T> T.timberLog(title: String = "Log"): T {
    Timber.tag("LOGGGGG").d("$title: $this")
    return this
}

@Deprecated(
    message = "Debugging purposes. This method logs the value of the object and return itself",
    level = DeprecationLevel.WARNING
)
fun <T> T?.timberLogNullable(title: String = "Log"): T? {
    Timber.tag("LOGGGGG").d("$title: $this")
    return this
}