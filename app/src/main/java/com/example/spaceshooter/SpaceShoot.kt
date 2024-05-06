import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.spaceshooter.EnemySpaceship

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Handler
import android.view.MotionEvent
import android.view.View

import com.example.spaceshooter.Explosion
import com.example.spaceshooter.GameOver
import com.example.spaceshooter.Meteor
import com.example.spaceshooter.OurSpaceship
import com.example.spaceshooter.PowerUp
import com.example.spaceshooter.R
import com.example.spaceshooter.Shot
import java.util.Random

class SpaceShoot(private val appContext: Context) : View(appContext) {

    // Properties
    var background: Bitmap
    var lifeImage: Bitmap
    var gameHandler: Handler
    var UPDATE_MILLIS: Long = 30
    var points: Int = 0
    var life: Int = 3
    private var gameOver = false
    var highestScore: Int = 0
    var scorePaint: Paint
    var TEXT_SIZE: Int = 80
    var paused: Boolean = false
    var ourSpaceship: OurSpaceship
    var enemySpaceship: EnemySpaceship
    var random: Random = Random()
    var enemyShots: ArrayList<Shot> = ArrayList()
    var ourShots: ArrayList<Shot?> = ArrayList()
    var enemyExplosion: Boolean = false
    var explosion: Explosion? = null
    var explosions: ArrayList<Explosion> = ArrayList()
    var enemyShotAction: Boolean = false
    var meteors: ArrayList<Meteor> = ArrayList()
    var powerUps: ArrayList<PowerUp> = ArrayList() // Add powerUps list

    // Initialize powerUps list
    var mediaPlayer: MediaPlayer? =
        MediaPlayer.create(appContext, R.raw.sound01) // Initialize media player

    val runnable: Runnable = Runnable { this.invalidate() }

    init {
        // Get screen size
        val display = (appContext as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y

        // Initialize objects
        ourSpaceship = OurSpaceship(appContext)
        enemySpaceship = EnemySpaceship(appContext)
        background = BitmapFactory.decodeResource(
            appContext.resources,
            R.drawable.background
        )
        lifeImage = BitmapFactory.decodeResource(
            appContext.resources,
            R.drawable.playerlife
        )

        // Initialize the handler object
        gameHandler = Handler()

        // Initialize score paint
        scorePaint = Paint()
        scorePaint.color = Color.RED
        scorePaint.textSize = TEXT_SIZE.toFloat()
        scorePaint.textAlign = Paint.Align.LEFT

        // Add meteors every 3 seconds
        gameHandler.postDelayed(object : Runnable {
            override fun run() {
                addMeteor()
                gameHandler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    // Drawing method
    // Drawing method
    // Drawing method
    override fun onDraw(canvas: Canvas) {
        // Draw the background image
        canvas.drawBitmap(background, 0f, 0f, null)

        // Ensure meteors list is not empty before iterating
        if (meteors.isNotEmpty()) {
            val meteorsToRemove = ArrayList<Meteor>() // List to hold meteors to be removed
            for (meteor in meteors) {
                meteor.draw(canvas)
                meteor.update()

                if (Rect.intersects(meteor.rect, ourSpaceship.rect)) {
                    life--
                    meteorsToRemove.add(meteor) // Add the meteor to the list of meteors to be removed
                    explosion = Explosion(appContext, ourSpaceship.ox, ourSpaceship.oy)
                    explosions.add(explosion!!)
                }
            }

            // Remove meteors that need to be removed
            meteors.removeAll(meteorsToRemove)
        }

        // Draw the points text
        canvas.drawText("Points: $points", 20f, 80f, scorePaint)

        if (life == 0 && !gameOver) {
            gameOver = true
            paused = true
            val intent = Intent(appContext, GameOver::class.java)
            intent.putExtra("points", points)
            if (points > highestScore) {
                highestScore = points
                saveHighestScore(highestScore)
            }
            appContext.startActivity(intent)
            (appContext as Activity).finish()
            return
        }

        enemySpaceship.ex += enemySpaceship.enemyVelocity
        if (enemySpaceship.ex + enemySpaceship.enemySpaceshipWidth >= screenWidth) {
            enemySpaceship.enemyVelocity *= -1
        }
        if (enemySpaceship.ex <= 0) {
            enemySpaceship.enemyVelocity *= -1
        }

        if (!enemyShotAction && enemySpaceship.ex >= 200 + random.nextInt(400)) {
            val enemyShot = Shot(
                appContext,
                enemySpaceship.ex + enemySpaceship.enemySpaceshipWidth / 2,
                enemySpaceship.ey
            )
            enemyShots.add(enemyShot)
            enemyShotAction = true
        }

        if (!enemyExplosion) {
            // Adjust the method call to draw the enemy spaceship
            enemySpaceship.draw(canvas)
        }

        if (ourSpaceship.isAlive) {
            if (ourSpaceship.ox > screenWidth - ourSpaceship.ourSpaceshipWidth) {
                ourSpaceship.ox = screenWidth - ourSpaceship.ourSpaceshipWidth
            } else if (ourSpaceship.ox < 0) {
                ourSpaceship.ox = 0
            }
            // Adjust the method call to draw our spaceship
            ourSpaceship.draw(canvas)
        }

        // Check collision between enemy shots and our spaceship
        for (i in enemyShots.indices.reversed()) {
            enemyShots[i].shy += 10
            canvas.drawBitmap(
                enemyShots[i].shot,
                enemyShots[i].shx.toFloat(),
                enemyShots[i].shy.toFloat(),
                null
            )
            if ((enemyShots[i].shx >= ourSpaceship.ox)
                && (enemyShots[i].shx <= ourSpaceship.ox + ourSpaceship.ourSpaceshipWidth)
                && (enemyShots[i].shy >= ourSpaceship.oy)
                && (enemyShots[i].shy <= screenHeight)
            ) {
                life--
                enemyShots.removeAt(i)
                explosion = Explosion(appContext, ourSpaceship.ox, ourSpaceship.oy)
                explosions.add(explosion!!)
            } else if (enemyShots[i].shy >= screenHeight) {
                enemyShots.removeAt(i)
            }
            if (enemyShots.isEmpty()) {
                enemyShotAction = false
            }
        }

        // Check collision between our shots and enemy spaceship
        for (i in ourShots.indices.reversed()) {
            val shot = ourShots[i]
            if (shot != null && shot.shy != null) {
                shot.shy -= 15
                canvas.drawBitmap(
                    shot.shot,
                    shot.shx.toFloat(),
                    shot.shy.toFloat(),
                    null
                )
                if ((shot.shx >= enemySpaceship.ex)
                    && (shot.shx <= enemySpaceship.ex + enemySpaceship.enemySpaceshipWidth)
                    && (shot.shy <= enemySpaceship.enemySpaceshipHeight)
                    && (shot.shy >= enemySpaceship.ey)
                ) {
                    points++
                    ourShots.removeAt(i)
                    explosion = Explosion(appContext, enemySpaceship.ex, enemySpaceship.ey)
                    explosions.add(explosion!!)
                } else if (shot.shy <= 0) {
                    ourShots.removeAt(i)
                }
            }
        }


        // Draw explosions
        for (i in explosions.indices) {
            canvas.drawBitmap(
                explosions[i].getExplosion(explosions[i].explosionFrame)!!,
                explosions[i].eX.toFloat(),
                explosions[i].eY.toFloat(),
                null
            )

            explosions[i].explosionFrame++
            if (explosions[i].explosionFrame > 8) {
                explosions.removeAt(i)
            }
        }

        // Check collision between our shots and meteors
        val shotsToRemove = ArrayList<Int>()
        val meteorsToRemove = ArrayList<Int>()
        for (i in ourShots.indices) {
            val shot = ourShots[i]
            if (shot != null) {
                for (j in meteors.indices) {
                    val meteor = meteors[j]
                    if (Rect.intersects(shot.rect, meteor.rect)) {
                        points++ // Increase points
                        shotsToRemove.add(i) // Add index of shot to be removed
                        meteorsToRemove.add(j) // Add index of meteor to be removed
                        explosion = Explosion(appContext, meteor.x, meteor.y)
                        explosions.add(explosion!!) // Add explosion effect
                        break // Break inner loop after one meteor is destroyed by a shot
                    }
                }
            }
        }

        // Remove shots and meteors
        for (index in shotsToRemove) {
            if (index >= 0 && index < ourShots.size) {
                ourShots.removeAt(index)
            }
        }
        for (index in meteorsToRemove) {
            if (index >= 0 && index < meteors.size) {
                val destroyedMeteor = meteors.removeAt(index)
                // Handle power-up logic here if needed
            }
        }

        // Post delayed runnable
        val handler = Handler()
        if (!paused) handler.postDelayed(runnable, UPDATE_MILLIS)
    }

    // Touch event handling method
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt() // Add this line to get touch Y position
        if (event.action == MotionEvent.ACTION_UP) {
            if (ourShots.size < 3) {
                // Update our spaceship with touch X and Y positions
                ourSpaceship.update(touchX, touchY, ourShots)
                playShootSound() // Call playShootSound method here
            }
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            ourSpaceship.ox = touchX
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            ourSpaceship.ox = touchX
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        // Handle click event if necessary
        return true
    }

    // Method to play shoot sound
    private fun playShootSound() {
        if (mediaPlayer != null) {
            mediaPlayer!!.start() // Start playing the shoot sound
        }
    }

    // Method to save highest score
    private fun saveHighestScore(score: Int) {
        val sharedPreferences = appContext.getSharedPreferences("SpaceShooterPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("highestScore", score)
        editor.apply()
    }

    // Method to load highest score
    private fun loadHighestScore(): Int {
        val sharedPreferences =
            appContext.getSharedPreferences("SpaceShooterPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("highestScore", 0) // Default value is 0 if not found
    }

    // Method to add meteor
    fun addMeteor() {
        val x =
            ourSpaceship.ox + random.nextInt(ourSpaceship.ourSpaceshipWidth) // Start from behind of our spaceship
        val y = -100 // Start from above the screen
        val velocity = 5 + random.nextInt(3) // Set velocity
        val meteor = Meteor(appContext, x, y, velocity) // Create meteor
        meteors.add(meteor) // Add meteor to ArrayList
    }


    // Companion object to hold screen dimensions
    companion object {
        var screenWidth: Int = 0
        var screenHeight: Int = 0
    }

    // Avoid accidental override by renaming the method
    fun getSpaceShootHandler(): Handler? {
        return gameHandler
    }
}
