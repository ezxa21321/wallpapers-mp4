package com.example.xdwallpaper.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.example.xdwallpaper.storage.WallpaperManagerHelper
import java.io.File

class VideoLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = VideoEngine()

    inner class VideoEngine : Engine() {
        private var mp: MediaPlayer? = null
        private var currentHolder: SurfaceHolder? = null

        private val reloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentHolder?.let { startPlayback(it) }
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            val filter = IntentFilter(WallpaperManagerHelper.ACTION_RELOAD_WALLPAPER)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(reloadReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(reloadReceiver, filter)
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            currentHolder = holder
            startPlayback(holder)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) mp?.start() else mp?.pause()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            currentHolder = null
            releasePlayer()
        }

        override fun onDestroy() {
            super.onDestroy()
            try {
                unregisterReceiver(reloadReceiver)
            } catch (e: Exception) {
                // Игнорируем повторное снятие регистрации
            }
            releasePlayer()
        }

        private fun startPlayback(holder: SurfaceHolder) {
            val path = WallpaperManagerHelper.getActiveWallpaperPath(applicationContext) ?: return
            val file = File(path)
            if (!file.exists()) return

            releasePlayer()

            try {
                mp = MediaPlayer().apply {
                    setSurface(holder.surface)
                    setDataSource(file.absolutePath)
                    isLooping = true
                    setVolume(0f, 0f)
                    prepareAsync()
                    setOnPreparedListener { player ->
                        if (isVisible) player.start()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun releasePlayer() {
            mp?.run {
                stop()
                release()
            }
            mp = null
        }
    }
}
