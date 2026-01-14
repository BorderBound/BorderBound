package com.github.codeworkscreativehub.borderbound.state

import android.annotation.SuppressLint
import android.view.MotionEvent
import com.github.codeworkscreativehub.borderbound.GLRenderer
import com.github.codeworkscreativehub.borderbound.R
import com.github.codeworkscreativehub.borderbound.animation.Animation
import com.github.codeworkscreativehub.borderbound.animation.AnimationFactory
import com.github.codeworkscreativehub.borderbound.animation.TranslateAnimation
import com.github.codeworkscreativehub.borderbound.`object`.Plane
import com.github.codeworkscreativehub.borderbound.`object`.TextureCoordinates

class TutorialState private constructor() : State() {

    private var nextState: State = this
    private lateinit var logo: Plane
    private lateinit var screen1: Plane
    private lateinit var screen2: Plane
    private var screenNumber = 1

    override fun initialize(renderer: GLRenderer) {
        val coordinatesLogo = TextureCoordinates.getFromBlocks(6, 4, 11, 6)
        val logoHeight = renderer.getWidth() * (2f / 5f)
        logo = Plane(0f, renderer.getHeight(), renderer.getWidth(), logoHeight, coordinatesLogo)
        renderer.addDrawable(logo)

        val tutScreenWidth = renderer.getWidth() * (5f / 6f)
        val tutScreenHeight = tutScreenWidth * (4f / 5f)
        val tutScreenX = renderer.getWidth() * (1f / 12f)
        val tutScreenY = renderer.getHeight() - logoHeight - (renderer.getHeight() + tutScreenHeight - logoHeight) / 2
        val coordinatesScreen1 = TextureCoordinates.getFromBlocks(6, 6, 11, 10)
        screen1 = Plane(tutScreenX, tutScreenY, tutScreenWidth, tutScreenHeight, coordinatesScreen1)
        screen1.scale = 0f
        screen1.isVisible = false
        renderer.addDrawable(screen1)

        val coordinatesScreen2 = TextureCoordinates.getFromBlocks(11, 4, 16, 8)
        screen2 = Plane(tutScreenX, tutScreenY, tutScreenWidth, tutScreenHeight, coordinatesScreen2)
        screen2.scale = 0f
        screen2.isVisible = false
        renderer.addDrawable(screen2)
    }

    override fun entry() {
        nextState = this

        if (screenNumber == 1) {
            logo.y = getScreenHeight()
            val logoAnimation = TranslateAnimation(logo, Animation.DURATION_LONG, Animation.DURATION_SHORT)
            logoAnimation.setTo(0f, getScreenHeight() - logo.height)
            logoAnimation.start()
        }

        if (screenNumber == 1) {
            AnimationFactory.startScaleShow(screen1)
        } else {
            AnimationFactory.startScaleShow(screen2, 0)
        }
    }

    override fun exit() {
        if (screenNumber == 3) {
            screenNumber = 2
            val logoAnimation = TranslateAnimation(logo, Animation.DURATION_SHORT, 0)
            logoAnimation.setTo(0f, getScreenHeight())
            logoAnimation.start()
        }

        if (screenNumber == 1) {
            AnimationFactory.startScaleHide(screen1, 0)
        } else {
            AnimationFactory.startScaleHide(screen2)
            screenNumber = 1
        }

        if (nextState === MainMenuState.getInstance()) {
            screenNumber = 1
            val logoAnimation = TranslateAnimation(logo, Animation.DURATION_SHORT, 0)
            logoAnimation.setTo(0f, getScreenHeight())
            logoAnimation.start()
        }
    }

    override fun next(): State {
        return nextState
    }

    override fun onBackPressed() {
        nextState = MainMenuState.getInstance()
    }

    override fun onTouchEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (screenNumber == 1) {
                playSound(R.raw.click)
                exit()
                screenNumber = 2
                entry()
            } else {
                screenNumber = 3
                playSound(R.raw.click)
                nextState = LevelPackSelectState.getInstance()
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: TutorialState? = null

        fun getInstance(): TutorialState {
            if (instance == null) {
                instance = TutorialState()
            }
            return instance!!
        }
    }
}
