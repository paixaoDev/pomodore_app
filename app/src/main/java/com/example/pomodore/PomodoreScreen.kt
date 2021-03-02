package com.example.pomodore

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import com.example.pomodore.models.PomodoreTimeModel
import com.example.pomodore.vmodels.PomodoreScreenViewModel
import kotlinx.android.synthetic.main.activity_pomodore_screen.*
import java.util.concurrent.TimeUnit


class PomodoreScreen : MainActivity() {

    private var timer : CountDownTimer? = null
    private val viewModel = PomodoreScreenViewModel()
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodore_screen)

        setupView()
        setupObservable()
        viewModel.getPomodoreTimer()
    }

    var canGoNext = true

    private fun setupView (){
        this.button_next.setOnClickListener {
            if(canGoNext) {

                mediaPlayer?.stop()
                timer?.start()

                canGoNext = false
                this.button_next.isEnabled = canGoNext
            }
        }

        this.configure.setOnClickListener {
            button_finish_adjust.visibility = if(button_finish_adjust.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            adjust_screen.visibility = if(adjust_screen.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            textView.visibility = if(adjust_screen.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        this.button_finish_adjust.setOnClickListener {
            viewModel.saveNewPomodoro(PomodoreTimeModel(
                    workTime = seekbar_work.progress + 10 ,
                    pauseTime = seekbar_pause.progress+ 3,
                    longPause = seekbar_big_pause.progress + 10,
                    loopsToLongPause = 3,
                    actualLoop = 0,
                    pomodoreStatus = 0
            ), canGoNext)

            button_finish_adjust.visibility = View.GONE
            adjust_screen.visibility = View.GONE
            textView.visibility = View.VISIBLE
        }

        this.seekbar_work.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                text_work_time.text = (progress + 10).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        this.seekbar_pause.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                text_pause_time.text = (progress + 3).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        this.seekbar_big_pause.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                text_long_pause_time.text = (progress + 10).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun setupObservable (){
        val pomodoreTimeObservable = viewModel.pomodoroTimer
        val workerObservable = viewModel.workerInfo

        pomodoreTimeObservable.observe(this, Observer { pomodore ->
            startTimer( 60 * when(pomodore.pomodoreStatus){
                0 -> pomodore.workTime
                1 -> pomodore.pauseTime
                2 -> pomodore.longPause
                else -> 0
            })

            updateButton(pomodore.pomodoreStatus)
        })

        workerObservable.observe(this, Observer { worker ->

        })
    }

    private fun startTimer (time: Int){
        updateTimer((time * 1000).toLong())

        if(time > 0){
            timer = object : CountDownTimer(
                (time * 1000).toLong(),
                (1000).toLong()
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    updateTimer(millisUntilFinished)
                }

                override fun onFinish() {
                    changePomodoroType()
                    initiateAlarm()
                }
            }
        }
    }

    private fun updateTimer (time : Long){
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = time / 1000 % 60
        this.text_actual_time.text = "$minutes : $seconds"
    }

    private fun updateButton (type: Int){
        this.button_next.isEnabled = true
        this.button_next.text = when(type){
            0 -> "Trabalhar"
            1 -> "Pausa"
            2 -> "Pausa Longa"
            else -> "Iniciar"
        }
    }

    private fun changePomodoroType (){
        viewModel.addLoop()
        canGoNext = true
        this.button_next.isEnabled = true
    }

    private fun initiateAlarm (){
        if(mediaPlayer == null){

            var alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if(alert == null){
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                if(alert == null){
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                }
            }

            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 100, AudioManager.FLAG_ALLOW_RINGER_MODES)

            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(this, alert)
        }

        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }
}