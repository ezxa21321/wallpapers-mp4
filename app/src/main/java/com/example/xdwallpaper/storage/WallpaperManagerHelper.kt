package com.example.xdwallpaper.storage

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object WallpaperManagerHelper {
    private const val PREFS_NAME = "xd_wallpaper_prefs"
    private const val KEY_ACTIVE_PATH = "active_video_path"
    private const val MAX_FILE_SIZE = 100 * 1024 * 1024

    fun getActiveWallpaperPath(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACTIVE_PATH, null)
    }

    fun setActiveWallpaperPath(context: Context, path: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ACTIVE_PATH, path)
            .apply()
    }

    fun copyMp4ToInternal(context: Context, uri: Uri, targetName: String): File? {
        val target = File(context.filesDir, targetName)
        var totalBytes = 0L

        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(target).use { output ->
                    val buffer = ByteArray(8192)
                    var bytes: Int
                    while (input.read(buffer).also { bytes = it } > 0) {
                        totalBytes += bytes
                        if (totalBytes > MAX_FILE_SIZE) {
                            target.delete()
                            return null
                        }
                        output.write(buffer, 0, bytes)
                    }
                }
            }
            target
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
