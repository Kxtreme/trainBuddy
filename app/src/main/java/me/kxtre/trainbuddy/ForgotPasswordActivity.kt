package me.kxtre.trainbuddy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.AuthenticationController
import me.kxtre.trainbuddy.interfaces.Callback
import me.kxtre.trainbuddy.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
    }

    fun onResetPasswordClick(v: View) {
        val email = binding.editEmail.text.toString()

        AuthenticationController.forgotPassword(email, object: Callback {
            override fun onSucess() {
                //TODO
            }

            override fun onError() {
                //TODO
            }

        }, this)
    }
}