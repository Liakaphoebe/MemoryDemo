package com.example.memorydemo

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.memorydemo.databinding.ActivityHomeBinding
import com.example.memorydemo.model.Level
import com.example.memorydemo.model.Session
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat


class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    var email: String = "korir@gmail.com"
    var bestTime:String = "0:0"
    var attempt: Int = 1
    var userKey: String = ""
    var key: String = ""
    var allLevels: ArrayList<Level> = ArrayList()
    var allSessions: ArrayList<Session> = ArrayList()
    var dialog: Dialog? = null
    var firebaseDatabase: FirebaseDatabase? = null
    private var levelReference: DatabaseReference? = null
    private var sessionReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        levelReference = firebaseDatabase!!.getReference("MemoryGameLevel")
        sessionReference = firebaseDatabase!!.getReference("MemoryGameSession")

        email = intent.getStringExtra("email").toString()

        memory_game.setOnClickListener{
            val builder = AlertDialog.Builder(this)
                .setView(R.layout.progress_dialog_setting)
            dialog = builder.create()
            setDialog(true)

            getlevel()
        }

        game_report.setOnClickListener{
            val intent = Intent(this@Home, Report::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }

    private fun getlevel(){
        levelReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val level: Level? = child.getValue(Level::class.java)
                    if(level!!.player == email){
                        userKey = child.key.toString()
                        allLevels.add(level)
                    }
                }

                if(allLevels.size <  1)
                    updateLevel(Level(1,email,"0:0",1))
                else {
                    val level: Level = allLevels[0]
                    bestTime = level.bestTime!!
                    getSessions(level.level!!)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                setDialog(false)
                Log.e("getlevel", error.message)
            }
        })
    }

    private fun updateLevel(level: Level){
        levelReference!!.child(levelReference!!.push().key!!).setValue(level)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    getlevel()
                } else {
                    setDialog(false)
                    Toast.makeText(this, "Failed to update level", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getSessions(level: Int){
        sessionReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allSessions.clear()
                for (child in snapshot.children) {
                    val session: Session? = child.getValue(Session::class.java)
                    if(session!!.player == email && session.level == level){
                        allSessions.add(session)
                    }
                }

                if(allSessions.size < 1){
                    attempt = 1
                    updateSession(Session(email,1,1,1,"0:0"),true)
                } else {
                    attempt = allSessions.size + 1
                    updateSession(Session(email,attempt,1,1,"0:0"),true)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                setDialog(false)
                Log.e("getSession", error.message)
            }
        })
    }

    private fun updateSession(session: Session,next: Boolean){
        if(next)
            key = sessionReference!!.push().key!!

        sessionReference!!.child(key).setValue(session)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if(next){
                        setDialog(false)

                        val intent = Intent(this@Home, MemoryGame::class.java)
                        intent.putExtra("attempts", attempt)
                        intent.putExtra("bestTime", bestTime)
                        intent.putExtra("key", key)
                        resultLauncher.launch(intent)
                    }
                } else {
                    if(next)
                        setDialog(false)

                    Toast.makeText(this, "Failed to update level", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val time = data!!.getStringExtra("time")
            val key = data.getStringExtra("key")


            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val current = dateFormat.parse("2000-01-01 00:$time")!!.time
            val best = dateFormat.parse("2000-01-01 00:$bestTime")!!.time

            if(bestTime == "0:0" || best > current){
                levelReference!!.child(userKey).child("bestTime").setValue(time)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("resultLauncher", "level updated")
                        } else {
                            Log.d("resultLauncher", "level update failed")
                        }
                }
            }

            updateSession(Session(email,attempt,1,1,time),false)
        }
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }
}