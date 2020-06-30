package me.kxtre.trainbuddy

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
        AuthenticationController.login(binding.editEmail.text.toString(), binding.editPassword.text.toString(), object: Callback {
            override fun onSucess() {
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onError() {
                //Toast.makeText(applicationContext,"Login Failed",Toast.LENGTH_SHORT).show()
            }

        }, this)
    }

    fun goToRegister(v: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivityForResult(intent, INTENT_STATE_CHANGE)
    }

    fun onResetPasswordClick(v: View) {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivityForResult(intent, INTENT_STATE_CHANGE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
