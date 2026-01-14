package com.github.codeworkscreativehub.borderbound.animation

import com.github.codeworkscreativehub.borderbound.`object`.Drawable
import com.github.codeworkscreativehub.borderbound.`object`.Plane

object AnimationFactory {
    fun startMenuAnimationOutPressed(plane: Plane) {
        plane.cancelAnimations()
        val menuAnimation = TranslateAnimation(plane, Animation.DURATION_LONG, Animation.DURATION_SHORT)
        menuAnimation.setTo(-plane.width, plane.y)
        menuAnimation.setHideAfter(true)
        menuAnimation.start()
    }

    fun startMenuAnimationOut(plane: Plane) {
        plane.cancelAnimations()
        val menuAnimation = TranslateAnimation(plane, Animation.DURATION_SHORT, 0)
        menuAnimation.setTo(-plane.width, plane.y)
        menuAnimation.setHideAfter(true)
        menuAnimation.start()
    }

    fun startMenuAnimationEnter(plane: Plane, delay: Int) {
        plane.cancelAnimations()
        plane.isVisible = true
        val menuAnimation = TranslateAnimation(plane, Animation.DURATION_LONG, delay)
        menuAnimation.setTo(0f, plane.y)
        menuAnimation.start()
    }

    fun startScaleShow(plane: Plane) {
        startScaleShow(plane, Animation.DURATION_LONG)
    }

    fun startScaleShow(plane: Plane, delay: Int) {
        plane.cancelAnimations()
        plane.isVisible = true
        val leftAnimation = ScaleAnimation(plane, Animation.DURATION_LONG, delay)
        leftAnimation.setTo(1f)
        leftAnimation.start()
    }

    fun startScaleHide(plane: Plane) {
        startScaleHide(plane, Animation.DURATION_LONG)
    }

    fun startScaleHide(plane: Plane, delay: Int) {
        plane.cancelAnimations()
        val leftAnimation = ScaleAnimation(plane, Animation.DURATION_LONG, delay)
        leftAnimation.setTo(0f)
        leftAnimation.setHideAfter(true)
        leftAnimation.start()
    }

    fun startMoveYTo(plane: Drawable, toY: Float) {
        plane.cancelAnimations()
        plane.isVisible = true
        val leftAnimation = TranslateAnimation(plane, Animation.DURATION_LONG, Animation.DURATION_LONG)
        leftAnimation.setTo(plane.x, toY)
        leftAnimation.start()
    }
}
