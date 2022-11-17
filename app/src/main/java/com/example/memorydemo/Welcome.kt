package com.example.memorydemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorydemo.databinding.ActivityWelcomeBinding
import kotlinx.android.synthetic.main.activity_welcome.*

class Welcome : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bt_signIn.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        bt_signUp.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}