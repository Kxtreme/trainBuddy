package me.kxtre.trainbuddy.controllers

import me.kxtre.trainbuddy.models.Training
import java.util.*

object Controller {
    fun findByIdInAvailableTrainings(trainingID: Int): Training {
        return availableTrainings.find { it.id == trainingID }!!
    }

    val availableTrainings = LinkedList<Training>()
}