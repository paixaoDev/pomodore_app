package com.example.pomodore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}