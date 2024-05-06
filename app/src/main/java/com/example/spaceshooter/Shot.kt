package com.example.spaceshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.MediaPlayer

class Shot(context: Context, @JvmField var shx: Int, @JvmField var shy: Int) {
    // Initialize the shot bitmap
    @JvmField
    val shot: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.laser01)

    // Load shooting sound effect
    private val shootSound: MediaPlayer? = MediaPlayer.create(context, R.raw.sound01)

    val rect: Rect
        get() = Rect(shx, shy, shx + shot.width, shy + shot.height)

    fun playShootSound() {
        shootSound?.start()
    }

    // Method to release resources when the shot is destroyed
    fun release() {
        shootSound?.release()
    }
}
