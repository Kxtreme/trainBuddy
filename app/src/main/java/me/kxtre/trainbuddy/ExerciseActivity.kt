package me.kxtre.trainbuddy

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.adapters.ExercisesAdapter
import me.kxtre.trainbuddy.controllers.ContextEngine
import me.kxtre.trainbuddy.controllers.Controller
import me.kxtre.trainbuddy.controllers.StateController
import me.kxtre.trainbuddy.databinding.ActivityExerciseBinding
import me.kxtre.trainbuddy.interfaces.BasicCallBack
import me.kxtre.trainbuddy.models.Exercise
import me.kxtre.trainbuddy.models.Training
import org.json.JSONObject
import org.kaldi.*
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

interface SensorListener {
    fun onChange(x: Float, y: Float, z: Float)
}

class ExerciseActivity : AppCompatActivity(), SensorEventListener, RecognitionListener {
    init {
        System.loadLibrary("kaldi_jni")
    }

    private lateinit var binding: ActivityExerciseBinding
    private lateinit var mSensorManager: SensorManager
    private lateinit var training: Training
    private var model: Model? = null
    private var recognizer: SpeechRecognizer? = null
    private var mSensor: Sensor? = null
    private var listener: SensorListener = object : SensorListener {
        override fun onChange(x: Float, y: Float, z: Float) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val trainingID = intent!!.getIntExtra("trainingID", 0)
        if (trainingID == 0) {
            return
        }
        binding.trainingName.text = intent!!.getStringExtra("trainingName")

        if(intent!!.getBooleanExtra("done", false)) {
            binding.buttonMain.visibility = View.GONE
        }

        try {
           training = Controller.findByIdInAvailableTrainings(trainingID)
            StateController.training = training
            redrawExerciseList()
        } catch (e: Error) {
            Toast.makeText(this, R.string.training_not_available, Toast.LENGTH_SHORT).show()
        }

        SetupTask(this).execute()
    }

    override fun onDestroy() {
        stopListeners()
        super.onDestroy()
    }

    /*
* button action on click when logged
* */
    fun mainButtonClick(button: View) {
        //StateController.cleanTraining()
        StateController.exercise = null
        executeTraining(training)
        binding.buttonMain.visibility = View.GONE
    }

    /*
    * action to execute the training
    * */
    private fun executeTraining(training: Training) {

        if (StateController.training == null) {
            startListeners()
        }
        StateController.training = training
        executeNextExercise()
    }

    /*
    * action to execute the next exercise in the training
    * it should also determine if the training is complete or not
    * */
    private fun executeNextExercise() {
        val exercises = StateController.training?.exercises
        if (StateController.exercise == null) {
            if (exercises?.size == 0) {
                Toast.makeText(
                    applicationContext,
                    R.string.exercises_not_available,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            executeExercise(exercises?.get(0))
            return
        }
        val index = exercises?.indexOf(StateController.exercise)?.plus(1)
        if (index != null && index >= exercises.size) {
            notifyTrainingComplete()
            return
        }
        executeExercise(index?.let { exercises[it] })
    }

    /*
    * start the sensors listening
    * includes the accelerometer and the voice recognition services
    * */
    private fun startListeners() {
        recognizeMicrophone()
        mSensorManager.registerListener(
            this, mSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    /*
    * stop the sensors listening
    * includes the accelerometer and the voice recognition services
    * */
    private fun stopListeners() {
        mSensorManager.unregisterListener(this)
        stopRecognizeMicrophone()
    }

    /*
    * start the exercise
    * includes register the exercise listener for the accelerometer data output
    * includes register an exercise count listener
    *   listener includes notify the exercise increments
    *   listener includes notify if the training is done
    *   listener includes execute the next exercise in training when done
    * */
    private fun executeExercise(exercise: Exercise?) {
        if (exercise == null) {
            return
        }
        StateController.exercise = exercise
        redrawExerciseList()
        exercise.registerCountMechanism(object : BasicCallBack {
            override fun onEvent() {
                if (!exercise.isDone) {
                    incrementExerciseCounter()
                    return
                }
                exercise.registerCountMechanism(object : BasicCallBack {
                    override fun onEvent() {
                    }

                });
                notifyExerciseComplete()
                executeNextExercise()
            }
        })
        listener = object : SensorListener {
            override fun onChange(x: Float, y: Float, z: Float) {
                binding.textViewX.text = x.toString()
                binding.textViewY.text = y.toString()
                binding.textViewZ.text = z.toString()
                exercise.notifyAccelerometerChange(x, y, z)
            }
        }
    }

    /*
    * represents one exercise done
    * */
    private fun notifyExerciseComplete() {
        val exercise = StateController.exercise ?: return
        Controller.notifyExerciseComplete(
            this, exercise.id.toString(), when (exercise.percentage) {
                100 -> "normal"
                in 101..Int.MAX_VALUE -> "easy"
                else -> "hard"
            }
        )
    }

    /*
        * represents what to do on exercise count change
        * */
    private fun incrementExerciseCounter() {
        redrawExerciseList()
    }

    /*
    * represents the UI redraw for list update
    * */
    private fun redrawExerciseList() {
        StateController.training?.exercises?.let {
            val exercisesAdapter = ExercisesAdapter(
                this,
                R.layout.list_item_exercise,
                it
            )
            binding.listExercises.adapter = exercisesAdapter
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

    /*
* represents the set of actions to occour when the training is complete
* */
    private fun notifyTrainingComplete() {
        Toast.makeText(applicationContext, getString(R.string.training_done), Toast.LENGTH_LONG)
            .show()
        redrawExerciseList()
        stopListeners()
        StateController.training?.done = true
        Controller.notifyTrainingComplete(this, StateController.training?.id.toString())

        StateController.training = null
    }

    private class SetupTask internal constructor(activity: ExerciseActivity) :
        AsyncTask<Void?, Void?, Exception?>() {
        var activityReference: WeakReference<ExerciseActivity> = WeakReference(activity)
        override fun doInBackground(vararg p0: Void?): Exception? {
            try {
                val assets = Assets(activityReference.get())
                val assetDir: File = assets.syncAssets()
                Vosk.SetLogLevel(0)
                activityReference.get()?.model = Model("$assetDir/model-android")

            } catch (e: IOException) {
                return e
            }
            return null
        }


    }

    /*
* represents the start the microphone recognition service
* */
    private fun recognizeMicrophone() {
        if (recognizer != null) {
            recognizer!!.cancel()
            recognizer = null
        } else {
            try {
                recognizer = SpeechRecognizer(model)
                recognizer!!.addListener(this)
                recognizer!!.startListening()
            } catch (e: IOException) {
            }
        }
    }

    /*
* represents the stop the microphone recognition service
* */
    private fun stopRecognizeMicrophone() {
        try {
            recognizer?.cancel()
            recognizer = null

        } catch (e: IOException) {
        }
    }

    /*
* represents the event of receiving an phrase result from the microphone recognition service
* includes send the phrase to the context engine for processing and redraw the UI
* */
    override fun onResult(p0: String?) {
        if (p0 == null) return

        ContextEngine.sendInstructions(JSONObject(p0).getString("text"))
        redrawExerciseList()
    }

    /*
* represents the action of receiving partial results from the microphone recognition service
* */
    override fun onPartialResult(p0: String?) {

    }

    /*
* represents the action of the microphone recognition service timing out
* includes restart the service
* */
    override fun onTimeout() {
        recognizeMicrophone()
    }

    /*
* represents an Error event from the microphone recognition service
* */
    override fun onError(p0: java.lang.Exception?) {
        p0?.printStackTrace()
    }
}