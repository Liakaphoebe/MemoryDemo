package com.example.memorydemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memorydemo.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        tv_signup.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

            finish()
        }

        bt_signUp.setOnClickListener{
            val email = et_email.text.toString()
            val pass = et_password.text.toString()
            val confirm = et_confirm.text.toString()

            if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                et_email.error = "Invalid Email"
            }  else if(pass.isEmpty()){
                et_password.error = "Passsword is empty"
            } else if(pass.length < 6){
                et_password.error = "Passsword must be greater than 6"
            } else if(confirm.isEmpty() || pass != confirm){
                et_confirm.error = "Passwords don't match"
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}