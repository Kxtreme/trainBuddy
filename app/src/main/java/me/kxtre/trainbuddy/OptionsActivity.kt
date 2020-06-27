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
import me.kxtre.trainbuddy.controllers.StateController
import me.kxtre.trainbuddy.databinding.ActivityOptionsBinding

class OptionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionsBinding
    val CHOOSE_TRAINING = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_options)
        binding.buttonRecordSensors.setOnClickListener { startRecordActivity() }
        binding.buttonTrainings.setOnClickListener { startTrainingsActivity() }
    }

    private fun startTrainingsActivity():Boolean {
        val intent = Intent(this, TrainingActivity::class.java)
        startActivityForResult(intent, CHOOSE_TRAINING)
        return true
    }

    private fun startRecordActivity(): Boolean {
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.CONTEXT_RESTRICTED && requestCode == CHOOSE_TRAINING) {
            val trainingID = data!!.getIntExtra("trainingID", 0)
            val intent = intent
            intent.putExtra("trainingID", trainingID)
            setResult(Activity.RESULT_OK, intent);
            finish()
        }
    }
}
