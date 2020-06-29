package me.kxtre.trainbuddy

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.AuthenticationController
import me.kxtre.trainbuddy.controllers.DataManager
import me.kxtre.trainbuddy.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
    }

    fun onSaveClick(v: View) {
        //TODO
    }

    fun onRollClick(v: View) {
        val user = DataManager.INSTANCE.getUser()
        binding.editBirthDate.setText(user.bornDate)
        binding.editEmail.setText(user.email)
        binding.editHeight.setText(user.height)
        binding.editName.setText(user.name)
    }

}