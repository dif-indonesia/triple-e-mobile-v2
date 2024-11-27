package id.co.dif.base_project

import com.google.android.gms.maps.model.LatLng
import id.co.dif.base_project.utils.megaBytes

const val LOCATION_UPDATE_INTERVAL = 1000 * 60 * 15L
const val ALARM_REQUEST_CODE = 134
//const val LOCATION_UPDATE_INTERVAL = 1000 * 10L
const val NOTIFICATION_CHANNEL_ID = 1
const val START_HOUR_LOCATION_SCHEDULE = 8
const val START_MINUTE_LOCATION_SCHEDULE = 0
const val END_HOUR_LOCATION_SCHEDULE = 19
const val END_MINUTE_LOCATION_SCHEDULE = 0
const val MAX_IMAGE_SIZE_MEGABYTES = 10L
val imageExtensions = listOf(
    "jpg",
    "jpeg",
    "png",
    "gif",
    "bmp",
    "tiff",
    "tif",
    "webp",
    "svg",
    "ico",
    "jp2",
    "heic",
    "heif"
)
val allowedMimeType = arrayOf(
    "image/jpeg",
    "image/png",
    "image/gif",
    "application/pdf",
    "image/jpg",
    "video/*",
)

val centerPointOfIndonesia = LatLng(-2.548926, 118.0148634)