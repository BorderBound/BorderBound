package com.github.codeworkscreativehub.borderbound.animation

import com.github.codeworkscreativehub.borderbound.`object`.Drawable

class TranslateAnimation(
    mesh: Drawable,
    duration: Int,
    startIn: Int
) : AnimationSingle(mesh, duration, startIn) {

    private var fromX: Float = 0f
    private var fromY: Float = 0f
    private var toX: Float = 0f
    private var toY: Float = 0f
    private var hideAfter = false

    fun setTo(x: Float, y: Float): TranslateAnimation {
        this.toX = x
        this.toY = y
        return this
    }

    fun setHideAfter(hideAfter: Boolean): TranslateAnimation {
        this.hideAfter = hideAfter
        return this
    }

    override fun tick(percentage: Double) {
        subject.x = (fromX + (toX - fromX) * percentage).toFloat()
        subject.y = (fromY + (toY - fromY) * percentage).toFloat()
    }

    override fun firstTick() {
        this.fromX = subject.x
        this.fromY = subject.y
    }

    override fun finalTick() {
        subject.x = toX
        subject.y = toY

        if (hideAfter) {
            subject.isVisible = false
        }
    }

    override fun reverse(): TranslateAnimation {
        val reversed = TranslateAnimation(subject, duration, delay)
        reversed.setTo(fromX, fromY)
        return reversed
    }
}
