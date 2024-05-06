package com.example.spaceshooter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    var tvPoints: TextView? = null
    var tvHighestScoreValue: TextView? = null // TextView to display the highest score

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)
        val points = intent.extras!!.getInt("points")
        tvPoints = findViewById<TextView>(R.id.tvPoints)
        tvPoints?.text = points.toString()

        // Load and display the highest score
        val highestScore = loadHighestScore()
        tvHighestScoreValue = findViewById<TextView>(R.id.tvHighestScoreValue)
        tvHighestScoreValue?.text = highestScore.toString()

        // Check if the current score is higher than the highest score
        if (points > highestScore) {
            // If yes, save the new highest score
            saveHighestScore(points)
            tvHighestScoreValue?.text = points.toString() // Update the display
        }
    }


    // Method to load the highest score from SharedPreferences
    private fun loadHighestScore(): Int {
        val sharedPreferences = getSharedPreferences("SpaceShooterPrefs", MODE_PRIVATE)
        return sharedPreferences.getInt("highestScore", 0) // Default value is 0 if not found
    }

    // Method to save the highest score to SharedPreferences
    private fun saveHighestScore(score: Int) {
        val sharedPreferences = getSharedPreferences("SpaceShooterPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("highestScore", score)
        editor.apply()
    }

    fun restart(v: View?) {
        val intent = Intent(this@GameOver, StartUp::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(v: View?) {
        finish()
    }
}
