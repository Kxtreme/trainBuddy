package me.kxtre.trainbuddy

import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.Controller
import me.kxtre.trainbuddy.databinding.ActivityOptionsBinding

class OptionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_options)
        binding.buttonRecordSensors.setOnClickListener { startRecordActivity() }
    }

    private fun startRecordActivity(): Boolean {
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
        return true
    }
}
