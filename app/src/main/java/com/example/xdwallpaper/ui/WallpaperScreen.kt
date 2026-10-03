package com.example.xdwallpaper.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xdwallpaper.service.VideoLiveWallpaperService
import com.example.xdwallpaper.storage.WallpaperManagerHelper
import com.example.xdwallpaper.storage.XdArchiveHandler
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperScreen() {
    val context = LocalContext.current
    var currentFileInfo by remember {
        mutableStateOf(WallpaperManagerHelper.getActiveWallpaperPath(context)?.let { File(it).name } ?: "Файл не выбран")
    }
    var statusMessage by remember { mutableStateOf("Поддерживаются файлы .mp4 и .xd (до 100 МБ)") }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        val fileName = getFileName(context, uri)
        statusMessage = "Обработка файла..."

        val savedFile: File? = if (fileName.endsWith(".xd", ignoreCase = true)) {
            XdArchiveHandler.extractXdPackage(context, uri, "active_extracted.mp4")
        } else {
            WallpaperManagerHelper.copyMp4ToInternal(context, uri, "active_imported.mp4")
        }

        if (savedFile != null && savedFile.exists()) {
            WallpaperManagerHelper.setActiveWallpaperPath(context, savedFile.absolutePath)
            currentFileInfo = fileName
            statusMessage = "Файл успешно загружен (${savedFile.length() / (1024 * 1024)} МБ)"
        } else {
            statusMessage = "Ошибка: файл поврежден или превышает лимит 100 МБ"
            Toast.makeText(context, statusMessage, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("XD Wallpaper Engine") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoFile,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = currentFileInfo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = statusMessage,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // Запрос системного селектора файлов
                        filePicker.launch(arrayOf("video/mp4", "application/octet-stream", "*/*"))
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Выбрать .MP4 или .XD")
                }

                Button(
                    onClick = {
                        val currentPath = WallpaperManagerHelper.getActiveWallpaperPath(context)
                        if (currentPath == null) {
                            Toast.makeText(context, "Сначала выберите видео", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        applyWallpaper(context)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Установить как обои")
                }
            }
        }
    }
}

private fun applyWallpaper(context: Context) {
    try {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(context, VideoLiveWallpaperService::class.java)
            )
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Не удалось открыть меню обоев", Toast.LENGTH_SHORT).show()
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    var name = "video.mp4"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}
