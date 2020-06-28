package me.kxtre.trainbuddy.controllers

import android.content.Context
import me.kxtre.trainbuddy.controllers.AuthenticationController.generateAuthenticationHeaders
import me.kxtre.trainbuddy.interfaces.Callback
import me.kxtre.trainbuddy.models.Training
import me.kxtre.trainbuddy.utils.HttpCallBack
import me.kxtre.trainbuddy.utils.HttpUtils
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object Controller {

    fun findByIdInAvailableTrainings(trainingID: Int): Training {
        return availableTrainings.find { it.id == trainingID }!!
    }

    fun fetchTrainings(context: Context, callback: Callback) {
        val headers =
            generateAuthenticationHeaders(DataManager.INSTANCE.storedUserJWT)
        HttpUtils.Get(object : HttpCallBack {
            @Throws(JSONException::class)
            override fun onResult(response: JSONObject) {
                availableTrainings = Training.parseResponse(response).toList()
                callback.onSucess()
            }

            override fun onResult(response: String) {
                callback.onError()
            }

            override fun onFail(error: String) {
                callback.onError()
            }
        }, DataManager.INSTANCE.mainURL + "/api/trainings", context, true, headers)
    }

    fun notifyTrainingComplete(context: Context, id: String) {
        val headers =
            generateAuthenticationHeaders(DataManager.INSTANCE.storedUserJWT)
        HttpUtils.Get(object : HttpCallBack {
            @Throws(JSONException::class)
            override fun onResult(response: JSONObject) {
            }

            override fun onResult(response: String) {
            }

            override fun onFail(error: String) {
            }
        }, DataManager.INSTANCE.mainURL + "/api/trainings/"+id+"/finished", context, false, headers)
    }

    fun notifyExerciseComplete(context: Context, id: String, state: String) {
        val headers =
            generateAuthenticationHeaders(DataManager.INSTANCE.storedUserJWT)
        HttpUtils.Get(object : HttpCallBack {
            @Throws(JSONException::class)
            override fun onResult(response: JSONObject) {
            }

            override fun onResult(response: String) {
            }

            override fun onFail(error: String) {
            }
        }, DataManager.INSTANCE.mainURL + "/api/trainings/"+id+"/"+state, context, false, headers)
    }

    fun cleanTrainings() {
        availableTrainings.forEach { it.exercises.forEach { it.reset() } }
    }

    var availableTrainings = emptyList<Training>()
}