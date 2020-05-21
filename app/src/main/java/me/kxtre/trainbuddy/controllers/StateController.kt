package me.kxtre.trainbuddy.controllers

import me.kxtre.trainbuddy.models.Exercise
import me.kxtre.trainbuddy.models.State
import me.kxtre.trainbuddy.models.Training

object StateController {
    val INTENT_STATE_CHANGE = 1000
    val INTENT_START_TRAINING = 1001
    var training: Training? = null
    var exercise: Exercise? = null
    fun changeState(state: State) {
        this.state = state
    }
    var state = State.Initial
}
