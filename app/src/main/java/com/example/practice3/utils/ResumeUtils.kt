package com.example.practice3.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object ResumeUtils {

    fun downloadAndOpenResume(context: Context, resumeUrl: String) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val uri = Uri.parse(resumeUrl)
            val fileName = getFileNameFromUrl(resumeUrl)

            val request = DownloadManager.Request(uri)
                .setTitle("Скачивание резюме")
                .setDescription("Скачивание файла: $fileName")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadId = downloadManager.enqueue(request)

            // Сохраняем downloadId для отслеживания
            saveDownloadId(context, downloadId, fileName)

            Toast.makeText(context, "Начато скачивание резюме", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка при скачивании: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDownloadedResume(context: Context, fileName: String) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val mimeType = getMimeType(file.absolutePath)

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }

                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Файл не найден", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка при открытии файла: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileNameFromUrl(url: String): String {
        return try {
            val uri = Uri.parse(url)
            val path = uri.lastPathSegment ?: "resume.pdf"
            if (path.contains(".")) {
                path
            } else {
                "resume_$path.pdf" // Default to PDF if no extension
            }
        } catch (e: Exception) {
            "resume_${System.currentTimeMillis()}.pdf"
        }
    }

    private fun getMimeType(filePath: String): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/pdf"
    }

    private fun saveDownloadId(context: Context, downloadId: Long, fileName: String) {
        val prefs = context.getSharedPreferences("resume_downloads", Context.MODE_PRIVATE)
        prefs.edit().putLong(fileName, downloadId).apply()
    }

    fun getDownloadId(context: Context, fileName: String): Long {
        val prefs = context.getSharedPreferences("resume_downloads", Context.MODE_PRIVATE)
        return prefs.getLong(fileName, -1)
    }
}

