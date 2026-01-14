package com.github.codeworkscreativehub.borderbound.animation

import com.github.codeworkscreativehub.borderbound.`object`.Drawable

class ScaleAnimation(
    mesh: Drawable,
    duration: Int,
    startIn: Int
) : AnimationSingle(mesh, duration, startIn) {

    private var from: Float = 0f
    private var to: Float = 0f
    private var hideAfter = false

    fun setTo(to: Float): ScaleAnimation {
        this.to = to
        return this
    }

    fun setHideAfter(hideAfter: Boolean): ScaleAnimation {
        this.hideAfter = hideAfter
        return this
    }

    override fun firstTick() {
        this.from = subject.scale
    }

    override fun tick(percentage: Double) {
        subject.scale = (from + (to - from) * percentage).toFloat()
    }

    override fun finalTick() {
        subject.scale = to

        if (hideAfter) {
            subject.isVisible = false
        }
    }

    override fun reverse(): ScaleAnimation {
        val reversed = ScaleAnimation(subject, duration, delay)
        reversed.setTo(from)
        return reversed
    }
}
