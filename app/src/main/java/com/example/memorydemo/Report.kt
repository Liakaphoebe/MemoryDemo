package com.example.memorydemo

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.memorydemo.databinding.ActivityReportBinding
import com.example.memorydemo.model.Session
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_report.*

class Report : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    var firebaseDatabase: FirebaseDatabase? = null
    private var sessionReference: DatabaseReference? = null
    var allSessions: ArrayList<Session> = ArrayList<Session>()
    var email: String? = null
    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        sessionReference = firebaseDatabase!!.getReference("MemoryGameSession")

        email = intent.getStringExtra("email").toString()


        val builder = AlertDialog.Builder(this)
            .setView(R.layout.progress_dialog)
        dialog = builder.create()
        setDialog(true)

        getSessions()
    }

    fun getSessions(){
        sessionReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                setDialog(false)
                for (child in snapshot.children) {
                    val session: Session? = child.getValue(Session::class.java)
                    if(session!!.player == email && session.level == 1){
                        allSessions.add(session)
                    }
                }

                setChartData()
            }
            override fun onCancelled(error: DatabaseError) {
                setDialog(false)
                Toast.makeText(this@Report, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                Log.e("getSession", error.message)
            }
        })
    }

    fun setChartData(){
        val xData = ArrayList<String>()
        val yData = ArrayList<Entry>()

        allSessions.forEachIndexed { index, element ->
            xData.add(index.toString())

            val mins = element.time!!.split(":")[0].toFloat() * 60f
            val secs = element.time!!.split(":")[1].toFloat()

            yData.add(Entry(mins + secs,index))
        }

        val lineDataSet = LineDataSet(yData,"Secs")
        lineDataSet.color = ResourcesCompat.getColor(resources, R.color.purple_700, null)

        val data = LineData(xData,lineDataSet)

        lineChart.data = data
        lineChart.invalidate()
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }
}