package com.example.memorydemo

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.memorydemo.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_welcome.*


class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tv_signup.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        val builder = AlertDialog.Builder(this)
            .setView(R.layout.progress_dialog)
        dialog = builder.create()

        bt_signIn.setOnClickListener{
            val email = et_email.text.toString()
            val pass = et_password.text.toString()

            if(email.isEmpty()){
                et_email.error = "Email is empty"
            } else if(pass.isEmpty()){
                et_password.error = "Passsword is empty"
            } else {
                setDialog(true)
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    setDialog(false)
                    if (it.isSuccessful){
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, Home::class.java)
                        intent.putExtra("email",email)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Failed to login user", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }
}