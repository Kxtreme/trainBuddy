package me.kxtre.trainbuddy

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.adapters.ExercisesAdapter
import me.kxtre.trainbuddy.adapters.TrainingsAdapter
import me.kxtre.trainbuddy.controllers.Controller
import me.kxtre.trainbuddy.controllers.StateController
import me.kxtre.trainbuddy.controllers.StateController.STATE_READY
import me.kxtre.trainbuddy.databinding.ActivityOptionsBinding
import me.kxtre.trainbuddy.databinding.ActivityTrainingBinding
import me.kxtre.trainbuddy.models.Training

class TrainingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training)
        setAdapters()
        registerListeners()
    }

    private fun registerListeners() {
        binding.listviewTrainings.setOnItemClickListener { adapterView, view, i, l ->
            val training =  adapterView.getItemAtPosition(i) as Training
            val intent = intent
            intent.putExtra("trainingID", training.id)
            intent.putExtra("trainingName", training.name)

            if(training.done) {
                intent.putExtra("done", true)
            } else {
                intent.putExtra("done", false)
            }

            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
    private fun setAdapters() {
        Controller.availableTrainings.let {
            val trainingsAdapter = TrainingsAdapter(
                this,
                R.layout.list_item_training,
                it
            )
            binding.listviewTrainings.adapter = trainingsAdapter
        }
    }
}