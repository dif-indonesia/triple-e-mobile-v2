package id.co.dif.base_project.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.net.toUri
import java.util.Calendar

class AndroidDownloader(
    private val context: Context
) : Downloader {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, title: String, mimeType: String): Long {
        val calendar = Calendar.getInstance()
        val time = calendar.timeInMillis.toString()
        val request = DownloadManager.Request(url.toUri())
            .setMimeType(mimeType)

            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(title)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title")
        // Here's the magic part to make it save with the correct extension
        request.allowScanningByMediaScanner()
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "$title.${MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)}"
        )
        Toast.makeText(context, "Downloading file", Toast.LENGTH_SHORT).show()
        return downloadManager.enqueue(request)
    }
}