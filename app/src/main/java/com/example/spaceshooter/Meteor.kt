package com.example.spaceshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import java.util.Random

class Meteor(context: Context, @JvmField var x: Int, @JvmField var y: Int, var velocity: Int) {
    var meteor: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.meteor01)
    var random: Random = Random()

    fun resetMeteor() {
        this.x = random.nextInt(SpaceShoot.screenWidth - meteor.width)
        this.y = -meteor.height
        this.velocity = 5 + random.nextInt(3)
    }

    fun update() {
        this.y += velocity
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(meteor, x.toFloat(), y.toFloat(), null)
    }

    val isOffScreen: Boolean
        get() = y > SpaceShoot.screenHeight

    val rect: Rect
        get() = Rect(x, y, x + meteor.width, y + meteor.height)

    // Adjust the method to check if a power-up should drop
    fun containsPowerUp(): Boolean {
        val randomNumber = random.nextInt(100)
        return randomNumber < POWER_UP_DROP_PROBABILITY
    }

    companion object {
        // Adjust the probability of a power-up dropping
        private const val POWER_UP_DROP_PROBABILITY = 100
    }
}
