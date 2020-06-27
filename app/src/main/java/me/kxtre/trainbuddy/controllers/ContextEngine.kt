package me.kxtre.trainbuddy.controllers

import android.util.Log
import me.kxtre.trainbuddy.models.Training

object ContextEngine {

    /*
    * represents the automatic decision of the training to be done
    * */
    fun decideBestTraining(trainings: List<Training>): Training? {
        return trainings.filter { !it.done }.getOrNull(0)

    }

    /*
    * represents the receiving of actions to be done to the current exercise
    * includes separate the phrase by word and try to execute the action on the current exercise
    * */
    fun sendInstructions(action: String?) {
        Log.d("actions", action)
        parseStringToWords(action).forEach { tryRealizeAction(it) }
    }

    /*
    * represents the call of the action based on the key it will try to match to an available action
    * */
    private fun tryRealizeAction(action: String) {
        actions[action]?.invoke()
    }

    /*
    * represents the splitting of an phrase into an collection of words
    * */
    private fun parseStringToWords(string: String?): List<String> {
        if (string == null || string == "") return emptyList()
        return string.split(" ")
    }

    /*
    * represents the available actions for the exercise
    * represented by a map of key -> lambda function
    * */
    val actions:Map<String, () -> Unit?> = mapOf(
        Pair("easy", {
            StateController.exercise?.addToPercentage(10)
        }),
        Pair("hard", {
            StateController.exercise?.addToPercentage(-10)
        })
    )
}
