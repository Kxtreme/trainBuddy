package me.kxtre.trainbuddy

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.adapters.ExercisesAdapter
import me.kxtre.trainbuddy.controllers.*
import me.kxtre.trainbuddy.controllers.StateController.INTENT_START_TRAINING
import me.kxtre.trainbuddy.controllers.StateController.INTENT_STATE_CHANGE
import me.kxtre.trainbuddy.controllers.StateController.STATE_READY
import me.kxtre.trainbuddy.databinding.ActivityMainBinding
import me.kxtre.trainbuddy.interfaces.BasicCallBack
import me.kxtre.trainbuddy.interfaces.Callback
import me.kxtre.trainbuddy.models.Exercise
import me.kxtre.trainbuddy.models.State
import me.kxtre.trainbuddy.models.Training
import me.kxtre.trainbuddy.utils.HttpCallBack
import me.kxtre.trainbuddy.utils.HttpUtils
import org.json.JSONObject
import org.kaldi.*
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference


interface SensorListener {
    fun onChange(x: Float, y: Float, z: Float)
}

class MainActivity : AppCompatActivity(), SensorEventListener, RecognitionListener {
    init {
       System.loadLibrary("kaldi_jni")
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSensorManager: SensorManager
    private var model: Model? = null
    private var recognizer: SpeechRecognizer? = null
    private var mSensor: Sensor? = null
    private var listener: SensorListener = object : SensorListener {
        override fun onChange(x: Float, y: Float, z: Float) {
        }

    }

    private val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    val SHARED_PREFERENCES = "Shared"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val button = binding.buttonMain
        button.visibility = View.INVISIBLE
        button.isClickable = false
        binding.trainingName.text = getString(R.string.retrieving_state)

        val sharedPreferences = getSharedPreferences(
            SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )

        DataManager.INSTANCE.sharedPreferences = sharedPreferences
        evaluateLoginStatus()
        //represents the decision tree of long main button click
//        button.setOnLongClickListener { _button ->
//            when (StateController.state) {
//                State.Initial -> initialButtonLongClick(_button)
//                State.GUEST -> guestButtonLongClick(_button)
//                State.LOGGED -> loggedButtonLongClick(_button)
//            }
//        }
//        requestPermissions()
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        SetupTask(this).execute()
    }

    /*
    * request user permissions needed
    * */
    private fun requestPermissions() {
        // Check if user has given permission to record audio
        val permissionCheck = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.RECORD_AUDIO
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSIONS_REQUEST_RECORD_AUDIO
            )
            return
        }
    }

    override fun onDestroy() {
        stopListeners()
        super.onDestroy()
    }

    /*
    * check with server the login status
    * */
    private fun evaluateLoginStatus() {
        //val button = binding.buttonMain
        AuthenticationController.checkAuthentication(this, object : Callback {

            override fun onSucess() {
                StateController.changeState(State.LOGGED)
                //button.text = getString(R.string.start_training_more_options)
                goToTrainingList(this@MainActivity)
            }

            override fun onError() {
                StateController.changeState(State.Initial)
                //button.text = getString(R.string.login_register)
                goToLoginView(this@MainActivity)
            }
        })
    }

    private fun goToTrainingList(mainActivity: MainActivity) {
        val intent = Intent(this, TrainingActivity::class.java)
        startActivityForResult(intent, INTENT_START_TRAINING)
    }

    /*
    * button action on long click when already logged
    * */
    private fun loggedButtonLongClick(_button: View): Boolean {
        goToMoreOptionsView(this)
        return true
    }

    /*
    * represents the action to go to the more options activity
    * */
    private fun goToMoreOptionsView(mainActivity: MainActivity) {
        val intent = Intent(this, OptionsActivity::class.java)
        startActivityForResult(intent, INTENT_START_TRAINING)
    }

    /*
    * button action on long click when not logged
    * */
    private fun guestButtonLongClick(_button: View): Boolean {
        goToRegisterView(this)
        return true
    }

    /*
    * represents the action to go to the register activity
    * */
    private fun goToRegisterView(mainActivity: MainActivity) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivityForResult(intent, INTENT_STATE_CHANGE)
    }

    /*
    * button action on long click when not sure if logged or not
    * */
    private fun initialButtonLongClick(button: View): Boolean {
        Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show()
        return true
    }

    /*
    * represents the decision tree when the main button is clicked
    * */
    fun mainButtonClick(button: View) {
        when (StateController.state) {
            State.Initial -> initialButtonClick(button)
            State.GUEST -> guestButtonClick(button)
            State.LOGGED -> loggedButtonClick(button)
        }
    }

    /*
    * button action on click when not sure if logged or not
    * */
    private fun initialButtonClick(button: View) {
        Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show()
    }

    private fun guestButtonClick(button: View) {
        goToLoginView(this)
    }

    /*
    * button action on click when not logged
    * */
    private fun goToLoginView(mainActivity: MainActivity) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, INTENT_STATE_CHANGE)
    }

    /*
    * button action on click when logged
    * */
    private fun loggedButtonClick(button: View) {
        //StateController.cleanTraining()
        StateController.exercise = null
        val trainings = Controller.availableTrainings
        val training = ContextEngine.decideBestTraining(trainings)
        if (training == null) {
            Toast.makeText(this, R.string.training_not_available, Toast.LENGTH_SHORT).show()
            return
        }
        executeTraining(training)
    }

    /*
    * action to execute the training
    * */
    private fun executeTraining(training: Training) {
        requestPermissions()

        if(StateController.training == null) {
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
        Controller.notifyExerciseComplete(this, exercise.id.toString(), when(exercise.percentage) {
            100 -> "normal"
            in 101..Int.MAX_VALUE -> "easy"
            else -> "hard"
        })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == INTENT_STATE_CHANGE) {
            evaluateLoginStatus()
            return
        }
        if (requestCode == INTENT_START_TRAINING) {
            if(!data!!.getBooleanExtra("done", false)){
                binding.buttonMain.visibility = View.VISIBLE
                binding.buttonMain.isClickable = true
            }
            binding.trainingName.text = data.getStringExtra("trainingName")

            val trainingID = data!!.getIntExtra("trainingID", 0)
            if (trainingID == 0) {
                return
            }
            try {
                executeTraining(Controller.findByIdInAvailableTrainings(trainingID))
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

    private class SetupTask internal constructor(activity: MainActivity) :
        AsyncTask<Void?, Void?, Exception?>() {
        var activityReference: WeakReference<MainActivity> = WeakReference(activity)
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
            } catch (e: IOException) {}
        }
    }

    /*
    * represents the stop the microphone recognition service
    * */
    private fun stopRecognizeMicrophone() {
            try {
                recognizer?.cancel()
                recognizer = null

            } catch (e: IOException) {}
    }

    /*
    * represents the event of receiving an phrase result from the microphone recognition service
    * includes send the phrase to the context engine for processing and redraw the UI
    * */
    override fun onResult(p0: String?) {
        if(p0 == null) return

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

