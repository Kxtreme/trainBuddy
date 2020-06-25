package me.kxtre.trainbuddy.controllers

import android.util.Log
import me.kxtre.trainbuddy.models.Training

object ContextEngine {

    fun decideBestTraining(trainings: List<Training>): Training? {
        return trainings[0]

    }

    fun sendInstructions(action: String?) {
        parseStringToWords(action).forEach { tryRealizeAction(it) }
    }

    private fun tryRealizeAction(action: String) {
        actions[action]?.invoke()
    }

    private fun parseStringToWords(string: String?): List<String> {
        if (string == null || string == "") return emptyList()
        return string.split(" ")
    }

    val actions:Map<String, () -> Unit?> = mapOf(
        Pair("easy", {
            StateController.exercise?.addToPercentage(10)
        })
    )
}
