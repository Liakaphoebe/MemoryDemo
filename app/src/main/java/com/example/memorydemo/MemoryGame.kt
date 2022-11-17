package com.example.memorydemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorydemo.databinding.ActivityMemoryGameBinding
import com.example.memorydemo.model.Card
import kotlinx.android.synthetic.main.activity_memory_game.*
import java.util.*
import kotlin.concurrent.schedule


class MemoryGame : AppCompatActivity() {
    private lateinit var binding: ActivityMemoryGameBinding
    var picked = mutableListOf<Card>()
    private var start: Boolean = true
    var timer:Boolean = false
    var key: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoryGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cardsArrayList: ArrayList<Card> = ArrayList()

        val images = mutableListOf(R.drawable.sadg, R.drawable.sadb,
            R.drawable.happyg, R.drawable.happyb, R.drawable.grandma, R.drawable.papi,
            R.drawable.sadb, R.drawable.sadg, R.drawable.sadbaby, R.drawable.sadbabyg)

        tv_attempts.text = "Attempts: ${intent.getIntExtra("attempts",0)}"
        tv_bestTime.text = "Best: ${intent.getStringExtra("bestTime")}"
        key = intent.getStringExtra("key").toString()

        val shuffledImages = mutableListOf<Int>()
        images.subList(0,6).forEach{
            shuffledImages.add(it)
            shuffledImages.add(it)
        }

        shuffledImages.shuffle()

        for (i in 0..11){
            cardsArrayList.add(Card(i,shuffledImages[i],false))
        }
        val layoutManager = GridLayoutManager(this, 3)

        memory_grid.layoutManager = layoutManager
        val cardAdapter: CardsAdapter = CardsAdapter(this, cardsArrayList);
        memory_grid.adapter = cardAdapter

        tv_timer.start()
    }

    fun win(){
        tv_timer.stop()

        val builder = AlertDialog.Builder(this)
            .setTitle("Winner")
            .setMessage("You've won, your total time is: " + tv_timer.text)
            .setIcon(R.drawable.trophy)
            .setPositiveButton("OK"){ _, _ ->
                val intent = Intent()
                intent.putExtra("time", tv_timer.text)
                intent.putExtra("key", key)
                setResult(RESULT_OK, intent)
                finish()
            }

        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    inner class CardsAdapter(
        private val context: Context,
        private val cardsAdapterList: ArrayList<Card>
        ) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CardViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.memory_card,
                parent, false
            )
            return CardViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            val map: Card = cardsAdapterList[position]
            val img = holder.memory_card_img

            if(picked.contains(map) || map.matched)
                img.setImageResource(map.img)
            else
                img.setImageResource(R.drawable.code)


            img.setOnClickListener{
                if(timer) return@setOnClickListener

                if(map.matched){
                    Toast.makeText(context, "Already matched", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if(picked.contains(map)){
                    picked.remove(map)
                }
                else if (picked.size > 0){
                    val before = picked.last()
                    picked.add(map)
                    val current = picked.last()

                    if(before.matched) {
                        picked.add(map)
                    }
                    else if (current.img != before.img){
                        timer = true
                        Timer().schedule(1000) {
                            this@MemoryGame.runOnUiThread {
                                picked.clear()
                                timer = false
                                notifyDataSetChanged()
                            }
                        }
                    }
                    else {
                        var allUnMatched = 0
                        for(_card in cardsAdapterList){
                            if(_card == before || _card == current){
                                _card.matched = true
                            }

                            if(!_card.matched) allUnMatched++
                        }

                        if(allUnMatched < 3){
                            for(_card in cardsAdapterList){
                                _card.matched = true
                            }
                            win()
                        }
                    }
                }
                else {
                    if(start){
                        start = false
                    }
                    picked.add(map)
                }

                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return cardsAdapterList.size
        }

        inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val memory_card_img: ImageView = itemView.findViewById(R.id.memory_card_img)
            val memory_rl: RelativeLayout = itemView.findViewById(R.id.memory_rl)
        }
    }
}