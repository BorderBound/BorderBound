package com.github.codeworkscreativehub.borderbound.filler

import com.github.codeworkscreativehub.borderbound.model.Level
import com.github.codeworkscreativehub.borderbound.model.Modifier
import com.github.codeworkscreativehub.borderbound.state.State

abstract class Filler {
    private var onFinished: Runnable? = null
    private var lastAction: Long = 0

    abstract fun fill()

    fun runOnFinished() {
        onFinished?.run()
    }

    protected fun sleep(milliseconds: Long) {
        val timePassed = System.currentTimeMillis() - lastAction
        var timeLeft = milliseconds - timePassed
        if (lastAction == 0L) {
            timeLeft = milliseconds
        }
        lastAction = System.currentTimeMillis()
        if (timeLeft <= 0) {
            return
        }
        try {
            Thread.sleep(timeLeft)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun setOnFinished(onFinished: Runnable) {
        this.onFinished = onFinished
    }

    companion object {
        fun get(levelData: Level, col: Int, row: Int, state: State): Filler? {
            return when (levelData.fieldAt(col, row).modifier) {
                Modifier.FLOOD -> FloodFiller(levelData, col, row, state)
                Modifier.BOMB -> BombFiller(levelData, col, row, state)
                Modifier.UP, Modifier.ROTATE_UP -> DirectionFiller(levelData, col, row, 0, -1, state)
                Modifier.RIGHT, Modifier.ROTATE_RIGHT -> DirectionFiller(levelData, col, row, 1, 0, state)
                Modifier.LEFT, Modifier.ROTATE_LEFT -> DirectionFiller(levelData, col, row, -1, 0, state)
                Modifier.DOWN, Modifier.ROTATE_DOWN -> DirectionFiller(levelData, col, row, 0, 1, state)
                else -> null
            }
        }
    }
}
