package com.github.codeworkscreativehub.borderbound.state

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import com.github.codeworkscreativehub.borderbound.GLRenderer
import com.github.codeworkscreativehub.borderbound.animation.Animation

class ExitState private constructor() : State() {

    override fun initialize(renderer: GLRenderer) {

    }

    override fun entry() {
        Handler(Looper.getMainLooper()).postDelayed({
            getActivity()?.finish()
        }, Animation.DURATION_LONG.toLong())
    }

    override fun exit() {

    }

    override fun next(): State {
        return this
    }

    override fun onBackPressed() {

    }

    override fun onTouchEvent(event: MotionEvent) {

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: ExitState? = null

        fun getInstance(): ExitState {
            if (instance == null) {
                instance = ExitState()
            }
            return instance!!
        }
    }
}
