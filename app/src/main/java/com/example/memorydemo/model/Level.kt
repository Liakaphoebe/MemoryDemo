package com.example.memorydemo.model

class Level(var level: Int? = 0, var player: String? = "",
            var bestTime: String? = "0:0", var gameId: Int? = 0)
{
    override fun toString(): String = "$level , $player, $bestTime, $gameId"
}