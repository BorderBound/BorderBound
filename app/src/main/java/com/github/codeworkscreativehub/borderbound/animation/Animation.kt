package com.github.codeworkscreativehub.borderbound.animation

import com.github.codeworkscreativehub.borderbound.`object`.Drawable

abstract class Animation(
    val subject: Drawable,
    val delay: Int
) {
    var isRunning: Boolean = false
        private set
    var shouldBeDeleted: Boolean = false
        private set
    private var timeStarted: Long = 0

    init {
        subject.addAnimation(this)
    }

    private fun timeSinceStarted(): Long {
        return System.currentTimeMillis() - timeStarted
    }

    open fun start() {
        isRunning = true
        timeStarted = System.currentTimeMillis()
        this.shouldBeDeleted = false
    }

    fun pause() {
        isRunning = false
    }

    fun destroy() {
        isRunning = false
        this.shouldBeDeleted = true
    }

    internal open fun restart() {
        isRunning = true
        timeStarted = System.currentTimeMillis()
    }

    internal abstract fun tick(durationRunning: Long)

    fun tick() {
        if (!isRunning) {
            return
        }

        if (timeSinceStarted() > delay) {
            tick(timeSinceStarted() - delay)
        }
    }

    companion object {
        const val DURATION_LONG = 400
        const val DURATION_SHORT = 200
    }
}
