package com.example.spaceshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler

class OurSpaceship(private val context: Context) {
    @JvmField
    var ourSpaceship: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.myship3)
    @JvmField
    var ox: Int = 0
    @JvmField
    var oy: Int = 0
    @JvmField
    var isAlive: Boolean = true
    var ourVelocity: Int = 0
    var doubleShotEnabled: Boolean = false

    init {
        resetOurSpaceship()
    }

    val ourSpaceshipWidth: Int
        get() = ourSpaceship.width

    val ourSpaceshipHeight: Int
        get() = ourSpaceship.height

    val rect: Rect
        get() = Rect(ox, oy, ox + ourSpaceship.width, oy + ourSpaceship.height)

    private fun resetOurSpaceship() {
        ox = SpaceShoot.screenWidth / 2 - ourSpaceship.width / 2
        oy = SpaceShoot.screenHeight - ourSpaceship.height - 100
        ourVelocity = 10 // Adjust velocity as needed
    }

    fun update(x: Int, y: Int, ourShots: ArrayList<Shot?>) {
        if (doubleShotEnabled) {
            // Shoot double shots
            val shot1 = Shot(context, ox + ourSpaceship.width / 4, oy)
            val shot2 = Shot(context, ox + ourSpaceship.width * 3 / 4, oy)
            ourShots.add(shot1)
            ourShots.add(shot2)
        } else {
            // Shoot single shot
            val shot = Shot(context, ox + ourSpaceship.width / 2, oy)
            ourShots.add(shot)
        }
    }

    fun handlePowerUp() {
        // Set doubleShotEnabled to true
        doubleShotEnabled = true

        // Start a timer to disable the double shot after 10 seconds
        Handler().postDelayed({ doubleShotEnabled = false }, 10000)
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(ourSpaceship, ox.toFloat(), oy.toFloat(), null)
    }
}