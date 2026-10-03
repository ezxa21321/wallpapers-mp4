package com.example.xdwallpaper.service

import android.media.MediaPlayer
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.example.xdwallpaper.storage.WallpaperManagerHelper
import java.io.File

class VideoLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = VideoEngine()

    inner class VideoEngine : Engine() {
        private var mediaPlayer: MediaPlayer? = null

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            startPlayback(holder)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            releasePlayer()
        }

        private fun startPlayback(holder: SurfaceHolder) {
            val videoPath = WallpaperManagerHelper.getActiveWallpaperPath(applicationContext) ?: return
            val file = File(videoPath)
            if (!file.exists()) return

            releasePlayer()

            mediaPlayer = MediaPlayer().apply {
                setSurface(holder.surface)
                setDataSource(file.absolutePath)
                isLooping = true
                setVolume(0f, 0f) // Звук по умолчанию выключен для экономии заряда
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                }
            }
        }

        private fun releasePlayer() {
            mediaPlayer?.run {
                stop()
                release()
            }
            mediaPlayer = null
        }
    }
}
