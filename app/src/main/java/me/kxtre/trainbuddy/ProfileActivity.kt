package me.kxtre.trainbuddy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.controllers.AuthenticationController
import me.kxtre.trainbuddy.controllers.DataManager
import me.kxtre.trainbuddy.databinding.ActivityProfileBinding
import me.kxtre.trainbuddy.interfaces.Callback
import me.kxtre.trainbuddy.models.User
import me.kxtre.trainbuddy.utils.HttpCallBack
import me.kxtre.trainbuddy.utils.HttpUtils
import org.json.JSONException
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        getUser(object: Callback {
            override fun onSucess() {
                binding.textName.text = user!!.name
                binding.textEmail.text = user!!.email
                binding.textBirthDate.text = user!!.bornDate
                when(user!!.gender == "M") {
                    true-> binding.textGender.text = "Male"
                    false -> binding.textGender.text = "Female"
                }

                binding.editWeight.setText(user!!.weight)
                binding.editHeight.setText(user!!.height)
            }

            override fun onError() {
                //Toast.makeText(applicationContext,"Request Password Failed", Toast.LENGTH_SHORT).show()
            }
        }, this)
    }

    fun onSaveClick(v: View) {
        AuthenticationController.editProfile(binding.editHeight.text.toString(), binding.editWeight.text.toString(), object: Callback {
            override fun onSucess() {
                Toast.makeText(applicationContext, "Edit Profile Success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, TrainingActivity::class.java))
                finish()
            }

            override fun onError() {
                //Toast.makeText(applicationContext,"Edit Profile Failed",Toast.LENGTH_SHORT).show()
            }

        }, this)
    }

    private fun getUser(callback: Callback, context: Context) {
        val headers = AuthenticationController.generateAuthenticationHeaders(DataManager.INSTANCE.storedUserJWT)
        HttpUtils.Get(object : HttpCallBack {
            @Throws(JSONException::class)
            override fun onResult(response: JSONObject) {
                user = User.parseUser(response.toString())
                callback.onSucess()
            }

            override fun onResult(response: String) {
                callback.onError()
            }

            override fun onFail(error: String) {
                callback.onError()
            }
        }, DataManager.INSTANCE.mainURL + "/api/user", context, true, headers)
    }

    fun onRollClick(v: View) {
        val user = DataManager.INSTANCE.getUser()
        binding.editBirthDate.setText(user.bornDate)
        binding.editEmail.setText(user.email)
        binding.editHeight.setText(user.height)
        binding.editName.setText(user.name)
    }

}