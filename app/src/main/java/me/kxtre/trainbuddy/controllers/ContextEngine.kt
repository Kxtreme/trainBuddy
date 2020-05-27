package me.kxtre.trainbuddy.controllers

import me.kxtre.trainbuddy.models.Training

object ContextEngine {
    fun decideBestTraining(trainings: List<Training>): Training? {
        return trainings[0]

    }

}
