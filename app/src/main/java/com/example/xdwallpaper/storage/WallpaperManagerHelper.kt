package com.example.xdwallpaper.storage

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object WallpaperManagerHelper {
    private const val PREFS = "xd_prefs"
    const val ACTION_RELOAD_WALLPAPER = "com.example.xdwallpaper.ACTION_RELOAD_WALLPAPER"

    fun getActiveWallpaperPath(ctx: Context): String? =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("path", null)

    fun setActiveWallpaperPath(ctx: Context, path: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString("path", path)
            .apply()
        
        // Отправляем сигнал сервису обновить видеопоток на лету
        val intent = Intent(ACTION_RELOAD_WALLPAPER).apply {
            setPackage(ctx.packageName)
        }
        ctx.sendBroadcast(intent)
    }

    fun saveFile(ctx: Context, uri: Uri, isXd: Boolean): File? {
        // Уникальное имя исключает конфликт дескрипторов плеера
        val newFile = File(ctx.filesDir, "wp_${System.currentTimeMillis()}.mp4")
        
        return try {
            ctx.contentResolver.openInputStream(uri)?.use { input ->
                if (isXd) {
                    ZipInputStream(input).use { zis ->
                        var entry = zis.nextEntry
                        while (entry != null) {
                            if (entry.name.endsWith(".mp4", ignoreCase = true)) {
                                FileOutputStream(newFile).use { zis.copyTo(it) }
                                cleanOldFiles(ctx, newFile)
                                return newFile
                            }
                            entry = zis.nextEntry
                        }
                    }
                } else {
                    FileOutputStream(newFile).use { input.copyTo(it) }
                    cleanOldFiles(ctx, newFile)
                    return newFile
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun cleanOldFiles(ctx: Context, keepFile: File) {
        ctx.filesDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("wp_") && file.absolutePath != keepFile.absolutePath) {
                file.delete()
            }
        }
    }
}
