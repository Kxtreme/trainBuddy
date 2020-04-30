package me.kxtre.trainbuddy.controllers

import me.kxtre.trainbuddy.models.State

object StateController {
    fun changeState(state: State) {
        this.state = state
    }
    var state = State.Initial
}
