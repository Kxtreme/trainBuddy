package me.kxtre.trainbuddy

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_record.*
import me.kxtre.trainbuddy.databinding.ActivityRecordBinding


class RecordActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityRecordBinding
    private lateinit var mSensorManager: SensorManager
    private var recording = false
    private var mSensor: Sensor? = null
    private var timestamp: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_record)
        binding.buttonPrimary.setOnClickListener { primaryClick() }
        binding.buttonSecondary.setOnClickListener { shareResults() }
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun shareResults() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.share_subject)
        )
        intent.putExtra(Intent.EXTRA_TEXT, binding.textViewLog.text)
   startActivity(Intent.createChooser(intent, getString(R.string.share_subject)))
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    private fun primaryClick(): Boolean {
        recording = !recording
        if(recording) {
            binding.textViewLog.text = ""
            binding.buttonPrimary.text = getString(R.string.button_stop)
            mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL)
            return true
        }
        binding.buttonPrimary.text = getString(R.string.button_start)
        mSensorManager.unregisterListener(this)
        button_secondary.visibility = View.VISIBLE
        return true
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val x = event?.values?.get(0)!!
        val y = event.values?.get(1)!!
        val z = event.values?.get(2)!!
        if(timestamp == null) {
            timestamp = event.timestamp
        }
       binding.textViewLog.text = binding.textViewLog.text.toString() + (event.timestamp - timestamp!!) + ';' + x + ';' + y + ';' + z + '\n'
    }
}
