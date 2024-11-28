package com.example.team4application

import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : ComponentActivity() {
    private lateinit var vibrator: Vibrator

    private val timings = longArrayOf(0, 100, 100, 100, 100, 100, 100, 100)
    private val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
    private val repeatIndex = -1

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//
//        vibrator = getSystemService(Vibrator::class.java)
//
//        // Pythonコードを実行する前にPython.start()の呼び出しが必要
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//
//        val py = Python.getInstance()
//        val module = py.getModule("hello") // スクリプト名
//
//        try {
//            val randomNumber = module.callAttr("create_random_number").toFloat() // 関数名
//            findViewById<TextView>(R.id.text_view).text = randomNumber.toString()
//        } catch (e: PyException) {
//            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        val py = Python.getInstance()
        val module = py.getModule("chaquopy_test") // スクリプト名

        try {
            val randomNumber = module.callAttr("create_random_number").toFloat() // 関数名
            findViewById<TextView>(R.id.text_view).text = randomNumber.toString()
        } catch (e: PyException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun moveToMenu(view: View) {
        val intent = Intent(this@MainActivity, VibrateMenuActivity::class.java)
        startActivity(intent)
    }

    // Vibrate the device
//    fun vibrate(view: View) {
//        if (::vibrator.isInitialized && vibrator.hasVibrator()) {
//            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
//        }
//    }

    // turn on the microphone
    fun onMicrophoneClick(view: View) {
    }
}

