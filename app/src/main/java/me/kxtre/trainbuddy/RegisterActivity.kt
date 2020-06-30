package me.kxtre.trainbuddy

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.AuthenticationController
import me.kxtre.trainbuddy.databinding.ActivityRegisterBinding
import me.kxtre.trainbuddy.interfaces.Callback

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
    }

    fun onRegisterClick(v: View) {
        val email = binding.editEmail.text.toString()
        val password =  binding.editPassword.text.toString()
        val pass_confirm = binding.editPasswordConfirmation.text.toString()
        val name = binding.editName.text.toString()
        val birth = binding.editBirthDate.text.toString()
        val height = binding.editHeight.text.toString()
        val weight = binding.editWeight.text.toString()
        val gender = when(binding.genderSpinner.getSelectedItem().toString() == "Male") {
             true-> "M"
             false -> "F"
        }

        if(password != pass_confirm) {
            Toast.makeText(applicationContext,"Passwords don't match", Toast.LENGTH_SHORT).show()
            return
        }
        AuthenticationController.register(email, password, pass_confirm, name, birth, height, weight, gender, object:
            Callback {
            override fun onSucess() {
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onError() {
                //Toast.makeText(applicationContext,"Register Failed", Toast.LENGTH_SHORT).show()
            }

        }, this)
    }
}
