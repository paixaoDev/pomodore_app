package com.example.pomodore.remository

import com.example.pomodore.models.PomodoreTimeModel

class PomodoroTimeRepository {

    var pomodoreTime : PomodoreTimeModel? = null

    fun savePomodoroTime (pomodore : PomodoreTimeModel){
        pomodoreTime = pomodore
    }

    fun loadPomodoroTime () : PomodoreTimeModel{
        return pomodoreTime ?: PomodoreTimeModel()
    }
}