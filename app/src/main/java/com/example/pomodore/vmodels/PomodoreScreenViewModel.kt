package com.example.pomodore.vmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pomodore.models.PomodoreTimeModel
import com.example.pomodore.models.WorkerModel
import com.example.pomodore.remository.PomodoroTimeRepository

class PomodoreScreenViewModel: ViewModel() {

    val repo = PomodoroTimeRepository()

    private val _pomodoroTimer : MutableLiveData<PomodoreTimeModel> = MutableLiveData()
    val pomodoroTimer : LiveData<PomodoreTimeModel>
        get() {
            return _pomodoroTimer
        }

    private val _workerInfo : MutableLiveData<WorkerModel> = MutableLiveData()
    val workerInfo : LiveData<WorkerModel>
        get () {
            return _workerInfo
        }

    //Pega tudo o pomodoro timer
    fun getPomodoreTimer (){
        val pomodore = repo.loadPomodoroTime()
        _pomodoroTimer.postValue(pomodore)
    }

    //Adiciona um a contagem de loop
    fun addLoop (){
        val pomodore = repo.loadPomodoroTime()

        pomodore.actualLoop++
        if(pomodore.actualLoop > pomodore.loopsToLongPause) {
            pomodore.pomodoreStatus = 2
            pomodore.actualLoop = 0
        }else{
            if(pomodore.pomodoreStatus >= 1){
                pomodore.pomodoreStatus = 0
            }else{
                pomodore.pomodoreStatus = 1
            }
        }

        repo.savePomodoroTime(pomodore)
        getPomodoreTimer()
    }

    fun saveNewPomodoro (pomodore: PomodoreTimeModel, loadAfter: Boolean){
        val pomo = repo.loadPomodoroTime()
        pomodore.actualLoop = pomo.actualLoop
        pomodore.pomodoreStatus = pomo.pomodoreStatus
        repo.savePomodoroTime(pomodore)

        if(loadAfter)
            getPomodoreTimer()
    }

}