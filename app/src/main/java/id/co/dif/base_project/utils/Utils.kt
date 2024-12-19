package id.co.dif.base_project.utils

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.location.Location
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.ui.IconGenerator
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import id.co.dif.base_project.ALARM_REQUEST_CODE
import id.co.dif.base_project.END_HOUR_LOCATION_SCHEDULE
import id.co.dif.base_project.END_MINUTE_LOCATION_SCHEDULE
import id.co.dif.base_project.R
import id.co.dif.base_project.START_HOUR_LOCATION_SCHEDULE
import id.co.dif.base_project.START_MINUTE_LOCATION_SCHEDULE
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.data.LastLocation
import id.co.dif.base_project.data.LocationScheduleCommand
import id.co.dif.base_project.data.LocationScheduleInterval
import id.co.dif.base_project.data.LocationType
import id.co.dif.base_project.databinding.ItemMapClusterMarkerBinding
import id.co.dif.base_project.databinding.ItemMapMarkerBinding
import id.co.dif.base_project.imageExtensions
import id.co.dif.base_project.service.LocationService
import id.co.dif.base_project.service.LocationServiceStarterScheduleReceiver
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Calendar
import java.util.Locale
import java.util.function.Consumer
import kotlin.math.min
import kotlin.math.roundToInt

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 10:06
 *
 */
fun Int.toDp(resources: Resources): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    )
}

fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager = activity.getSystemService(
        AppCompatActivity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    if (inputMethodManager.isAcceptingText) {
        activity.currentFocus?.let { currentFocus ->
            inputMethodManager.hideSoftInputFromWindow(
                currentFocus.windowToken,
                0
            )
        }
    }
}

fun base64ImageToBitmap(encodedImage: String): Bitmap {
    val cleanEncodedImage: String = encodedImage.substring(encodedImage.indexOf(",") + 1)
    val decodedString: ByteArray = Base64.decode(cleanEncodedImage, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

fun base64ImageToBitmapHandle(encodedImage: String): Bitmap? {
    return try {
        val cleanEncodedImage = if (encodedImage.contains(",")) {
            encodedImage.substring(encodedImage.indexOf(",") + 1)
        } else {
            encodedImage
        }
        val decodedString = Base64.decode(cleanEncodedImage.trim(), Base64.DEFAULT)
        Log.d("Base64Image", "Decoded Byte Array Length: ${decodedString.size}")
        // Jika ukuran terlalu besar, kembalikan null atau tangani sesuai kebutuhan
        if (decodedString.size > 10_000_000) { // Contoh batas ukuran 10MB
            Log.e("Base64ImageError", "Image too large to decode")
            return null
        }
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: IllegalArgumentException) {
        Log.e("Base64ImageError", "Error decoding Base64 string: ${e.message}")
        null
    }
}



@RequiresApi(Build.VERSION_CODES.N)
suspend fun rotateImageCorrectly(context: Context, photoFile: File): File {
    val sourceBitmap =
        MediaStore.Images.Media.getBitmap(
            context?.contentResolver,
            photoFile.toUri()
        )
    val exif = ExifInterface(photoFile.inputStream())
    val rotation =
        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    val rotationInDegrees = when (rotation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        ExifInterface.ORIENTATION_TRANSVERSE -> -90
        ExifInterface.ORIENTATION_TRANSPOSE -> -270
        else -> 0
    }
    Log.d("TAG", "rotateImageCorrectly: $rotationInDegrees")
    val matrix = Matrix().apply {
        if (rotation != 0) preRotate(rotationInDegrees.toFloat())
    }
    val rotatedBitmap = Bitmap.createBitmap(
        sourceBitmap, 0, 0, sourceBitmap.width, sourceBitmap.height, matrix, true
    )

    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(photoFile))

    sourceBitmap.recycle()
    rotatedBitmap.recycle()
    return photoFile
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

val Int.toDp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun TextView.copyClipboard(message: String) {
    val clipboard = this.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("label", text)
    clipboard?.setPrimaryClip(clip)
}

fun Context.getStatusBarHeight(): Int {
    val height: Int
    val idStatusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
    height = if (idStatusBarHeight > 0) {
        resources.getDimensionPixelSize(idStatusBarHeight)
    } else {
        0
    }
    return height
}

fun makeMultipartData(key: String, requestData: Any?): MultipartBody.Part {
    return when (requestData) {
        is Boolean -> MultipartBody.Part.createFormData(key, if (requestData) "true" else "false")
        is File -> {
            val requestFile = requestData.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(key, requestData.name, requestFile)
        }

        else -> MultipartBody.Part.createFormData(key, requestData.toString())
    }
}

private fun openPdf(fileUri: Uri, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(fileUri, "application/pdf")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Download completed!", Toast.LENGTH_SHORT).show()
    }
}

fun Context.copyImageToFile(uri: Uri): File {
    val file = File.createTempFile("IMG_", ".jpg", cacheDir)
    file.outputStream().use {
        contentResolver.openInputStream(uri)?.copyTo(it)
    }
    return file
}

fun Context.compressImage(file: File, quality: Int = 60) {

}

fun Context.getImageFromUri(uri: Uri): File {
    Log.d("TAG", "getImageFromUri: asdasd ${uri.toString()}")
    val file = File.createTempFile("IMG_", ".jpg", cacheDir)
    file.outputStream().use {
        contentResolver.openInputStream(uri)?.copyTo(it)
    }
    val bitmap = BitmapFactory.decodeFile(file.path)
    val croppedBitmap = bitmap.centerCrop()
    file.outputStream().use {
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, it)
    }
    return file
}

fun Context.getImageFromUri(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    val croppedBitmap = bitmap.centerCrop()
    file.outputStream().use {
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, it)
    }
    return file
}

fun isImageMimeType(mimeType: String?): Boolean {
    // Get the file extension from the MIME type
    var fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    if (fileExtension != null) {
        // Convert the file extension to lowercase for comparison
        fileExtension = fileExtension.lowercase(Locale.getDefault())
        // List of common image file extensions
        val imageExtensions = arrayOf("jpg", "jpeg", "png", "gif", "bmp")
        // Check if the file extension is in the list of image extensions
        for (imageExt in imageExtensions) {
            if (fileExtension == imageExt) {
                return true // It's an image MIME type
            }
        }
    }
    return false // It's not an image MIME type
}

fun getMimeType(context: Context, uri: Uri): String? {
    val mimeType: String? = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        val cr: ContentResolver = context.contentResolver
        cr.getType(uri)
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
            uri
                .toString()
        )
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.lowercase(Locale.getDefault())
        )
    }
    return mimeType
}

fun getExtensionFromMimeType(mimeType: String): String? {
    val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    return extension?.lowercase()
}

fun getMimeTypeFromExtension(extension: String?): String? {
    var type: String? = null
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

fun Context.getFileFromUri(context: Context, uri: Uri): File {
    val mimeType = getMimeType(context, uri)
    val extension = getExtensionFromMimeType(mimeType.orDefault(""))
    val file = File.createTempFile("doc", ".$extension", cacheDir)
    file.outputStream().use {
        contentResolver.openInputStream(uri)?.use { d ->
            d.copyTo(it)
        }
    }

    return file
}

suspend fun Context.compressImageFileToDefinedSize(maxSizeBytes: Long, uri: Uri): File {
    val bytes = contentResolver.openInputStream(uri)?.use {
        it.readBytes()
    } ?: return uri.toFile()
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    var outputBytes: ByteArray
    var quality = 70
    do {
        val outputStream = ByteArrayOutputStream()
        outputStream.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputBytes = outputStream.toByteArray()
            quality = (quality * 0.9).roundToInt()
        }
    } while (outputBytes.size > maxSizeBytes && quality > 5)
    val file = File(cacheDir, "Image_${System.currentTimeMillis()}.jpg")
    file.writeBytes(outputBytes)
    return file
}

fun getFileSizeInBytes(file: File): Long {
    return if (file.exists()) {
        file.length()
    } else {
        -1 // File doesn't exist
    }
}

fun getFileNameFromUri(context: Context, uri: Uri?): String {
    uri?.let {
        context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val fileName = cursor.getString(nameIndex)
            val index = fileName.lastIndexOf('.')
            return if (index == -1) {
                fileName
            } else {
                fileName.substring(0, index)
            }
        }
    }
    return "Unknown file name"
}

fun shimmerDrawable(
    duration: Long = 800,
    baseAlpha: Float = 0.98f,
    highlightAlpha: Float = 0.85f,
    direction: Int = Shimmer.Direction.LEFT_TO_RIGHT,
    autoStart: Boolean = true,
): ShimmerDrawable {
    val shimmer =
        Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
            .setBaseAlpha(baseAlpha).setHighlightAlpha(highlightAlpha)
            .setDuration(duration) // how long the shimmering animation takes to do one full sweep
            .setDirection(direction).setAutoStart(autoStart)
            .build()
// This is the placeholder for the imageView
    val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }
    return shimmerDrawable
}

fun md5(s: Any): String {
    try {
        // Create MD5 Hash
        val digest = MessageDigest.getInstance("MD5")
        digest.update(s.hashCode().toString().toByteArray())
        val messageDigest = digest.digest()
        // Create Hex String
        val hexString = StringBuilder()
        for (aMessageDigest in messageDigest) {
            var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
            while (h.length < 2) h = "0$h"
            hexString.append(h)
        }
        return hexString.toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

@RequiresApi(api = Build.VERSION_CODES.S)
fun Window.setupWindowBlur(blurBehindRadius: Int) {
    addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
    val listener: Consumer<Boolean> = Consumer<Boolean> {
        attributes?.blurBehindRadius = blurBehindRadius
        attributes = attributes
    }
    decorView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            windowManager?.addCrossWindowBlurEnabledListener(listener)
        }

        override fun onViewDetachedFromWindow(v: View) {
            windowManager?.removeCrossWindowBlurEnabledListener(listener)
        }
    })
}

fun Context.showSoftKeyboard(view: View) {
    try {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    } catch (e: Exception) {
        Unit
    }
}

fun Context.getClipboard(): String? {
    try {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboardManager.hasPrimaryClip()) {
            val clipData = clipboardManager.primaryClip
            val item = clipData?.getItemAt(0)
            return item?.text?.toString()
        }
        return null
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun getCircularBitmap(srcBitmap: Bitmap?): Bitmap {
    val squareBitmapWidth = min(srcBitmap!!.width, srcBitmap.height)
    // Initialize a new instance of Bitmap
    // Initialize a new instance of Bitmap
    val dstBitmap = Bitmap.createBitmap(
        squareBitmapWidth,  // Width
        squareBitmapWidth,  // Height
        Bitmap.Config.ARGB_8888 // Config
    )
    val canvas = Canvas(dstBitmap)
    // Initialize a new Paint instance
    // Initialize a new Paint instance
    val paint = Paint()
    paint.isAntiAlias = true
    val rect = Rect(0, 0, squareBitmapWidth, squareBitmapWidth)
    val rectF = RectF(rect)
    canvas.drawOval(rectF, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    // Calculate the left and top of copied bitmap
    // Calculate the left and top of copied bitmap
    val left = ((squareBitmapWidth - srcBitmap.width) / 2).toFloat()
    val top = ((squareBitmapWidth - srcBitmap.height) / 2).toFloat()
    canvas.drawBitmap(srcBitmap, left, top, paint)
    // Free the native object associated with this bitmap.
    // Free the native object associated with this bitmap.
    srcBitmap.recycle()
    // Return the circular bitmap
    // Return the circular bitmap
    return dstBitmap
}

fun animateProgressBar(
    progressBar: ProgressBar, percent: Int, smoothRatio: Float = 3f, duration: Long = 500L
) {
    val maxValue = (100 * smoothRatio).roundToInt()
    progressBar.max = maxValue
    val progressValue = percent * maxValue / 100
    ObjectAnimator.ofInt(progressBar, "progress", progressValue).setDuration(duration).start();
}

fun loadImage(context: Context, url: String?): Bitmap? {
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    return try {
        val inputStream = URL(url).openStream()
        BitmapFactory.decodeStream(inputStream)
    } catch (_: Exception) {
        R.drawable.ic_person.drawableRes(context)?.asBitmap()
    }
}

fun Context.toBitmapDescriptor(@DrawableRes id: Int): BitmapDescriptor? {
    return BitmapDescriptorFactory.fromBitmap(
        makeClusterItemMarker(
            id.drawableRes(this)
                ?.asBitmap()!!
                .circularCrop()
        )
    )
}

fun Context.makeClusterItemMarker(bitmap: Bitmap, item: id.co.dif.base_project.data.Location? = null): Bitmap {
    val li = LayoutInflater.from(this)
    val markerBinding = ItemMapMarkerBinding.inflate(li)
    markerBinding.imgMarker.setImageBitmap(bitmap)
    item?.locationIsUpdated?.let { isReady ->
        val isAlertIsOn = !isReady
        markerBinding.viewAlert.isVisible = isAlertIsOn
    }
    val iconGen = IconGenerator(this).apply {
        setContentView(markerBinding.root)
        setBackground(null)
    }

    return Bitmap.createScaledBitmap(iconGen.makeIcon(), 50.toDp, 62.toDp, false)
}

fun Context.makeClusterMarker(itemCount: Int): Bitmap {
    val li = LayoutInflater.from(this)
    val markerBinding = ItemMapClusterMarkerBinding.inflate(li)
    markerBinding.itemCount.text = itemCount.toString()
    val iconGen = IconGenerator(this).apply {
        setContentView(markerBinding.root)
        setBackground(null)
    }

    return Bitmap.createScaledBitmap(iconGen.makeIcon(), 40.toDp, 40.toDp, false)
}

fun loadImage(context: Context, url: String?, onSuccess: (Bitmap) -> Unit, type: LocationType) {
    Log.d("TAG", "picassdaon loadImage: $url")
    val bit = R.drawable.ic_bakti.drawableRes(context)?.asBitmap()!!
    val target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            Log.d(
                "TAG",
                "picassdaon BitmapLoaded: ${bitmap?.byteCount} ${bitmap?.width} ${bitmap?.height} $url"
            )
            onSuccess(bitmap!!)
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            Log.d("TAG", "picassda onBitmapFailed: ${e?.localizedMessage}")
            onSuccess(bit)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
    }
    when (type) {
        LocationType.Note, LocationType.Technician -> loadImage(
            url,
            onSuccess,
            R.drawable.ic_person.drawableRes(context)!!,
            target
        )

        LocationType.TtMapAll -> loadImage(
            null,
            onSuccess,
            R.drawable.ic_alarm_high_quality.drawableRes(context)!!,
            target
        )

        LocationType.Site, LocationType.TtSiteAll -> loadImage(
            url,
            onSuccess,
            R.drawable.ic_bakti.drawableRes(context)!!,
            target
        )
    }
}

fun loadImage(url: String?, onSuccess: (Bitmap) -> Unit, default: Drawable, target: Target) {
    Log.d("TAG", "picassdaon loadImage inner: $url")
    if (url.isNullOrEmpty()) {
        onSuccess(default.asBitmap()!!)
        return
    }

    Picasso.get()
        .load(url)
        .placeholder(default)
        .error(default)
        .into(target)
}

val Context.isNetworkConnected: Boolean
    get() {
        val conMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = conMgr.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

fun openPdfWithDefaultViewer(context: Context, pdfUri: Uri?) {
    pdfUri?.let {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(pdfUri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_SHORT).show()
        }
    }
}

fun downloadPdf(url: String?): Uri? {
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    return try {
        val inputStream = URL(url).openStream()
        val pdfFile = createPdfFile() // Create a temporary PDF file
        saveStreamToFile(inputStream, pdfFile) // Save the downloaded data to the file
        Uri.fromFile(pdfFile) // Convert the file to a URI
    } catch (_: Exception) {
        null
    }
}

private fun createPdfFile(): File {
    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    dir.mkdirs()
    return File(dir, "document.pdf")
}

private fun saveStreamToFile(inputStream: java.io.InputStream, file: File) {
    val outputStream = FileOutputStream(file)
    val buffer = ByteArray(4096)
    var bytesRead: Int
    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        outputStream.write(buffer, 0, bytesRead)
    }
    outputStream.flush()
    outputStream.close()
    inputStream.close()
}

inline infix fun Boolean?.ifTrue(action: () -> Unit): Boolean? {
    if (this == true) action()
    return this == true
}

inline infix fun Boolean?.ifNot(action: () -> Unit) {
    if (this?.not() == true) action()
}

inline fun String?.isNotNullOrEmpty(): Boolean {
    return this.isNullOrEmpty().not()
}

inline fun Boolean?.ifFalse(action: () -> Unit): Boolean? {
    if (this == false) action()
    return this == false
}

fun toFormattedNumber(number: Int): String {
    if (number <= 1100) {
        return number.toString()
    }
    return "${(number / 1000)}K"
}

fun AppCompatActivity.getPermissionName(permissionString: String): String? {
    return try {
        val packageManager = applicationContext.packageManager
        val permissionInfo = packageManager.getPermissionInfo(permissionString, 0)
        val permissionName = permissionInfo.loadLabel(packageManager).toString()
        permissionName
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}

fun BaseActivity<*, *>.trimAllEditText(root: View = binding.root) {
    findViewsByType(
        root,
        android.widget.EditText::class.java
    ).forEach { it.setText(it.text.trim()) }
}

fun BaseFragment<*, *>.trimAllEditText(root: View = binding.root) {
    findViewsByType(
        root,
        android.widget.EditText::class.java
    ).forEach { it.setText(it.text.trim()) }
}

fun <T : View> findViewsByType(
    rootView: View?,
    viewType: Class<T>,
    exclude: ArrayList<Int> = arrayListOf()
): ArrayList<T> {
    val views = arrayListOf<T>()
    try {
        if (rootView is ViewGroup) {
            for (i in 0 until rootView.childCount) {
                val child: View = rootView.getChildAt(i)
                views.addAll(findViewsByType(child, viewType, exclude))
            }
        } else if (viewType.isInstance(rootView)) {
            val typedView = viewType.cast(rootView)
            if (exclude.none { typedView?.id == it }) {
                views.add(typedView)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        return views
    }
}

fun isImageUrl(url: String): Boolean {
    val regex = Regex("\\.(${imageExtensions.joinToString("|")})$", RegexOption.IGNORE_CASE)
    return regex.containsMatchIn(url)
}

fun urlTypeOf(url: String?): String {
    if (url.isNullOrEmpty()) return ""
    val isImage = isImageUrl(url)
    return if (isImage) {
        "image";
    } else {
        "document";
    }
}

fun toFormattedDistance(totalMeters: Float): String {
    val formatted = (totalMeters / 1000)
//    val kiloMeters = (totalMeters / 1000).toInt()
//    val meters = (totalMeters % 1000).toInt()
//    var formatted = "$meters m"
//    if (kiloMeters > 0) {
//        formatted = "$kiloMeters km $formatted"
//    }
    return "$formatted km"
}

fun Context.isDeviceOnline(): Boolean {
    val connManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connManager.getNetworkCapabilities(connManager.activeNetwork)
        networkCapabilities != null
    } else {
        val activeNetwork = connManager.activeNetworkInfo
        activeNetwork?.isConnectedOrConnecting == true && activeNetwork.isAvailable
    }
}

fun calculateIntervalToClosestSchedules(
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int
): LocationScheduleInterval {
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val minuteOfDay = calendar.get(Calendar.MINUTE)
    val currentTime = hourOfDay * 60 + minuteOfDay
    val midnight = 24 * 60
    val scheduleTimes = arrayOf(startHour * 60 + startMinute, endHour * 60 + endMinute)
    val (nextCommand, interval) = when {
        currentTime < scheduleTimes.min() -> LocationScheduleCommand.START to scheduleTimes.min() - currentTime
        currentTime < scheduleTimes.max() -> LocationScheduleCommand.STOP to scheduleTimes.max() - currentTime
        else -> LocationScheduleCommand.START to midnight - currentTime + scheduleTimes.min()
    }

    val (intervalHours, intervalMinutes) = interval / 60 to interval % 60
    Timber.d("startAlarmManager: $intervalHours hours, $intervalMinutes minutes, next command $nextCommand")
    return LocationScheduleInterval(interval = interval, nextCommand = nextCommand)
}

@RequiresApi(Build.VERSION_CODES.M)
fun BaseActivity<*, *>.startLocationServiceScheduler() {
    val calendar = Calendar.getInstance()
    var (interval, nextCommand) = calculateIntervalToClosestSchedules(
        startHour = START_HOUR_LOCATION_SCHEDULE,
        startMinute = START_MINUTE_LOCATION_SCHEDULE,
        endHour = END_HOUR_LOCATION_SCHEDULE,
        endMinute = END_MINUTE_LOCATION_SCHEDULE
    )
    val itIsWeekend = isTodayIsWeekend()
    itIsWeekend.log("itisWeekend")
    if (itIsWeekend) nextCommand = LocationScheduleCommand.START
    preferences.nextLocationScheduleCommand.value = nextCommand
    val alarmIntent = Intent(this, LocationServiceStarterScheduleReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        134,
        alarmIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    calendar.add(Calendar.MINUTE, interval)
    val manager = getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
    manager[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = pendingIntent
    val actionType = when (preferences.nextLocationScheduleCommand.value) {
        LocationScheduleCommand.START -> LocationService.ACTION_STOP
        LocationScheduleCommand.STOP -> LocationService.ACTION_START
        null -> LocationService.ACTION_START
    }
    Intent(applicationContext, LocationService::class.java).apply {
        action = actionType
        startService(this)
    }
}

fun isTodayIsWeekend(): Boolean {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
}

fun BaseActivity<*, *>.stopLocationServiceScheduler() {
    val alarmIntent = Intent(this, LocationServiceStarterScheduleReceiver::class.java)
    alarmIntent.action = LocationService.ACTION_STOP
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        ALARM_REQUEST_CODE,
        alarmIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val manager = getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
    manager.cancel(pendingIntent)
    //close existing/current notifications
    val notificationManager =
        applicationContext.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(1)
    Intent(applicationContext, LocationService::class.java).apply {
        action = LocationService.ACTION_STOP

        startService(this)
    }
}

fun constructLastLocation(location: Location): LastLocation {
    return LastLocation(
        latitude = location.latitude,
        longitude = location.longitude,
        lastUpdate = Calendar.getInstance().timeInMillis,
    )
}

enum class EllipsisPosition {
    START,
    MIDDLE,
    END
}

fun String.ellipsize(
    maxLength: Int,
    ellipsisPosition: EllipsisPosition = EllipsisPosition.END
): String {
    return when (ellipsisPosition) {
        EllipsisPosition.START -> {
            if (this.length > maxLength) {
                "...${this.substring(this.length - maxLength + 3)}"
            } else {
                this
            }
        }

        EllipsisPosition.MIDDLE -> {
            if (this.length > maxLength) {
                val halfLength = maxLength / 2
                val firstPart = this.substring(0, halfLength)
                val secondPart = this.substring(this.length - halfLength, this.length)
                "...$firstPart$secondPart..."
            } else {
                this
            }
        }

        else -> { // EllipsisPosition.END
            if (this.length > maxLength) {
                "${this.substring(0, maxLength - 3)}..."
            } else {
                this
            }
        }
    }
}

fun <E> MutableList<E>.addNotNull(item: E?) {
    item?.let { this.add(it) }
}

fun <E> MutableList<E>.addAllNotNull(items: Collection<E?>) {
    items.forEach { addNotNull(it) }
}

fun <E> MutableList<E>.addAllNotNull(vararg elements: E) {
    elements.asList().forEach { addNotNull(it) }
}



