package com.github.codeworkscreativehub.borderbound.state

import android.annotation.SuppressLint
import android.content.Intent
import android.view.MotionEvent
import androidx.core.content.edit
import androidx.core.net.toUri
import com.github.codeworkscreativehub.borderbound.GLRenderer
import com.github.codeworkscreativehub.borderbound.R
import com.github.codeworkscreativehub.borderbound.animation.Animation
import com.github.codeworkscreativehub.borderbound.animation.AnimationFactory
import com.github.codeworkscreativehub.borderbound.animation.ScaleAnimation
import com.github.codeworkscreativehub.borderbound.`object`.ObjectFactory
import com.github.codeworkscreativehub.borderbound.`object`.Plane
import com.github.codeworkscreativehub.borderbound.`object`.TextureCoordinates

class SettingsState private constructor() : State() {
    private var nextState: State = this

    private lateinit var volumeOff: Plane
    private lateinit var volumeOn: Plane
    private lateinit var tutorialButton: Plane
    private lateinit var volumeButton: Plane
    private lateinit var editorButton: Plane
    private lateinit var colorsExample: Plane
    private lateinit var colorsButton: Plane

    private var numberOfColorschemes: Int = 0
    private lateinit var glRenderer: GLRenderer

    override fun initialize(renderer: GLRenderer) {
        glRenderer = renderer

        numberOfColorschemes = glRenderer.numberOfColorschemes

        val menuEntriesWidth = glRenderer.getWidth() * 0.75f
        val menuEntriesHeight = menuEntriesWidth / 6
        val menuEntriesAvailableSpace = getScreenHeight()
        val menuEntriesStartY = getScreenHeight() - (menuEntriesAvailableSpace - 4 * menuEntriesHeight) / 2

        val coordinatesVolume = TextureCoordinates.getFromBlocks(6, 13, 12, 14)
        volumeButton = Plane(-menuEntriesWidth, menuEntriesStartY, menuEntriesWidth, menuEntriesHeight, coordinatesVolume)
        glRenderer.addDrawable(volumeButton)

        val coordinatesColors = TextureCoordinates.getFromBlocks(0, 15, 6, 16)
        colorsButton = Plane(-menuEntriesWidth, volumeButton.y - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesColors)
        glRenderer.addDrawable(colorsButton)

        val coordinatesTutorial = TextureCoordinates.getFromBlocks(6, 12, 12, 13)
        tutorialButton = Plane(-menuEntriesWidth, colorsButton.y - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesTutorial)
        glRenderer.addDrawable(tutorialButton)

        val coordinatesEditor = TextureCoordinates.getFromBlocks(6, 15, 12, 16)
        editorButton = Plane(-menuEntriesWidth, tutorialButton.y - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesEditor)
        glRenderer.addDrawable(editorButton)

        volumeOn = ObjectFactory.createSingleBox(12, 12, menuEntriesHeight)
        volumeOn.isVisible = false
        volumeOn.x = menuEntriesWidth
        volumeOn.y = volumeButton.y
        glRenderer.addDrawable(volumeOn)
        volumeOff = ObjectFactory.createSingleBox(13, 12, menuEntriesHeight)
        volumeOff.isVisible = false
        volumeOff.x = menuEntriesWidth
        volumeOff.y = volumeButton.y
        glRenderer.addDrawable(volumeOff)

        val coordinatesColorsExample = TextureCoordinates.getFromBlocks(14, 1, 16, 2)
        colorsExample = Plane(-menuEntriesWidth / 2, menuEntriesStartY, menuEntriesHeight * 2, menuEntriesHeight, coordinatesColorsExample)
        colorsExample.isVisible = false
        colorsExample.x = menuEntriesWidth
        colorsExample.y = colorsButton.y
        glRenderer.addDrawable(colorsExample)
    }

    override fun entry() {
        nextState = this

        val objShown = if (preferences.getBoolean("volumeOn", true)) volumeOn else volumeOff
        objShown.isVisible = true
        objShown.scale = 0f
        val scaleAnimation = ScaleAnimation(
            objShown,
            Animation.DURATION_LONG, Animation.DURATION_LONG
        )
        scaleAnimation.setTo(1f)
        scaleAnimation.start()

        colorsExample.isVisible = true
        colorsExample.scale = 0f
        val colorsScaleAnimation = ScaleAnimation(
            colorsExample,
            Animation.DURATION_LONG, Animation.DURATION_LONG + Animation.DURATION_SHORT
        )
        colorsScaleAnimation.setTo(1f)
        colorsScaleAnimation.start()

        AnimationFactory.startMenuAnimationEnter(volumeButton, (2.0f * Animation.DURATION_SHORT).toInt())
        AnimationFactory.startMenuAnimationEnter(colorsButton, (2.5f * Animation.DURATION_SHORT).toInt())
        AnimationFactory.startMenuAnimationEnter(tutorialButton, (3.0f * Animation.DURATION_SHORT).toInt())
        AnimationFactory.startMenuAnimationEnter(editorButton, (3.5f * Animation.DURATION_SHORT).toInt())
    }

    override fun exit() {
        val objShown = if (preferences.getBoolean("volumeOn", true)) volumeOn else volumeOff
        val scaleAnimation = ScaleAnimation(
            objShown,
            Animation.DURATION_LONG, 0
        )
        scaleAnimation.setTo(0f)
        scaleAnimation.setHideAfter(true)
        scaleAnimation.start()

        val colorsScaleAnimation = ScaleAnimation(
            colorsExample,
            Animation.DURATION_LONG, 0
        )
        colorsScaleAnimation.setTo(0f)
        colorsScaleAnimation.setHideAfter(true)
        colorsScaleAnimation.start()

        if (nextState === TutorialState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(tutorialButton)
        } else {
            AnimationFactory.startMenuAnimationOut(tutorialButton)
        }

        AnimationFactory.startMenuAnimationOut(volumeButton)
        AnimationFactory.startMenuAnimationOut(colorsButton)
        AnimationFactory.startMenuAnimationOut(editorButton)
    }

    override fun next(): State {
        return nextState
    }

    override fun onBackPressed() {
        nextState = MainMenuState.getInstance()
        playSound(R.raw.click)
    }

    override fun onTouchEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (volumeOn.collides(event, getScreenHeight()) || volumeButton.collides(event, getScreenHeight())) {
                playSound(R.raw.click)
                val newVolume = !preferences.getBoolean("volumeOn", true)
                preferences.edit { putBoolean("volumeOn", newVolume) }
                volumeOff.isVisible = !newVolume
                volumeOn.isVisible = newVolume
            } else if (colorsExample.collides(event, getScreenHeight()) || colorsButton.collides(event, getScreenHeight())) {
                playSound(R.raw.click)
                val newColorschemeIndex = (preferences.getInt("colorschemeIndex", 0) + 1) % numberOfColorschemes
                preferences.edit { putInt("colorschemeIndex", newColorschemeIndex) }
                glRenderer.setColorscheme(newColorschemeIndex)
            } else if (tutorialButton.collides(event, getScreenHeight())) {
                nextState = TutorialState.getInstance()
                playSound(R.raw.click)
            } else if (editorButton.collides(event, getScreenHeight())) {
                nextState = MainMenuState.getInstance()
                playSound(R.raw.click)
                val browserIntent = Intent(Intent.ACTION_VIEW, "https://flowit.bytehamster.com/".toUri())
                getActivity()?.startActivity(browserIntent)
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: SettingsState? = null

        fun getInstance(): SettingsState {
            if (instance == null) {
                instance = SettingsState()
            }
            return instance!!
        }
    }
}
