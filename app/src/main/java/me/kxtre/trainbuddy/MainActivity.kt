package me.kxtre.trainbuddy

import android.os.Bundle
import android.view.View
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
        var button = binding.buttonMain
        AuthenticationController.checkAuthentication(this, object : Callback {

            override fun onSucess() {
                StateController.changeState(State.LOGGED)
                button.text = getString(R.string.start_training)
            }

            override fun onError() {
                StateController.changeState(State.GUEST)
                button.text = getString(R.string.login)
            }
        })



    }

    fun mainButtonClick(button: View) {
        when(StateController.state) {
            State.Initial -> initialButtonClick(button)
            State.GUEST -> guestButtonClick(button)
            State.LOGGED -> loggedButtonClick(button)
        }
    }

    private fun initialButtonClick(button: View) {
        TODO("Not yet implemented")
    }

    private fun guestButtonClick(button: View) {
        TODO("Not yet implemented")
    }

    private fun loggedButtonClick(button: View) {
        TODO("Not yet implemented")
        /*List<Training> trainings = getAvailableTrainings(button)
        Training training = decideBestTraining(button, trainings)
        executeTraining(button, training)*/
    }

}
