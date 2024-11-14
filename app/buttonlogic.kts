package com.example.myapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isStarted = false
    private lateinit var statusLabel: TextView
    private lateinit var startButton: Button
    private lateinit var switchTrain: Switch
    private lateinit var switchCar: Switch
    private lateinit var switchBicycle: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        statusLabel = findViewById(R.id.status_label)
        startButton = findViewById(R.id.start_button)
        switchTrain = findViewById(R.id.switch_train)
        switchCar = findViewById(R.id.switch_car)
        switchBicycle = findViewById(R.id.switch_bicycle)

        // Set up button press listener
        startButton.setOnClickListener {
            onButtonPress()
        }

        // Set up switch state change listeners
        switchTrain.setOnCheckedChangeListener { _, _ -> updateStatusLabel() }
        switchCar.setOnCheckedChangeListener { _, _ -> updateStatusLabel() }
        switchBicycle.setOnCheckedChangeListener { _, _ -> updateStatusLabel() }

        // Menu button click listener
        val menuButton = findViewById<Button>(R.id.menu_button)
        menuButton.setOnClickListener {
            openMenu()
        }
    }

    private fun onButtonPress() {
        // Toggle between "Start" and "Running"
        if (isStarted) {
            startButton.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
            startButton.text = "Start"
            isStarted = false
        } else {
            startButton.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
            startButton.text = "Running"
            isStarted = true
        }
    }

    private fun updateStatusLabel() {
        val selectedOptions = mutableListOf<String>()
        if (switchTrain.isChecked) selectedOptions.add("Train")
        if (switchCar.isChecked) selectedOptions.add("Car")
        if (switchBicycle.isChecked) selectedOptions.add("Bicycle")
        
        if (selectedOptions.isNotEmpty()) {
            statusLabel.text = "Selected: ${selectedOptions.joinToString()}"
        } else {
            statusLabel.text = "None selected."
        }
    }

    private fun openMenu() {
        // Display a simple popup menu with Settings and Help options
        val popupMenu = PopupMenu(this, findViewById(R.id.menu_button))
        menuInflater.inflate(R.menu.menu_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.settings -> {
                    showSettingsDialog()
                    true
                }
                R.id.help -> {
                    showHelpDialog()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showSettingsDialog() {
        // Create a dialog for settings
        val settingsView = layoutInflater.inflate(R.layout.dialog_settings, null)

        val intensitySeekBar = settingsView.findViewById<SeekBar>(R.id.intensity_seekbar)
        val frequencySeekBar = settingsView.findViewById<SeekBar>(R.id.frequency_seekbar)
        val intensityLabel = settingsView.findViewById<TextView>(R.id.intensity_label)
        val frequencyLabel = settingsView.findViewById<TextView>(R.id.frequency_label)

        intensitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                intensityLabel.text = "Intensity: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        frequencySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                frequencyLabel.text = "Frequency: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        AlertDialog.Builder(this)
            .setTitle("Settings")
            .setView(settingsView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showHelpDialog() {
        // Create a simple help dialog
        AlertDialog.Builder(this)
            .setTitle("Help")
            .setMessage("This is a demo app. Use the settings and switches to interact.")
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
