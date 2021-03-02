package com.example.pomodore.models

class PomodoreTimeModel (
    val workTime : Int = 20,
    val pauseTime : Int = 5,
    val longPause : Int = 15,

    val loopsToLongPause : Int = 3,
    var actualLoop : Int = 0,

    // 0 work | 1 pause | 2 Long Pause
    var pomodoreStatus : Int = 0
)