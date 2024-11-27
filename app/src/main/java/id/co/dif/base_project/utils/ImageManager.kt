package id.co.dif.base_project.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.util.Base64
import android.util.Log
import id.co.dif.base_project.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder

//class ImageManager(val context: Context) {
//    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//
//    companion object {
//        private val imageCache: HashMap<String, Bitmap> = hashMapOf()
//        fun init(context: Context) {
//            loadImagesFromCacheDirectory(context)
//        }
//
//        private fun loadImagesFromCacheDirectory(context: Context) {
//            val cacheDir = File(context.cacheDir, "image")
//            if (cacheDir.exists()) {
//                val cacheFiles = cacheDir.listFiles()
//                for (cacheFile in cacheFiles) {
//                    val bitmap = BitmapFactory.decodeFile(cacheFile.path)
//                    if (bitmap != null) {
//                        imageCache[cacheFile.name] = bitmap
//                    }
//                }
//            }
//        }
//    }
//
//    fun loadImage(url: String?, onLoaded: (Bitmap?) -> Unit = {}): Bitmap? {
//        if (url.isNullOrEmpty()) {
//            onLoaded(null)
//            return null
//        }
//        val image: Bitmap? = imageCache[url]
//        scope.launch {
//            val freshImage = downloadImage(url)
//            freshImage?.let {
//                imageCache[url] = it
//                onLoaded(it)
//                saveImageToCacheDirectory(url, it) // Save to cache directory
//            }
//        }
//        image?.let { onLoaded(it) }
//        return image
//    }
//
//    private suspend fun downloadImage(url: String): Bitmap? {
//        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//        StrictMode.setThreadPolicy(policy)
//        return try {
//            val inputStream = URL(url).openStream()
//            BitmapFactory.decodeStream(inputStream)
//        } catch (_: Exception) {
//            R.drawable.ic_person.drawableRes(context)?.asBitmap()
//        }
//    }
//
//    private fun saveImageToCacheDirectory(url: String, bitmap: Bitmap) {
//        val cacheDir = File(context.cacheDir, "image")
//        if (!cacheDir.exists()) {
//            cacheDir.mkdirs()
//        }
//        val fileName = getCacheFileName(url)
//        Log.d("TAG", "saveImageToCacheDirectory-cache: $fileName")
//        val cacheFile = File(cacheDir, fileName)
//        val outputStream = FileOutputStream(cacheFile)
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//        outputStream.close()
//    }
//
//}