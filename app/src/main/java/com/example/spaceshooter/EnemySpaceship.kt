package com.example.spaceshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.BitmapFactory
import java.util.Random

class EnemySpaceship(var context: Context) {
    var enemySpaceship: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.enemy01)
    var ex: Int = 0
    var ey: Int = 0
    var enemyVelocity: Int = 0
    var random: Random = Random()

    val ENEMY_START_X: Int = 200
    val ENEMY_START_Y: Int = 0
    val ENEMY_MIN_VELOCITY: Int = 14
    val ENEMY_VELOCITY_RANGE: Int = 10

    init {
        resetEnemySpaceship()
    }

    val enemySpaceshipWidth: Int
        get() = enemySpaceship.width

    val enemySpaceshipHeight: Int
        get() = enemySpaceship.height

    private fun resetEnemySpaceship() {
        ex = ENEMY_START_X + random.nextInt(400)
        ey = ENEMY_START_Y
        enemyVelocity = ENEMY_MIN_VELOCITY + random.nextInt(ENEMY_VELOCITY_RANGE)
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(enemySpaceship, ex.toFloat(), ey.toFloat(), null)
    }
}
