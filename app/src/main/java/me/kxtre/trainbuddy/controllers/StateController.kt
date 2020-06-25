package me.kxtre.trainbuddy.controllers

import me.kxtre.trainbuddy.models.Exercise
import me.kxtre.trainbuddy.models.State
import me.kxtre.trainbuddy.models.Training

object StateController {
    val STATE_READY = 1
    val INTENT_STATE_CHANGE = 1000
    val INTENT_START_TRAINING = 1001
    var training: Training? = null
    var exercise: Exercise? = null
        set(value) {
            field = value
            if (value != null) {
                exercisesHistory.add(0, value)
            }
        }
    val exercisesHistory = mutableListOf<Exercise>()
    var isRecording = false;
    fun changeState(state: State) {
        this.state = state
    }

    fun cleanTraining() {
        exercisesHistory.clear()
        training?.exercises?.forEach {
            it.reset()
        }
    }

    var state = State.Initial
}
