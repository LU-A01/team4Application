package com.example.team4application

import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    private lateinit var vibrator: Vibrator

    private val timings = longArrayOf(0, 100, 100, 100, 100, 100, 100, 100)
    private val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
    private val repeatIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        vibrator = getSystemService(Vibrator::class.java)
    }

    fun moveToMenu(view: View) {
        val intent = Intent(this@MainActivity, VibrateMenuActivity::class.java)
        startActivity(intent)
    }

    // Vibrate the device
    fun vibrate(view: View) {
        if (::vibrator.isInitialized && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
        }
    }

    // turn on the microphone
    fun onMicrophoneClick(view: View) {
    }
}

