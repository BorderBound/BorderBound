package com.github.codeworkscreativehub.borderbound.state

import android.annotation.SuppressLint
import android.view.MotionEvent
import com.github.codeworkscreativehub.borderbound.GLRenderer
import com.github.codeworkscreativehub.borderbound.R
import com.github.codeworkscreativehub.borderbound.animation.Animation
import com.github.codeworkscreativehub.borderbound.animation.TranslateAnimation
import com.github.codeworkscreativehub.borderbound.model.LevelPack
import com.github.codeworkscreativehub.borderbound.`object`.LevelList
import com.github.codeworkscreativehub.borderbound.`object`.Plane
import com.github.codeworkscreativehub.borderbound.`object`.TextureCoordinates
import com.github.codeworkscreativehub.borderbound.util.ScrollHelper

class LevelSelectState private constructor() : State() {

    private var nextState: State = this
    var pack: LevelPack? = null
    private lateinit var selectLevelText: Plane
    private lateinit var levelList: LevelList
    private lateinit var scrollHelper: ScrollHelper
    private var pressed = false

    override fun initialize(renderer: GLRenderer) {
        val coordinatesLogo = TextureCoordinates.getFromBlocks(0, 11, 6, 13)
        selectLevelText = Plane(0f, renderer.getHeight(), renderer.getWidth(), renderer.getWidth() / 3, coordinatesLogo)
        selectLevelText.isVisible = false

        val boxSize = getScreenWidth() / (5 + 2 + 2)
        levelList = LevelList(boxSize, this)
        scrollHelper = ScrollHelper(levelList, false, true)

        renderer.addDrawable(levelList)
        renderer.addDrawable(selectLevelText)
    }

    override fun entry() {
        nextState = this
        pressed = false

        selectLevelText.cancelAnimations()
        selectLevelText.y = getScreenHeight()
        selectLevelText.isVisible = true
        val logoAnimation = TranslateAnimation(selectLevelText, Animation.DURATION_LONG, Animation.DURATION_SHORT)
        logoAnimation.setTo(0f, getScreenHeight() - selectLevelText.height)
        logoAnimation.start()

        levelList.setPack(pack!!)
        scrollHelper.setMaxima(0f, getScreenHeight() - selectLevelText.height, 0f, levelList.height)

        val levelListPos = getScreenHeight() - selectLevelText.height
        val lastScrollPos = preferences.getFloat("scroll_state_" + pack!!.id, levelListPos)
        val listAnimation = TranslateAnimation(levelList, Animation.DURATION_LONG, Animation.DURATION_SHORT)
        listAnimation.setTo(0f, scrollHelper.clampY(lastScrollPos))
        listAnimation.start()
    }

    override fun exit() {
        selectLevelText.cancelAnimations()
        val logoAnimation = TranslateAnimation(selectLevelText, Animation.DURATION_SHORT, 0)
        logoAnimation.setTo(0f, getScreenHeight())
        logoAnimation.setHideAfter(true)
        logoAnimation.start()

        levelList.cancelAnimations()
        val listAnimation = TranslateAnimation(levelList, Animation.DURATION_SHORT, 0)
        listAnimation.setTo(0f, 0f)
        listAnimation.start()
    }

    override fun next(): State {
        return nextState
    }

    override fun onBackPressed() {
        nextState = LevelPackSelectState.getInstance()
        playSound(R.raw.click)
    }

    override fun onTouchEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            pressed = true
        } else if (event.action == MotionEvent.ACTION_UP && !scrollHelper.isScrolling && pressed) {
            if (levelList.collides(event, getScreenHeight())) {
                GameState.getInstance().setLevel(levelList.getCollision(event, getScreenHeight())!!)
                nextState = GameState.getInstance()
                playSound(R.raw.click)
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            pressed = false
            preferences.edit().putFloat("scroll_state_" + pack!!.id, levelList.y).apply()
        }
        scrollHelper.onTouchEvent(event)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: LevelSelectState? = null

        fun getInstance(): LevelSelectState {
            if (instance == null) {
                instance = LevelSelectState()
            }
            return instance!!
        }
    }
}
