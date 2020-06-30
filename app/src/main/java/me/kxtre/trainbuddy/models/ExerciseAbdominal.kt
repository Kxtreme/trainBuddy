package me.kxtre.trainbuddy.models

import me.kxtre.trainbuddy.interfaces.BasicCallBack

class ExerciseAbdominal(ID: Int?, name: String?, repeats: Int?) :
    Exercise(ID, name, repeats) {
    private var countCallBack: BasicCallBack? = null
    private var isUp: Boolean? = null
    override fun registerCountMechanism(callBack: BasicCallBack) {
        countCallBack = callBack
    }

    override fun notifyAccelerometerChange(
        x: Float,
        y: Float,
        z: Float
    ) {
        if (countCallBack == null) return
        when (isUp) {
            null -> {
                checkUp(x, y, z)
                checkDown(x, y, z)
            }
            true -> {
                checkDown(x, y, z)
            }
            false -> checkUp(x, y, z)
        }
    }

    private fun checkUp(x: Float, y: Float, z: Float) {
        if(y > 5) {
            isUp = true
            addToProgress()
        }
    }
    private fun checkDown(x: Float, y: Float, z: Float) {
        if(x < -5 && y < -5) isUp = false
    }

    private fun addToProgress() {
        this.addToProgress(1)
        countCallBack?.onEvent()
    }
}