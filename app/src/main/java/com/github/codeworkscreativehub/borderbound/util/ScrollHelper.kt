package com.github.codeworkscreativehub.borderbound.util

import android.view.MotionEvent
import com.github.codeworkscreativehub.borderbound.`object`.Drawable
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ScrollHelper(
    private val drawable: Drawable,
    private val horizontal: Boolean,
    private val vertical: Boolean
) {
    private var downX = 0f
    private var downY = 0f
    private var oldX = 0f
    private var oldY = 0f
    private var minX = 0f
    private var minY = 0f
    private var maxX = 0f
    private var maxY = 0f

    var isScrolling = false
        private set

    private var pressed = false

    fun setMaxima(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        this.minX = minX
        this.minY = minY
        this.maxX = maxX
        this.maxY = maxY
    }

    fun clampX(pos: Float): Float {
        return max(min(pos, maxX), minX)
    }

    fun clampY(pos: Float): Float {
        return max(min(pos, maxY), minY)
    }

    private fun setChildX(pos: Float) {
        drawable.x = clampX(pos)
    }

    private fun setChildY(pos: Float) {
        drawable.y = clampY(pos)
    }

    private fun reset() {
        isScrolling = false
        pressed = false
    }

    fun onTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                oldX = drawable.x
                oldY = drawable.y
                pressed = true
            }

            MotionEvent.ACTION_MOVE -> if (pressed) {
                val currentX = event.x
                val currentY = event.y

                // Fixed potential bug: changed && to || to allow scrolling in single direction
                if (abs(downX - currentX) > MIN_DISTANCE || abs(downY - currentY) > MIN_DISTANCE) {
                    isScrolling = true
                }

                if (isScrolling) {
                    if (horizontal) {
                        val delta = downX - currentX
                        setChildX(oldX + delta)
                    }
                    if (vertical) {
                        val delta = downY - currentY
                        setChildY(oldY + delta)
                    }
                }
            }

            MotionEvent.ACTION_UP -> reset()
        }
    }

    companion object {
        private const val MIN_DISTANCE = 5f
    }
}
