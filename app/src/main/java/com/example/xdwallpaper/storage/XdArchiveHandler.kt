package com.example.xdwallpaper.storage

import android.content.Context
import android.net.Uri
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object XdArchiveHandler {
    private const val MAX_FILE_SIZE_BYTES = 100 * 1024 * 1024 // 100 МБ

    /**
     * Извлекает video.mp4 из .xd архива во внутреннее хранилище
     */
    fun extractXdPackage(context: Context, uri: Uri, destFileName: String): File? {
        val destFile = File(context.filesDir, destFileName)
        var totalBytesRead = 0L

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                ZipInputStream(BufferedInputStream(inputStream)).use { zis ->
                    var entry: ZipEntry? = zis.nextEntry
                    var foundVideo = false

                    while (entry != null) {
                        if (entry.name.endsWith(".mp4", ignoreCase = true)) {
                            FileOutputStream(destFile).use { fos ->
                                val buffer = ByteArray(8192)
                                var len: Int
                                while (zis.read(buffer).also { len = it } > 0) {
                                    totalBytesRead += len
                                    if (totalBytesRead > MAX_FILE_SIZE_BYTES) {
                                        destFile.delete()
                                        throw IOException("Файл превышает лимит 100 МБ")
                                    }
                                    fos.write(buffer, 0, len)
                                }
                            }
                            foundVideo = true
                            break
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                    if (foundVideo) return destFile
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Создает файл .xd из стандартного MP4
     */
    fun packMp4ToXd(sourceMp4: File, outputXd: File, title: String) {
        val manifestJson = """{"title": "$title", "version": "1.0", "loop": true}"""
        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputXd))).use { zos ->
            // 1. Метаданные
            zos.putNextEntry(ZipEntry("manifest.json"))
            zos.write(manifestJson.toByteArray())
            zos.closeEntry()

            // 2. Видеопоток
            zos.putNextEntry(ZipEntry("video.mp4"))
            sourceMp4.inputStream().use { it.copyTo(zos) }
            zos.closeEntry()
        }
    }
}
