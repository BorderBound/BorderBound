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

class MainMenuState private constructor() : State() {

    private var nextState: State = this
    private lateinit var logo: Plane
    private lateinit var startButton: Plane
    private lateinit var settingsButton: Plane
    private lateinit var exitButton: Plane

    override fun initialize(renderer: GLRenderer) {
        val coordinatesLogo = TextureCoordinates.getFromBlocks(0, 0, 6, 2)
        val logoHeight = renderer.getWidth() / 3
        logo = Plane(0f, renderer.getHeight(), renderer.getWidth(), logoHeight, coordinatesLogo)
        renderer.addDrawable(logo)

        val menuEntriesWidth = renderer.getWidth() * 0.75f
        val menuEntriesHeight = menuEntriesWidth / 6
        val menuEntriesAvailableSpace = getScreenHeight() - logoHeight
        val menuEntriesStartY = getScreenHeight() - logoHeight - (menuEntriesAvailableSpace - 4 * menuEntriesHeight) / 2

        val coordinatesStart = TextureCoordinates.getFromBlocks(0, 2, 6, 3)
        startButton = Plane(-menuEntriesWidth, menuEntriesStartY, menuEntriesWidth, menuEntriesHeight, coordinatesStart)
        renderer.addDrawable(startButton)

        val coordinatesSettings = TextureCoordinates.getFromBlocks(0, 3, 6, 4)
        settingsButton = Plane(-menuEntriesWidth, startButton.y - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesSettings)
        renderer.addDrawable(settingsButton)

        val coordinatesExit = TextureCoordinates.getFromBlocks(0, 4, 6, 5)
        exitButton = Plane(-menuEntriesWidth, settingsButton.y - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesExit)
        renderer.addDrawable(exitButton)
    }

    override fun entry() {
        nextState = this

        logo.y = getScreenHeight()
        val logoAnimation = TranslateAnimation(logo, Animation.DURATION_LONG, Animation.DURATION_SHORT)
        logoAnimation.setTo(0f, getScreenHeight() - logo.height)
        logoAnimation.start()

        AnimationFactory.startMenuAnimationEnter(startButton, (2.0f * Animation.DURATION_SHORT).toInt())
        AnimationFactory.startMenuAnimationEnter(settingsButton, (2.5f * Animation.DURATION_SHORT).toInt())
        AnimationFactory.startMenuAnimationEnter(exitButton, (3.0f * Animation.DURATION_SHORT).toInt())
    }

    override fun exit() {
        val logoAnimation = TranslateAnimation(logo, Animation.DURATION_SHORT, 0)
        logoAnimation.setTo(0f, getScreenHeight())
        logoAnimation.start()

        if (nextState === LevelPackSelectState.getInstance() || nextState === TutorialState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(startButton)
        } else {
            AnimationFactory.startMenuAnimationOut(startButton)
        }

        if (nextState === SettingsState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(settingsButton)
        } else {
            AnimationFactory.startMenuAnimationOut(settingsButton)
        }

        if (nextState === ExitState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(exitButton)
        } else {
            AnimationFactory.startMenuAnimationOut(exitButton)
        }
    }

    override fun next(): State {
        return nextState
    }

    override fun onBackPressed() {
        nextState = ExitState.getInstance()
        playSound(R.raw.click)
    }

    override fun onTouchEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (startButton.collides(event, getScreenHeight())) {
                playSound(R.raw.click)
                if (isSolved(0)) {
                    nextState = LevelPackSelectState.getInstance()
                } else {
                    nextState = TutorialState.getInstance()
                }
            } else if (settingsButton.collides(event, getScreenHeight())) {
                nextState = SettingsState.getInstance()
                playSound(R.raw.click)
            } else if (exitButton.collides(event, getScreenHeight())) {
                nextState = ExitState.getInstance()
                playSound(R.raw.click)
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: MainMenuState? = null

        fun getInstance(): MainMenuState {
            if (instance == null) {
                instance = MainMenuState()
            }
            return instance!!
        }
    }
}
