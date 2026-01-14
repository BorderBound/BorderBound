package com.github.codeworkscreativehub.borderbound.state

import android.annotation.SuppressLint
import android.view.MotionEvent
import com.github.codeworkscreativehub.borderbound.BuildConfig
import com.github.codeworkscreativehub.borderbound.Converter
import com.github.codeworkscreativehub.borderbound.GLRenderer
import com.github.codeworkscreativehub.borderbound.R
import com.github.codeworkscreativehub.borderbound.animation.Animation
import com.github.codeworkscreativehub.borderbound.animation.AnimationFactory
import com.github.codeworkscreativehub.borderbound.animation.ScaleAnimation
import com.github.codeworkscreativehub.borderbound.animation.TranslateAnimation
import com.github.codeworkscreativehub.borderbound.filler.Filler
import com.github.codeworkscreativehub.borderbound.model.Level
import com.github.codeworkscreativehub.borderbound.`object`.LevelDrawer
import com.github.codeworkscreativehub.borderbound.`object`.Number
import com.github.codeworkscreativehub.borderbound.`object`.ObjectFactory
import com.github.codeworkscreativehub.borderbound.`object`.Plane
import com.github.codeworkscreativehub.borderbound.`object`.TextureCoordinates

class GameState private constructor() : State() {

    private val levelDrawer = LevelDrawer.getInstance()
    private var nextState: State = this
    private lateinit var level: Level
    private var boardStartY = 0f
    private lateinit var winMessage: Plane
    private lateinit var lockedMessage: Plane
    private lateinit var left: Plane
    private lateinit var right: Plane
    private lateinit var restart: Plane
    private lateinit var stepsLabel: Plane
    private lateinit var stepsImproved: Plane
    private lateinit var solved: Plane
    private lateinit var headerBackground: Plane
    private lateinit var stepsUsed: Number
    private lateinit var stepsBest: Number
    private lateinit var stepsOptimal: Number

    // New fields for level display
    private lateinit var levelNumber: Number

    private var isFilling = false
    private var won = false
    private var topBarHeight = 0f
    private var topButtonSize = 0f
    private var topButtonY = 0f
    private var topBarPadding = 0f
    private var stepsUsedCurrentYDelta = 0f
    private var stepsUsedBestYDelta = 0f
    private var currentLevelYDelta = 0f
    private var stepsOptimalYDelta = 0f
    private var lastLevelState = LastLevelState.NO_LEVEL
    private var filler: Filler? = null

    override fun initialize(renderer: GLRenderer) {
        topBarHeight = renderer.getWidth() / (8 * 0.6f + 8 * 0.2f)
        topButtonSize = 0.6f * topBarHeight
        topBarPadding = 0.2f * topBarHeight
        topButtonY = renderer.getHeight() - topButtonSize - topBarPadding
        stepsUsedCurrentYDelta = topButtonSize * 0.8f
        stepsUsedBestYDelta = topButtonSize * 0.45f
        stepsOptimalYDelta = topButtonSize * 0.1f
        currentLevelYDelta = topButtonSize * -0.25f

        // Header background
        val coordinatesHeader = TextureCoordinates.getFromBlocks(14, 12, 15, 13)
        headerBackground = Plane(0f, renderer.getHeight(), renderer.getWidth(), topBarHeight, coordinatesHeader)
        headerBackground.isVisible = false
        renderer.addDrawable(headerBackground)

        // Left, Right, Restart buttons
        left = ObjectFactory.createSingleBox(0, 10, topButtonSize)
        left.x = topBarPadding
        left.y = renderer.getHeight() + topBarPadding
        left.isVisible = false
        renderer.addDrawable(left)

        right = ObjectFactory.createSingleBox(1, 10, topButtonSize)
        right.x = renderer.getWidth() - topBarPadding - topButtonSize
        right.y = renderer.getHeight() + topBarPadding
        right.isVisible = false
        renderer.addDrawable(right)

        restart = ObjectFactory.createSingleBox(2, 10, topButtonSize)
        restart.x = topButtonSize + 2 * topBarPadding
        restart.y = renderer.getHeight() + topBarPadding
        restart.isVisible = false
        renderer.addDrawable(restart)

        // Steps improved box
        stepsImproved = ObjectFactory.createSingleBox(4, 10, topBarHeight)
        stepsImproved.x = 6 * topButtonSize + 4 * topBarPadding
        stepsImproved.y = getScreenHeight() - topBarHeight
        stepsImproved.isVisible = false
        stepsImproved.scale = 2f
        renderer.addDrawable(stepsImproved)

        // Steps numbers
        stepsUsed = Number()
        stepsUsed.setFontSize((topButtonSize * 0.8f) * 0.35f)
        stepsUsed.x = 5 * topButtonSize + 3 * topBarPadding
        stepsUsed.y = renderer.getHeight() + topBarPadding + stepsUsedCurrentYDelta + 0.25f * (topButtonSize * 0.8f)
        renderer.addDrawable(stepsUsed)

        stepsBest = Number()
        stepsBest.setFontSize((topButtonSize * 0.8f) * 0.35f)
        stepsBest.x = 5 * topButtonSize + 3 * topBarPadding
        stepsBest.y = renderer.getHeight() + topBarPadding + stepsUsedBestYDelta + 0.25f * (topButtonSize * 0.8f)
        renderer.addDrawable(stepsBest)

        stepsOptimal = Number()
        stepsOptimal.setFontSize((topButtonSize * 0.8f) * 0.35f)
        stepsOptimal.x = 5 * topButtonSize + 3 * topBarPadding
        stepsOptimal.y = renderer.getHeight() + topBarPadding + stepsOptimalYDelta + 0.25f * (topButtonSize * 0.8f)
        renderer.addDrawable(stepsOptimal)

        // Level number
        levelNumber = Number()
        levelNumber.setFontSize((topButtonSize * 0.8f) * 0.35f)
        levelNumber.x = 5 * topButtonSize + 3 * topBarPadding
        levelNumber.y = renderer.getHeight() + topBarPadding + currentLevelYDelta + 0.5f * (topButtonSize * 0.8f)
        renderer.addDrawable(levelNumber)

        // Steps label
        val coordinateSteps = TextureCoordinates.getFromBlocks(12, 10, 15, 12)
        stepsLabel = Plane(0f, 0f, 3 * (topButtonSize * 0.8f), 2 * (topButtonSize * 0.8f), coordinateSteps)
        stepsLabel.x = 2 * topButtonSize + 4 * topBarPadding
        stepsLabel.y = renderer.getHeight() + topBarPadding + 0.2f * (topButtonSize * 0.8f)
        stepsLabel.isVisible = false
        renderer.addDrawable(stepsLabel)

        // Solved box
        solved = ObjectFactory.createSingleBox(3, 10, topButtonSize)
        solved.x = 6 * topButtonSize + 5 * topBarPadding
        solved.y = renderer.getHeight() + topBarPadding
        solved.isVisible = false
        renderer.addDrawable(solved)

        // Level drawer
        levelDrawer.isVisible = false
        levelDrawer.setScreenWidth(getScreenWidth())
        levelDrawer.x = 0f
        renderer.addDrawable(levelDrawer)

        // Win and locked messages
        val coordinatesWin = TextureCoordinates.getFromBlocks(0, 8, 6, 10)
        winMessage = Plane(0f, renderer.getHeight(), renderer.getWidth(), renderer.getWidth() / 3, coordinatesWin)
        winMessage.isVisible = false
        renderer.addDrawable(winMessage)

        val coordinatesLocked = TextureCoordinates.getFromBlocks(0, 13, 6, 15)
        lockedMessage = Plane(0f, renderer.getHeight(), renderer.getWidth(), renderer.getWidth() / 3, coordinatesLocked)
        lockedMessage.isVisible = false
        lockedMessage.y = -getScreenWidth() * 0.5f
        renderer.addDrawable(lockedMessage)

        val rightAnimation = ScaleAnimation(right, Animation.DURATION_LONG, 0)
        rightAnimation.setTo(1.2f)
    }

    override fun entry() {
        nextState = this
        lastLevelState = LastLevelState.NO_LEVEL
        reloadLevel()

        AnimationFactory.startMoveYTo(left, topButtonY)
        AnimationFactory.startMoveYTo(right, topButtonY)
        AnimationFactory.startMoveYTo(restart, topButtonY)
        AnimationFactory.startMoveYTo(stepsLabel, topButtonY - 0.75f * (topButtonSize * 0.2f))
        AnimationFactory.startMoveYTo(stepsBest, topButtonY + stepsUsedBestYDelta + 0.25f * topButtonSize)
        AnimationFactory.startMoveYTo(stepsUsed, topButtonY + stepsUsedCurrentYDelta + 0.25f * topButtonSize)
        AnimationFactory.startMoveYTo(stepsOptimal, topButtonY + stepsOptimalYDelta + 0.25f * topButtonSize)
        AnimationFactory.startMoveYTo(headerBackground, getScreenHeight() - topBarHeight)

        // Animate new level label and number
        AnimationFactory.startMoveYTo(levelNumber, topButtonY + currentLevelYDelta + 0.25f * topButtonSize)
    }

    private fun reloadLevel() {
        won = false
        stepsUsed.setValue(0)
        if (loadSteps(level.number) == STEPS_NOT_SOLVED) {
            stepsBest.setValue(Number.VALUE_NAN)
        } else {
            stepsBest.setValue(loadSteps(level.number))
        }
        if (level.optimalSteps <= 0) {
            stepsOptimal.setValue(Number.VALUE_NAN)
        } else {
            stepsOptimal.setValue(level.optimalSteps)
        }
        AnimationFactory.startScaleHide(stepsImproved, 0)
        isFilling = false
        level.reset()
        levelDrawer.setLevel(level)

        // Set level number
        levelNumber.setValue(level.number)

        var remainingSpace = getScreenHeight() - topBarHeight - levelDrawer.height
        val horizontalPaddingDelta = levelDrawer.boxSize / 2
        var horizontalPadding = horizontalPaddingDelta
        while (remainingSpace < 0) {
            levelDrawer.setScreenWidth(getScreenWidth() - 2 * horizontalPadding)
            levelDrawer.x = horizontalPadding
            remainingSpace = getScreenHeight() - topBarHeight - levelDrawer.height
            horizontalPadding += horizontalPaddingDelta
        }
        boardStartY = topBarHeight + remainingSpace / 2

        if (levelDrawer.y != getScreenHeight() - boardStartY) {
            levelDrawer.cancelAnimations()
            levelDrawer.isVisible = true
            val drawerAnimation: TranslateAnimation = if (lastLevelState == LastLevelState.NO_LEVEL) {
                levelDrawer.y = -levelDrawer.boxSize
                TranslateAnimation(levelDrawer, Animation.DURATION_LONG, Animation.DURATION_LONG)
            } else {
                TranslateAnimation(levelDrawer, Animation.DURATION_SHORT, 0)
            }
            drawerAnimation.setTo(levelDrawer.x, getScreenHeight() - boardStartY)
            drawerAnimation.start()
        }

        if (!isPlayable(level)) {
            val availableSpace = getScreenHeight()
            lockedMessage.cancelAnimations()
            lockedMessage.isVisible = true
            val inAnimation: TranslateAnimation = if (lastLevelState == LastLevelState.NO_LEVEL) {
                TranslateAnimation(lockedMessage, Animation.DURATION_LONG, Animation.DURATION_LONG)
            } else {
                TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0)
            }
            inAnimation.setTo(0f, (availableSpace - lockedMessage.height) / 2)
            inAnimation.start()
        } else {
            val outAnimation = TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0)
            outAnimation.setTo(0f, -getScreenWidth() * 0.5f)
            outAnimation.setHideAfter(true)
            outAnimation.start()
        }

        if (isSolved(level.number)) {
            if (lastLevelState == LastLevelState.NO_LEVEL) {
                solved.scale = 1f
                AnimationFactory.startMoveYTo(solved, topButtonY)
            } else if (lastLevelState == LastLevelState.NOT_SOLVED) {
                showSolved(Animation.DURATION_SHORT / 2)
            }
            lastLevelState = LastLevelState.SOLVED
        } else {
            if (!isSolved(level.number) && lastLevelState == LastLevelState.NO_LEVEL) {
                solved.isVisible = false
            } else if (!isSolved(level.number) && lastLevelState == LastLevelState.SOLVED) {
                hideSolved()
            }
            lastLevelState = LastLevelState.NOT_SOLVED
        }

        if (winMessage.isVisible) {
            winMessage.cancelAnimations()
            val outAnimation = TranslateAnimation(winMessage, Animation.DURATION_SHORT, 0)
            outAnimation.setTo(0f, -getScreenWidth() * 0.5f)
            outAnimation.setHideAfter(true)
            outAnimation.start()
        }
    }

    override fun exit() {
        levelDrawer.cancelAnimations()
        val logoAnimation = TranslateAnimation(levelDrawer, Animation.DURATION_LONG, Animation.DURATION_LONG)
        logoAnimation.setTo(levelDrawer.x, -levelDrawer.boxSize)
        logoAnimation.setHideAfter(true)
        logoAnimation.start()

        AnimationFactory.startMoveYTo(left, getScreenHeight() + topBarPadding)
        AnimationFactory.startMoveYTo(right, getScreenHeight() + topBarPadding)
        AnimationFactory.startMoveYTo(restart, getScreenHeight() + topBarPadding)
        AnimationFactory.startMoveYTo(solved, getScreenHeight() + topBarPadding)
        AnimationFactory.startMoveYTo(stepsLabel, getScreenHeight() + topBarPadding - 0.75f * (topButtonSize * 0.2f))
        AnimationFactory.startMoveYTo(
            stepsBest, getScreenHeight() + topBarPadding
                    + stepsUsedBestYDelta + 0.25f * topButtonSize
        )
        AnimationFactory.startMoveYTo(
            stepsUsed, getScreenHeight() + topBarPadding
                    + stepsUsedCurrentYDelta + 0.25f * topButtonSize
        )
        AnimationFactory.startMoveYTo(
            stepsOptimal, getScreenHeight() + topBarPadding
                    + stepsOptimalYDelta + 0.25f * topButtonSize
        )
        AnimationFactory.startMoveYTo(headerBackground, getScreenHeight())
        AnimationFactory.startMoveYTo(winMessage, -getScreenWidth() * 0.5f)
        AnimationFactory.startScaleHide(stepsImproved, 0)

        // Hide new level label and number
        AnimationFactory.startMoveYTo(
            levelNumber, getScreenHeight() + topBarPadding
                    + currentLevelYDelta + 0.25f * topButtonSize
        )

        val outAnimation = TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0)
        outAnimation.setTo(0f, -getScreenWidth() * 0.5f)
        outAnimation.setHideAfter(true)
        outAnimation.start()
    }

    override fun next(): State {
        return nextState
    }

    override fun onBackPressed() {
        nextState = LevelSelectState.getInstance()
        playSound(R.raw.click)
    }

    override fun onTouchEvent(event: MotionEvent) {
        if (event.action != MotionEvent.ACTION_DOWN) {
            return
        }

        if (left.collides(event, getScreenHeight())) {
            playSound(R.raw.click)
            if (level.indexInPack == 0) {
                nextState = LevelSelectState.getInstance()
            } else {
                level = level.pack.getLevel(level.indexInPack - 1)
                reloadLevel()
            }
        } else if (right.collides(event, getScreenHeight())
            || winMessage.collides(event, getScreenHeight())
        ) {
            playSound(R.raw.click)
            if (level.pack.size() == level.indexInPack + 1) {
                nextState = LevelSelectState.getInstance()
            } else {
                level = level.pack.getLevel(level.indexInPack + 1)
                reloadLevel()
            }
        } else if (restart.collides(event, getScreenHeight())) {
            playSound(R.raw.click)
            if (BuildConfig.DEBUG_LEVELS) {
                makeUnPlayed(level.number)
            }
            if (stepsUsed.getValue() != 0) {
                wiggle()
            }
            if (isFilling) {
                filler?.setOnFinished { reloadLevel() }
            } else {
                reloadLevel()
            }
        } else if (!isFilling && !won && isPlayable(level)) {
            checkFieldTouched(event)
        }
    }

    private fun wiggle() {
        ScaleAnimation(levelDrawer, Animation.DURATION_SHORT / 2, 0)
            .setTo(0.95f).start()
        ScaleAnimation(levelDrawer, Animation.DURATION_SHORT / 2, Animation.DURATION_SHORT / 2)
            .setTo(1f).start()
    }

    private fun checkFieldTouched(event: MotionEvent) {
        for (row in 0 until level.height) {
            for (col in 0 until level.width) {
                if (event.y > boardStartY + row * levelDrawer.boxSize
                    && event.y < boardStartY + (row + 1) * levelDrawer.boxSize
                    && event.x > levelDrawer.x + (col + 0.5) * levelDrawer.boxSize
                    && event.x < levelDrawer.x + (col + 1.5) * levelDrawer.boxSize
                ) {

                    triggerField(col, row)
                }
            }
        }
    }

    private fun triggerField(col: Int, row: Int) {
        filler = Filler.get(level, col, row, this)
        if (filler != null) {
            stepsUsed.increment()
            playSound(R.raw.click)
            isFilling = true
            if (level.fieldAt(col, row).modifier.isRotating()) {
                val rotated = level.fieldAt(col, row).modifier.rotate()
                level.fieldAt(col, row).modifier = rotated
            }
            filler!!.setOnFinished {
                isFilling = false
                checkWon()
            }
            filler!!.fill()
        }
    }

    fun setLevel(level: Level) {
        this.level = level
    }

    private fun checkWon() {
        won = true
        for (r in 0 until level.height) {
            for (c in 0 until level.width) {
                val f = level.fieldAt(c, r)
                if (Converter.convertColor(f.modifier) != null // Is not a color
                    && f.color != Converter.convertColor(f.modifier)
                ) {
                    won = false
                }
            }
        }

        if (won) {
            playSound(R.raw.won)
            makePlayed(level.number)
            saveSteps(level.number, stepsUsed.getValue())
            lastLevelState = LastLevelState.SOLVED

            val availableSpace = getScreenHeight()
            winMessage.y = -getScreenWidth() * 0.5f
            winMessage.isVisible = true
            val inAnimation = TranslateAnimation(winMessage, Animation.DURATION_SHORT, 0)
            inAnimation.setTo(0f, (availableSpace - winMessage.height) / 2)
            inAnimation.start()

            if (!solved.isVisible) {
                showSolved(Animation.DURATION_LONG)
            }

            if (stepsUsed.getValue() < stepsBest.getValue() && stepsBest.getValue() < STEPS_NOT_SOLVED) {
                AnimationFactory.startScaleShow(stepsImproved, 0)
            }
        }
    }

    private fun showSolved(speed: Int) {
        solved.cancelAnimations()
        solved.scale = 0f
        solved.y = topButtonY
        solved.isVisible = true
        val leftAnimation = ScaleAnimation(solved, speed, 0)
        leftAnimation.setTo(1f)
        leftAnimation.start()
    }

    private fun hideSolved() {
        val leftAnimation = ScaleAnimation(solved, Animation.DURATION_SHORT / 2, 0)
        leftAnimation.setTo(0f)
        leftAnimation.setHideAfter(true)
        leftAnimation.start()
    }

    private enum class LastLevelState {
        SOLVED, NO_LEVEL, NOT_SOLVED
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: GameState? = null

        fun getInstance(): GameState {
            if (instance == null) {
                instance = GameState()
            }
            return instance!!
        }
    }
}
