package com.example.spaceshooter

import com.example.spaceshooter.MainActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class StartUp : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startup)
    }

    fun startGame(v: View?) {
        val intent = Intent(this,MainActivity::class.java)
    startActivity(intent)

    }
}
