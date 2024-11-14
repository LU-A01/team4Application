package com.example.team4application

import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var vibrator: Vibrator
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var rmsTextView: TextView

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

    // 音声認識を開始する関数
    fun onMicrophoneClick(view: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "話してください")
        }

        // 音声認識のリスナー設定
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {
                // 音量レベルを表示
                rmsTextView.text = "音量レベル: $rmsdB"
            }

            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                Toast.makeText(this@MainActivity, "音声認識エラー: $error", Toast.LENGTH_SHORT).show()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy() // リソースの解放
    }
}
