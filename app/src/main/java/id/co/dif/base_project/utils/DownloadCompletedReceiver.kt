package id.co.dif.base_project.utils

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity


class DownloadCompletedReceiver : BroadcastReceiver() {
    private lateinit var downloadManager: DownloadManager
    override fun onReceive(context: Context?, intent: Intent?) {
        downloadManager = context?.getSystemService(DownloadManager::class.java)!!

        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            val query = DownloadManager.Query().setFilterById(id)

            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val columnIndexReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val columnIndexStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

                if (columnIndexReason != -1 && columnIndexStatus != -1) {
                    val status = cursor.getInt(columnIndexStatus)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        println("Download with ID $id finished")
                        Toast.makeText(context, "File download is finished and saved to downloads folder", Toast.LENGTH_SHORT).show()
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        val reason = cursor.getInt(columnIndexReason)
                        println("Download with ID $id failed with reason: $reason")
                        Toast.makeText(context, "Download failed!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle the case when the required columns are not found in the cursor
                    println("Download with ID $id: Required columns not found in the cursor.")
                    Toast.makeText(context, "Download status unknown!", Toast.LENGTH_SHORT).show()
                }
            }
            cursor.close()
        }
    }


}