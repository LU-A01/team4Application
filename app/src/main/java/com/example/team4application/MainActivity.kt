package com.example.team4application

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private lateinit var vibrator: Vibrator
    private lateinit var rmsTextView: TextView
    private lateinit var resultTextView: TextView // 結果表示用
    private lateinit var startButton: Button // 録音ボタン

    private val SAMPLE_RATE = 16000 // サンプリングレート
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

    private val timings = longArrayOf(0, 100, 100, 100, 100, 100, 100, 100)
    private val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
    private val repeatIndex = -1
    private val PERMISSION_REQUEST_CODE = 1
    private var isRecording = false // 録音状態を管理

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vibrator = getSystemService(Vibrator::class.java)
        rmsTextView = findViewById(R.id.rmsTextView) // TextViewの初期化
        resultTextView = findViewById(R.id.resultTextView) // 結果表示用
        startButton = findViewById(R.id.startButton) // 録音ボタンの初期化

        // ボタンのクリックリスナー
        startButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
    }

    private fun checkAudioPermission(): Boolean {
        // 権限が既に許可されているか確認
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        // 権限をリクエスト
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "マイク権限が許可されました", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "マイク権限が必要です", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startRecording() {
        isRecording = true
        startButton.text = "STOP" // ボタンのテキストを変更

        Thread {
            if (checkAudioPermission()) {
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
                }

                audioRecord.stop()
                audioRecord.release()

                // 最終データを処理
                processAudioData(audioData)
            } else {
                runOnUiThread {
                    Toast.makeText(this, "マイク権限がありません", Toast.LENGTH_SHORT).show()
                    requestAudioPermission() // 権限リクエスト
                }
            }
        }.start()

        Toast.makeText(this, "録音を開始しました", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording() {
        isRecording = false
        startButton.text = "START" // ボタンのテキストを変更
        Toast.makeText(this, "録音を停止しました", Toast.LENGTH_SHORT).show()
    }

    private fun processAudioData(audioData: ByteArray) {
        // バイト値の合計を計算
        val byteSum = calculateByteSum(audioData)

        // 合計値をTextViewに表示
        val resultText = "録音データのバイト合計: $byteSum"
        resultTextView.text = resultText
    }

    private fun calculateByteSum(audioData: ByteArray): Long {
        var sum: Long = 0
        for (byte in audioData) {
            sum += byte.toUByte().toLong() // 符号なしのバイト値を加算
        }
        return sum
    }

    private fun calculateRms(buffer: ByteArray): Double {
        var sum = 0.0
        for (b in buffer) {
            val normalized = b / 128.0 // Byte を -1.0～1.0 に正規化
            sum += normalized * normalized
        }
        return Math.sqrt(sum / buffer.size)
    }

    private fun vibrate() {
        if (::vibrator.isInitialized && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 100), intArrayOf(0, 255, 0, 255), -1))
        }
    }

    fun moveToMenu(view: View) {
        val intent = Intent(this@MainActivity, VibrateMenuActivity::class.java)
        startActivity(intent)
    }
}
