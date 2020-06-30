package me.kxtre.trainbuddy

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.*
import me.kxtre.trainbuddy.controllers.StateController.INTENT_START_TRAINING
import me.kxtre.trainbuddy.controllers.StateController.INTENT_STATE_CHANGE
import me.kxtre.trainbuddy.databinding.ActivityMainBinding
import me.kxtre.trainbuddy.interfaces.Callback
import me.kxtre.trainbuddy.models.State


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    val SHARED_PREFERENCES = "Shared"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.trainingName.text = getString(R.string.retrieving_state)

        val sharedPreferences = getSharedPreferences(
            SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )

        DataManager.INSTANCE.sharedPreferences = sharedPreferences
        evaluateLoginStatus()

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
        AuthenticationController.checkAuthentication(this, object : Callback {

            override fun onSucess() {
                StateController.changeState(State.LOGGED)
                goToTrainingList(this@MainActivity)
            }

            override fun onError() {
                StateController.changeState(State.Initial)
                goToLoginView(this@MainActivity)
            }
        })
    }

    private fun goToTrainingList(mainActivity: MainActivity) {
        val intent = Intent(this, TrainingActivity::class.java)
        startActivityForResult(intent, INTENT_START_TRAINING)
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

