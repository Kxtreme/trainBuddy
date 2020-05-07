package me.kxtre.trainbuddy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.*
import me.kxtre.trainbuddy.databinding.ActivityMainBinding
import me.kxtre.trainbuddy.interfaces.Callback
import me.kxtre.trainbuddy.models.State

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val SHARED_PREFERENCES = "Shared"
    val INTENT_STATE_CHANGE = 1000
    val INTENT_START_TRAINING = 1001
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
                executeTraining(Controller.findByIdInAvailableTrainings(trainingID))
            } catch (e: Error) {
                Toast.makeText(this, R.string.training_not_available, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
