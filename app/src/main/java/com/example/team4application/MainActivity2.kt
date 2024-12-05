package com.example.team4application

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import java.util.*

class MainActivity2 : ComponentActivity() {
    private lateinit var vibrator: Vibrator
    private lateinit var rmsTextView: TextView
    private lateinit var toggleSwitch: Switch

    private val SAMPLE_RATE = 16000 // サンプリングレート
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

    private val timings = longArrayOf(0, 100, 100, 100, 100, 100, 100, 100)
    private val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
    private val repeatIndex = -1

    private var isRecording = false // 録音状態を管理

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        vibrator = getSystemService(Vibrator::class.java)
        rmsTextView = findViewById(R.id.rmsTextView) // TextViewの初期化
        toggleSwitch = findViewById(R.id.toggleSwitch) // Switchの初期化

        // トグルボタンの状態変更リスナー
        toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startRecording()
            } else {
                stopRecording()
            }
        }
    }

    // 録音を開始
    private fun startRecording() {
        isRecording = true

        Thread {
            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                BUFFER_SIZE
            )

            val buffer = ByteArray(BUFFER_SIZE)
            val audioData = ByteArray(SAMPLE_RATE * 2) // 1秒分のデータ用バッファ（16bit -> 2bytes）
            var offset = 0

            audioRecord.startRecording()

            while (isRecording) {
                val numRead = audioRecord.read(buffer, 0, buffer.size)
                if (numRead > 0 && offset + numRead <= audioData.size) {
                    System.arraycopy(buffer, 0, audioData, offset, numRead)
                    offset += numRead
                }

                // 音量レベルを計算して表示
                val rms = calculateRms(buffer)
                runOnUiThread {
                    rmsTextView.text = "音量レベル (RMS): %.2f".format(rms)
                }

                // 音量レベルが一定以上なら振動
                if (rms >= 100) {
                    runOnUiThread {
                        Toast.makeText(this, "100dB超え検知！振動します", Toast.LENGTH_SHORT).show()
                    }
                    vibrate()
                }
            }

            audioRecord.stop()
            audioRecord.release()

            // 最終データを処理
            processAudioData(audioData)
        }.start()

        Toast.makeText(this, "録音を開始しました", Toast.LENGTH_SHORT).show()
    }

    // 録音を停止
    private fun stopRecording() {
        isRecording = false
        Toast.makeText(this, "録音を停止しました", Toast.LENGTH_SHORT).show()
    }

    // 音声データを処理する関数
    private fun processAudioData(audioData: ByteArray) {
        // Pythonモジュールや分類処理に渡す
        println("録音データの長さ: ${audioData.size} bytes")
    }

    // 音量レベル（RMS）を計算する
    private fun calculateRms(buffer: ByteArray): Double {
        var sum = 0.0
        for (b in buffer) {
            val normalized = b / 128.0 // Byte を -1.0～1.0 に正規化
            sum += normalized * normalized
        }
        return Math.sqrt(sum / buffer.size)
    }

    // デバイスを振動させる
    private fun vibrate() {
        if (::vibrator.isInitialized && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeatIndex))
        }
    }

    // 画面遷移
    fun moveToMenu(view: View) {
        val intent = Intent(this@MainActivity2, VibrateMenuActivity::class.java)
        startActivity(intent)
    }
}
