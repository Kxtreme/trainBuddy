package me.kxtre.trainbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.AuthenticationController
import me.kxtre.trainbuddy.controllers.StateController.INTENT_STATE_CHANGE
import me.kxtre.trainbuddy.databinding.ActivityLoginBinding
import me.kxtre.trainbuddy.interfaces.Callback

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
    }

    fun onLoginClick(v: View) {
        AuthenticationController.login(binding.editTextLogin.text.toString(), binding.editTextPassword.text.toString(), object: Callback {
            override fun onSucess() {
                setResult(INTENT_STATE_CHANGE)
                finish()
            }

            override fun onError() {
                Toast.makeText(applicationContext,"Login Failed",Toast.LENGTH_SHORT).show()
            }

        }, this)
    }
}
