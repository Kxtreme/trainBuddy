package me.kxtre.trainbuddy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.kxtre.trainbuddy.adapters.TrainingsAdapter
import me.kxtre.trainbuddy.controllers.AuthenticationController
import me.kxtre.trainbuddy.controllers.Controller
import me.kxtre.trainbuddy.databinding.ActivityTrainingBinding
import me.kxtre.trainbuddy.interfaces.Callback
import me.kxtre.trainbuddy.models.Training


class TrainingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training)
        configureActionBar()

        setAdapters()
        registerListeners()
    }

    private fun registerListeners() {
        binding.listviewTrainings.setOnItemClickListener { adapterView, view, i, l ->
            val training =  adapterView.getItemAtPosition(i) as Training
            val intent = Intent(applicationContext, ExerciseActivity::class.java)
            intent.putExtra("trainingID", training.id)
            intent.putExtra("trainingName", training.name)

            if(training.done) {
                intent.putExtra("done", true)
            } else {
                intent.putExtra("done", false)
            }
            startActivity(intent)
        }
    }
    private fun setAdapters() {
        Controller.availableTrainings.let {
            val trainingsAdapter = TrainingsAdapter(
                this,
                R.layout.list_item_training,
                it
            )
            binding.listviewTrainings.adapter = trainingsAdapter
        }
    }

    fun configureActionBar() {
        val actionBar: ActionBar? = null
        actionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_1 -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true
            }
            R.id.option_2 -> {
                AuthenticationController.logout(object:
                    Callback {
                    override fun onSucess() {
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                        finish()
                    }
                    override fun onError() {
                        Toast.makeText(applicationContext,"Logout Failed", Toast.LENGTH_SHORT).show()
                    }

                }, this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //exitProcess(0)
        finishAffinity()
    }
}