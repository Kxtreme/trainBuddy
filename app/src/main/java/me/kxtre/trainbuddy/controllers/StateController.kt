package me.kxtre.trainbuddy.controllers

import me.kxtre.trainbuddy.models.State

object StateController {
    val INTENT_STATE_CHANGE = 1000
    val INTENT_START_TRAINING = 1001
    fun changeState(state: State) {
        this.state = state
    }
    var state = State.Initial
}
