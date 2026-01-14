package com.github.codeworkscreativehub.borderbound;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.github.codeworkscreativehub.borderbound.model.LevelPack;
import com.github.codeworkscreativehub.borderbound.state.ExitState;
import com.github.codeworkscreativehub.borderbound.state.GameState;
import com.github.codeworkscreativehub.borderbound.state.LevelPackSelectState;
import com.github.codeworkscreativehub.borderbound.state.LevelSelectState;
import com.github.codeworkscreativehub.borderbound.state.MainMenuState;
import com.github.codeworkscreativehub.borderbound.state.SettingsState;
import com.github.codeworkscreativehub.borderbound.state.State;
import com.github.codeworkscreativehub.borderbound.state.TutorialState;

public class Main extends AppCompatActivity {
    private MyGLSurfaceView glSurfaceView;
    private SoundPool soundPool;
    private State currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        glSurfaceView = findViewById(R.id.gl_surface_view);

        getSharedPreferences("preferences", Context.MODE_PRIVATE).edit()
                .putInt("lastAppVersion", BuildConfig.VERSION_CODE)
                .apply();

        LevelPack.parsePacks(this);
        createViews();

        ActivityManager.TaskDescription taskDesc = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            taskDesc = new ActivityManager.TaskDescription.Builder()
                    .setLabel(getString(R.string.app_name))
                    .setPrimaryColor(0xff206dbc)
                    .build();
        }
        setTaskDescription(taskDesc);

        // Modern back gesture + hardware back handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentState != null) {
                    currentState.onBackPressed();
                    switchState();
                }
            }
        });
    }

    private void createViews() {
        glSurfaceView.getRenderer().setOnViewportSetupComplete(() -> {
            soundPool = new SoundPool(Main.this);
            soundPool.loadSound(R.raw.click);
            soundPool.loadSound(R.raw.fill);
            soundPool.loadSound(R.raw.won);

            State[] states = new State[]{
                    MainMenuState.getInstance(),
                    ExitState.getInstance(),
                    SettingsState.getInstance(),
                    LevelPackSelectState.getInstance(),
                    LevelSelectState.getInstance(),
                    GameState.getInstance(),
                    TutorialState.getInstance()
            };

            for (State state : states) {
                state.initialize(glSurfaceView.getRenderer(), soundPool, Main.this);
            }

            currentState = MainMenuState.getInstance();
            currentState.entry();
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currentState != null) {
            event.offsetLocation(-glSurfaceView.getX(), -glSurfaceView.getY());
            currentState.onTouchEvent(event);
            switchState();
        }
        return false;
    }

    private void switchState() {
        State newState = currentState.next();
        if (currentState != newState) {
            currentState.exit();
            currentState = newState;
            currentState.entry();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
}
