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



class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    private val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    val SHARED_PREFERENCES = "Shared"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
/*        val button = binding.buttonMain
        button.visibility = View.INVISIBLE
        button.isClickable = false*/
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
        requestPermissions()

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

/*    *//*
    * represents the decision tree when the main button is clicked
    * *//*
    fun mainButtonClick(button: View) {
        when (StateController.state) {
            State.Initial -> initialButtonClick(button)
            State.GUEST -> guestButtonClick(button)
            State.LOGGED -> loggedButtonClick(button)
        }
    }*/

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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == INTENT_STATE_CHANGE) {
            evaluateLoginStatus()
            return
        }
        /*if (requestCode == INTENT_START_TRAINING) {
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
        }*/

    }


}

