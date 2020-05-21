package me.kxtre.trainbuddy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.*
import me.kxtre.trainbuddy.controllers.StateController.INTENT_START_TRAINING
import me.kxtre.trainbuddy.controllers.StateController.INTENT_STATE_CHANGE
import me.kxtre.trainbuddy.controllers.StateController.training
import me.kxtre.trainbuddy.databinding.ActivityMainBinding
import me.kxtre.trainbuddy.interfaces.BasicCallBack
import me.kxtre.trainbuddy.interfaces.Callback
import me.kxtre.trainbuddy.models.Exercise
import me.kxtre.trainbuddy.models.State
import me.kxtre.trainbuddy.models.Training

interface SensorListener {
    fun onChange(x: Float, y: Float, z: Float)
}
class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mSensorManager: SensorManager
    private var mSensor: Sensor? = null
    private var listener: SensorListener = object: SensorListener {
        override fun onChange(x: Float, y: Float, z: Float) {
        }

    }

    val SHARED_PREFERENCES = "Shared"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val button = binding.buttonMain
        val sharedPreferences = getSharedPreferences(
            SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        DataManager.INSTANCE.sharedPreferences = sharedPreferences
        evaluateLoginStatus()

        button.setOnLongClickListener { _button -> when(StateController.state) {
            State.Initial -> initialButtonLongClick(_button)
            State.GUEST -> guestButtonLongClick(_button)
            State.LOGGED -> loggedButtonLongClick(_button)
        } }
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun evaluateLoginStatus() {
        val button = binding.buttonMain
        AuthenticationController.checkAuthentication(this, object : Callback {

            override fun onSucess() {
                StateController.changeState(State.LOGGED)
                button.text = getString(R.string.start_training_more_options)
            }

            override fun onError() {
                StateController.changeState(State.GUEST)
                button.text = getString(R.string.login_register)
            }
        })
    }

    private fun loggedButtonLongClick(_button: View): Boolean {
        goToMoreOptionsView(this)
        return true
    }

    private fun goToMoreOptionsView(mainActivity: MainActivity) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivityForResult(intent, INTENT_START_TRAINING)
    }

    private fun guestButtonLongClick(_button: View): Boolean {
        goToRegisterView(this)
        return true
    }

    private fun goToRegisterView(mainActivity: MainActivity) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivityForResult(intent, INTENT_STATE_CHANGE)
    }

    private fun initialButtonLongClick(button: View): Boolean {
        Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show()
        return  true
    }

    fun mainButtonClick(button: View) {
        when(StateController.state) {
            State.Initial -> initialButtonClick(button)
            State.GUEST -> guestButtonClick(button)
            State.LOGGED -> loggedButtonClick(button)
        }
    }

    private fun initialButtonClick(button: View) {
        Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show()
    }

    private fun guestButtonClick(button: View) {
        goToLoginView(this)
    }

    private fun goToLoginView(mainActivity: MainActivity) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, INTENT_STATE_CHANGE)
    }

    private fun loggedButtonClick(button: View) {
        val trainings = Controller.availableTrainings
        val training = ContextEngine.decideBestTraining(trainings)
        executeTraining(button, training)
    }

    private fun executeTraining(button: View, training: Training) {
        StateController.training = training
        executeNextExercise()
    }

    private fun executeNextExercise() {
        val exercises = StateController.training?.exercises
        if (exercises?.size == 0 || exercises?.indexOf(StateController.exercise) == exercises?.size?.minus(
                1
            )
        ) {
            notifyTrainingComplete()
        }
        if (StateController.exercise == null) {
            executeExercise(exercises?.get(0))
            return
        }
        executeExercise(exercises?.get(exercises.indexOf(StateController.exercise).plus(1)))
    }

    private fun executeExercise(exercise: Exercise?) {
        if(exercise == null) {
            return
        }
        //TODO("add to screen list")
        StateController.exercise = exercise
        exercise.registerCountMechanism(object : BasicCallBack {
            override fun onEvent() {
                if(exercise.progress != exercise.repeats) {
                    incrementExerciseCounter()
                    return
                }
                exercise.registerCountMechanism(null);
                executeNextExercise()
            }
        })
        listener = object : SensorListener {
            override fun onChange(x: Float, y: Float, z: Float) {
                exercise.notifyAccelerometerChange(x, y, z)
            }
        }

    }

    private fun incrementExerciseCounter() {
        TODO("increment on the latest on the list")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode !=  Activity.RESULT_OK) {
            return;
        }
        if(requestCode == INTENT_STATE_CHANGE) {
            evaluateLoginStatus()
            return
        }
        if(requestCode == INTENT_START_TRAINING) {
            val trainingID = data!!.getIntExtra("trainingID", 0)
            if(trainingID == 0) {
                return
            }
            try {
                executeTraining(findViewById(R.id.button_main), Controller.findByIdInAvailableTrainings(trainingID))
            } catch (e: Error) {
                Toast.makeText(this, R.string.training_not_available, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x = event?.values?.get(0)!!
        val y = event.values?.get(1)!!
        val z = event.values?.get(2)!!
        listener.onChange(x, y, z)
    }
    private fun notifyTrainingComplete() {
        Toast.makeText(this, "Training", Toast.LENGTH_SHORT).show()
    }
}
