package com.github.codeworkscreativehub.borderbound.state

import android.annotation.SuppressLint
import android.view.MotionEvent
import com.github.codeworkscreativehub.borderbound.GLRenderer
import com.github.codeworkscreativehub.borderbound.R
import com.github.codeworkscreativehub.borderbound.animation.Animation
import com.github.codeworkscreativehub.borderbound.animation.AnimationFactory
import com.github.codeworkscreativehub.borderbound.animation.TranslateAnimation
import com.github.codeworkscreativehub.borderbound.model.LevelPack
import com.github.codeworkscreativehub.borderbound.`object`.Container
import com.github.codeworkscreativehub.borderbound.`object`.Plane
import com.github.codeworkscreativehub.borderbound.`object`.TextureCoordinates
import com.github.codeworkscreativehub.borderbound.util.ScrollHelper

class LevelPackSelectState private constructor() : State() {

    private var nextState: State = this
    private lateinit var pack1: Plane
    private lateinit var pack2: Plane
    private lateinit var pack3: Plane
    private lateinit var pack4: Plane
    private lateinit var selectLevelPackText: Plane
    private lateinit var container: Container
    private var pressed = false
    private lateinit var scrollHelper: ScrollHelper

    override fun initialize(renderer: GLRenderer) {
        val coordinatesLogo = TextureCoordinates.getFromBlocks(6, 10, 12, 12)
        selectLevelPackText = Plane(0f, renderer.getHeight(), renderer.getWidth(), renderer.getWidth() / 3, coordinatesLogo)
        selectLevelPackText.isVisible = false

        val menuEntriesWidth = renderer.getWidth() * 0.75f
        val menuEntriesHeight = menuEntriesWidth / 6

        container = Container()
        scrollHelper = ScrollHelper(container, false, false)

        val coordinatesPack1 = TextureCoordinates.getFromBlocks(0, 5, 6, 6)
        pack1 = Plane(-menuEntriesWidth, -2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack1)
        container.addDrawable(pack1)

        val coordinatesPack2 = TextureCoordinates.getFromBlocks(0, 6, 6, 7)
        pack2 = Plane(-menuEntriesWidth, pack1.y - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack2)
        container.addDrawable(pack2)

        val coordinatesPack3 = TextureCoordinates.getFromBlocks(0, 7, 6, 8)
        pack3 = Plane(-menuEntriesWidth, pack2.y - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack3)
        container.addDrawable(pack3)

        val coordinatesPack4 = TextureCoordinates.getFromBlocks(6, 14, 12, 15)
        pack4 = Plane(-menuEntriesWidth, pack3.y - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack4)
        container.addDrawable(pack4)

        renderer.addDrawable(container)
        renderer.addDrawable(selectLevelPackText)
    }

    override fun entry() {
        nextState = this
        pressed = false

        selectLevelPackText.cancelAnimations()
        selectLevelPackText.y = getScreenHeight()
        selectLevelPackText.isVisible = true
        val logoAnimation = TranslateAnimation(selectLevelPackText, Animation.DURATION_LONG, Animation.DURATION_LONG)
        logoAnimation.setTo(0f, getScreenHeight() - selectLevelPackText.height)
        logoAnimation.start()

        container.y = getScreenHeight() - selectLevelPackText.height
        scrollHelper.setMaxima(-Float.MAX_VALUE, -Float.MAX_VALUE, container.y, 0f)

        AnimationFactory.startMenuAnimationEnter(pack1, (3.0f * Animation.DURATION_SHORT).toInt())
        AnimationFactory.startMenuAnimationEnter(pack2, (3.5f * Animation.DURATION_SHORT).toInt())
        AnimationFactory.startMenuAnimationEnter(pack3, (4.0f * Animation.DURATION_SHORT).toInt())
        AnimationFactory.startMenuAnimationEnter(pack4, (4.5f * Animation.DURATION_SHORT).toInt())
    }

    override fun exit() {
        selectLevelPackText.cancelAnimations()
        val logoAnimation = TranslateAnimation(selectLevelPackText, Animation.DURATION_SHORT, 0)
        logoAnimation.setTo(0f, getScreenHeight())
        logoAnimation.setHideAfter(true)
        logoAnimation.start()

        if (LevelSelectState.getInstance().pack === LevelPack.EASY) {
            AnimationFactory.startMenuAnimationOutPressed(pack1)
        } else {
            AnimationFactory.startMenuAnimationOut(pack1)
        }

        if (LevelSelectState.getInstance().pack === LevelPack.MEDIUM) {
            AnimationFactory.startMenuAnimationOutPressed(pack2)
        } else {
            AnimationFactory.startMenuAnimationOut(pack2)
        }

        if (LevelSelectState.getInstance().pack === LevelPack.HARD) {
            AnimationFactory.startMenuAnimationOutPressed(pack3)
        } else {
            AnimationFactory.startMenuAnimationOut(pack3)
        }

        if (LevelSelectState.getInstance().pack === LevelPack.COMMUNITY) {
            AnimationFactory.startMenuAnimationOutPressed(pack4)
        } else {
            AnimationFactory.startMenuAnimationOut(pack4)
        }
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
            pressed = true
        } else if (event.action == MotionEvent.ACTION_UP && !scrollHelper.isScrolling && pressed) {
            if (pack1.collides(event.x, event.y + container.y, getScreenHeight())) {
                openSelectState(LevelPack.EASY)
            } else if (pack2.collides(event.x, event.y + container.y, getScreenHeight())) {
                openSelectState(LevelPack.MEDIUM)
            } else if (pack3.collides(event.x, event.y + container.y, getScreenHeight())) {
                openSelectState(LevelPack.HARD)
            } else if (pack4.collides(event.x, event.y + container.y, getScreenHeight())) {
                openSelectState(LevelPack.COMMUNITY)
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            pressed = false
        }
        scrollHelper.onTouchEvent(event)
    }

    private fun openSelectState(pack: LevelPack) {
        nextState = LevelSelectState.getInstance()
        LevelSelectState.getInstance().pack = pack
        playSound(R.raw.click)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: LevelPackSelectState? = null

        fun getInstance(): LevelPackSelectState {
            if (instance == null) {
                instance = LevelPackSelectState()
            }
            return instance!!
        }
    }
}
