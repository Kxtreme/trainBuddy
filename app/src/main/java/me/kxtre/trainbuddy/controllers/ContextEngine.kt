package me.kxtre.trainbuddy.controllers

import android.util.Log
import me.kxtre.trainbuddy.models.Training

object ContextEngine {
    fun decideBestTraining(trainings: List<Training>): Training? {
        return trainings[0]

    }

    fun realizeAction(action: String?) {
        Log.d("recognized", action)
    }

}
