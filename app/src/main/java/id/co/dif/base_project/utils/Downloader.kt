package id.co.dif.base_project.utils

interface Downloader {
    fun downloadFile(url: String, title: String, mimeType: String): Long
}