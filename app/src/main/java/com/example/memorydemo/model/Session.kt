package com.example.memorydemo.model

class Session (var player: String? = "",var attempt: Int? = 0,var level: Int? = 0,
               var gameId: Int? = 0,var time: String? = "0:0")
{
    override fun toString(): String = "$player , $gameId, $time"
}