package com.github.codeworkscreativehub.borderbound

import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.github.codeworkscreativehub.borderbound.model.LevelPack
import com.github.codeworkscreativehub.borderbound.state.ExitState
import com.github.codeworkscreativehub.borderbound.state.GameState
import com.github.codeworkscreativehub.borderbound.state.LevelPackSelectState
import com.github.codeworkscreativehub.borderbound.state.LevelSelectState
import com.github.codeworkscreativehub.borderbound.state.MainMenuState
import com.github.codeworkscreativehub.borderbound.state.SettingsState
import com.github.codeworkscreativehub.borderbound.state.State
import com.github.codeworkscreativehub.borderbound.state.TutorialState

class Main : AppCompatActivity() {
    private var glSurfaceView: MyGLSurfaceView? = null
    private var soundPool: SoundPool? = null
    private var currentState: State? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remove title and set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContentView(R.layout.main)

        glSurfaceView = findViewById(R.id.gl_surface_view)

        // Update last app version in preferences
        getSharedPreferences("preferences", MODE_PRIVATE).edit().apply {
            putInt("lastAppVersion", BuildConfig.VERSION_CODE)
            apply()
        }

        LevelPack.parsePacks(this)
        createViews()

        // Handle modern TaskDescription for Recents screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val taskDesc = ActivityManager.TaskDescription.Builder()
                .setLabel(getString(R.string.app_name))
                .setPrimaryColor(0xff206dbc.toInt())
                .build()
            setTaskDescription(taskDesc)
        }

        // Handle Back Press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentState?.let {
                    it.onBackPressed()
                    switchState()
                }
            }
        })
    }

    private fun createViews() {
        // Set up the renderer callback
        glSurfaceView?.renderer?.setOnViewportSetupComplete {
            soundPool = SoundPool(this@Main).apply {
                loadSound(R.raw.click)
                loadSound(R.raw.fill)
                loadSound(R.raw.won)
            }

            val states = arrayOf(
                MainMenuState.getInstance(),
                ExitState.getInstance(),
                SettingsState.getInstance(),
                LevelPackSelectState.getInstance(),
                LevelSelectState.getInstance(),
                GameState.getInstance(),
                TutorialState.getInstance()
            )

            // Initialize all states
            for (state in states) {
                state.initialize(glSurfaceView?.renderer!!, soundPool!!, this@Main)
            }

            // Set initial state
            currentState = MainMenuState.getInstance()
            currentState?.entry()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        currentState?.let {
            // Offset the touch coordinates based on surface view position
            event.offsetLocation(-(glSurfaceView?.x ?: 0f), -(glSurfaceView?.y ?: 0f))
            it.onTouchEvent(event)
            switchState()
        }
        return false
    }

    private fun switchState() {
        val newState = currentState?.next()
        if (currentState != newState) {
            currentState?.exit()
            currentState = newState
            currentState?.entry()
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }
}