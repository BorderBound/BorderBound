package com.github.codeworkscreativehub.borderbound.animation

import com.github.codeworkscreativehub.borderbound.`object`.Drawable

abstract class AnimationSingle(
    subject: Drawable,
    val duration: Int,
    startIn: Int
) : Animation(subject, startIn) {

    private var hadFirstTick = false

    abstract fun reverse(): AnimationSingle

    internal abstract fun tick(percentage: Double)
    internal abstract fun finalTick()

    internal open fun firstTick() {
        // To be overridden
    }

    override fun tick(durationRunning: Long) {
        if (!hadFirstTick) {
            hadFirstTick = true
            firstTick()
        }
        if (durationRunning > duration) {
            finalTick()
            this.destroy()
            return
        }
        tick(durationRunning.toDouble() / duration.toDouble())
    }
}
