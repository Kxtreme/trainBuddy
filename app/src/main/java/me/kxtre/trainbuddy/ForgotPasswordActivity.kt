package me.kxtre.trainbuddy

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.AuthenticationController
import me.kxtre.trainbuddy.databinding.ActivityForgotPasswordBinding
import me.kxtre.trainbuddy.interfaces.Callback

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
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onError() {
                //Toast.makeText(applicationContext,"Request Password Failed", Toast.LENGTH_SHORT).show()
            }

        }, this)
    }
}