package me.kxtre.trainbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.AuthenticationController
import me.kxtre.trainbuddy.controllers.StateController
import me.kxtre.trainbuddy.databinding.ActivityRegisterBinding
import me.kxtre.trainbuddy.interfaces.Callback

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
    }
    fun onLoginClick(v: View) {
        val email = binding.editTextLogin.text.toString()
        val password =  binding.editTextPassword.text.toString()
        val pass_confirm = binding.editTextConfirm.text.toString()
        val name = binding.editTextName.text.toString()
        val birth = binding.editTextBirthDay.text.toString()
        val height = binding.editTextHeight.text.toString().toInt()
        val weight = binding.editTextWeight.text.toString().toInt()

        if(password != pass_confirm) {
            Toast.makeText(applicationContext,"Passwords don't match", Toast.LENGTH_SHORT).show()
            return
        }
        AuthenticationController.register(email, password, name, birth, height, weight, object:
            Callback {
            override fun onSucess() {
                setResult(StateController.INTENT_STATE_CHANGE)
                finish()
            }

            override fun onError() {
                Toast.makeText(applicationContext,"Register Failed", Toast.LENGTH_SHORT).show()
            }

        }, this)
    }
}
