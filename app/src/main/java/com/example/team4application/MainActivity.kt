package com.example.team4application

import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var vibrator: Vibrator
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var rmsTextView: TextView
    private lateinit var toggleSwitch: Switch

    private val timings = longArrayOf(0, 100, 100, 100, 100, 100, 100, 100)
    private val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
    private val repeatIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        vibrator = getSystemService(Vibrator::class.java)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this) // SpeechRecognizerの初期化
        rmsTextView = findViewById(R.id.rmsTextView) // TextViewの初期化
        toggleSwitch = findViewById(R.id.toggleSwitch) // Switchの初期化

        // トグルボタンの状態変更リスナー
        toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startSpeechRecognition()
            } else {
                stopSpeechRecognition()
            }
        }
    }
    // 画面遷移
    fun moveToMenu(view: View) {
        val intent = Intent(this@MainActivity, VibrateMenuActivity::class.java)
        startActivity(intent)
    }

    // デバイスを振動させる
    fun vibrate(view: View) {
        if (::vibrator.isInitialized && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "話してください")
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, Long.MAX_VALUE) // 無音タイムアウトを無効化
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 60000L) // 最低1分間認識
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {
                val adjustedRms = (rmsdB.coerceAtLeast(0f) * 10).toInt()
                rmsTextView.text = "音量レベル (dB 相当): $adjustedRms"
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                // 必要に応じて再開
            }
            override fun onError(error: Int) {
                Toast.makeText(this@MainActivity, "音声認識エラー: $error", Toast.LENGTH_SHORT).show()
                // 必要に応じて再試行
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    Toast.makeText(this@MainActivity, "認識結果: ${matches[0]}", Toast.LENGTH_LONG).show()
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
        Toast.makeText(this, "音声認識を開始しました", Toast.LENGTH_SHORT).show()
    }



    // 音声認識を停止
    private fun stopSpeechRecognition() {
        speechRecognizer.stopListening()
        Toast.makeText(this, "音声認識を停止しました", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy() // リソースの解放
    }
}
