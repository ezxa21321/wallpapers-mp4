package com.example.xdwallpaper.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.xdwallpaper.service.VideoLiveWallpaperService
import com.example.xdwallpaper.storage.WallpaperManagerHelper

@Composable
fun WallpaperScreen() {
    val ctx = LocalContext.current
    var status by remember { mutableStateOf("Выберите видео (.mp4) или архив (.xd)") }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val isXd = uri.toString().endsWith(".xd", true)
            val file = WallpaperManagerHelper.saveFile(ctx, uri, isXd)
            if (file != null) {
                WallpaperManagerHelper.setActiveWallpaperPath(ctx, file.absolutePath)
                status = "Обои установлены!"
                Toast.makeText(ctx, "Обои применены!", Toast.LENGTH_SHORT).show()

                // Автоматически открываем системное окно установки при необходимости
                try {
                    val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                        putExtra(
                            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            ComponentName(ctx, VideoLiveWallpaperService::class.java)
                        )
                    }
                    ctx.startActivity(intent)
                } catch (e: Exception) {
                    // Если обои уже стоят на рабочем столе, видео обновится само через Broadcast
                }
            } else {
                status = "Ошибка: файл поврежден или больше 100 МБ"
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(status)
        Spacer(Modifier.height(20.dp))
        Button(onClick = { picker.launch(arrayOf("*/*")) }) {
            Text("Выбрать обои")
        }
    }
}
