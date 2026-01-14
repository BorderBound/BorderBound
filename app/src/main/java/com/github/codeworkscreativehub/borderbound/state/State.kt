package com.github.codeworkscreativehub.borderbound.state

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.MotionEvent
import androidx.core.content.edit
import com.github.codeworkscreativehub.borderbound.BuildConfig
import com.github.codeworkscreativehub.borderbound.GLRenderer
import com.github.codeworkscreativehub.borderbound.SoundPool
import com.github.codeworkscreativehub.borderbound.model.Level
import com.github.codeworkscreativehub.borderbound.model.LevelPack

abstract class State {

    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f
    private var soundPool: SoundPool? = null
    private var activity: Activity? = null
    private lateinit var playedPrefs: SharedPreferences
    private lateinit var prefs: SharedPreferences

    abstract fun entry()

    abstract fun exit()

    abstract fun next(): State

    abstract fun onBackPressed()

    abstract fun onTouchEvent(event: MotionEvent)

    protected abstract fun initialize(renderer: GLRenderer)

    fun initialize(renderer: GLRenderer, soundPool: SoundPool, activity: Activity) {
        this.screenWidth = renderer.getWidth()
        this.screenHeight = renderer.getHeight()
        this.soundPool = soundPool
        this.activity = activity
        this.playedPrefs = activity.getSharedPreferences("playedState", Context.MODE_PRIVATE)
        this.prefs = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)

        renderer.setColorscheme(preferences.getInt("colorschemeIndex", 0))
        initialize(renderer)
    }

    internal fun makePlayed(level: Int) {
        playedPrefs.edit { putBoolean("l$level", true) }
    }

    internal fun makeUnPlayed(level: Int) {
        playedPrefs.edit { putBoolean("l$level", false) }
    }

    internal fun saveSteps(level: Int, steps: Int) {
        if (playedPrefs.getInt("s$level", STEPS_NOT_SOLVED) > steps) {
            playedPrefs.edit { putInt("s$level", steps) }
        }
    }

    fun loadSteps(level: Int): Int {
        return playedPrefs.getInt("s$level", STEPS_NOT_SOLVED)
    }

    fun isSolved(level: Int): Boolean {
        return playedPrefs.getBoolean("l$level", false)
    }

    val preferences: SharedPreferences
        get() = prefs

    fun isPlayable(level: Level): Boolean {
        var current = level

        // Debug override: unlock everything
        if (BuildConfig.DEBUG_LEVELS) {
            return true
        }

        for (i in 0..UNLOCK_NEXT_LEVELS) {

            // First level in pack
            if (current.indexInPack == 0) {
                return isFirstLevelPlayable(current.pack)
            }

            // Any solved previous level unlocks this one
            if (isSolved(current.number)) {
                return true
            }

            // Move to previous level
            current = current.pack.getLevel(current.indexInPack - 1)
        }

        return false
    }

    private fun isFirstLevelPlayable(pack: LevelPack): Boolean {
        return pack.isEasy()
                || (pack.isMedium() && isSolved(LevelPack.EASY.firstLevel!!.number))
                || ((pack.isHard() || pack.isCommunity()) && isSolved(LevelPack.MEDIUM.firstLevel!!.number))
    }

    fun getScreenWidth(): Float {
        return screenWidth
    }

    fun getScreenHeight(): Float {
        return screenHeight
    }

    fun playSound(resId: Int) {
        if (preferences.getBoolean("volumeOn", true)) {
            soundPool?.playSound(resId)
        }
    }

    fun getActivity(): Activity? {
        return activity
    }

    companion object {
        const val STEPS_NOT_SOLVED = 999
        private val UNLOCK_NEXT_LEVELS = if (BuildConfig.DEBUG_LEVELS) 500 else 5
    }
}
