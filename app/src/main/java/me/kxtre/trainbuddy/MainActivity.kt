package me.kxtre.trainbuddy

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.AuthenticationController
import me.kxtre.trainbuddy.controllers.StateController
import me.kxtre.trainbuddy.databinding.ActivityMainBinding
import me.kxtre.trainbuddy.interfaces.Callback
import me.kxtre.trainbuddy.models.State

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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

        button.setOnLongClickListener { _button -> when(StateController.state) {
            State.Initial -> initialButtonLongClick(_button)
            State.GUEST -> guestButtonLongClick(_button)
            State.LOGGED -> loggedButtonLongClick(_button)
        } }


    }

    private fun loggedButtonLongClick(_button: View): Boolean {
        goToMoreOptionsView(this)
    }

    private fun guestButtonLongClick(_button: View): Boolean {
        goToRegisterView(this)
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

    private fun loggedButtonClick(button: View) {
        TODO("Not yet implemented")
        /*List<Training> trainings = getAvailableTrainings(button)
        Training training = decideBestTraining(button, trainings)
        executeTraining(button, training)*/
    }

}
