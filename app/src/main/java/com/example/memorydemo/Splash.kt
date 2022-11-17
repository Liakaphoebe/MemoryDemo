package com.example.memorydemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule


class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timer("SettingUp", false).schedule(3000) {
            val intent = Intent(this@Splash, Login::class.java)
            startActivity(intent)

            finish()
        }
    }

}