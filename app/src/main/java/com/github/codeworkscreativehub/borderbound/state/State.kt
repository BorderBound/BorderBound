package com.github.codeworkscreativehub.borderbound.state;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;

import com.github.codeworkscreativehub.borderbound.BuildConfig;
import com.github.codeworkscreativehub.borderbound.GLRenderer;
import com.github.codeworkscreativehub.borderbound.SoundPool;
import com.github.codeworkscreativehub.borderbound.model.Level;
import com.github.codeworkscreativehub.borderbound.model.LevelPack;

abstract public class State {
    static final int STEPS_NOT_SOLVED = 999;
    private static final int UNLOCK_NEXT_LEVELS;

    static {
        if (BuildConfig.DEBUG_LEVELS) {
            UNLOCK_NEXT_LEVELS = 500;
        } else {
            UNLOCK_NEXT_LEVELS = 5;
        }
    }

    private float screenWidth;
    private float screenHeight;
    private SoundPool soundPool;
    private Activity activity;
    private SharedPreferences playedPrefs;
    private SharedPreferences prefs;

    abstract public void entry();

    abstract public void exit();

    abstract public State next();

    abstract public void onBackPressed();

    abstract public void onTouchEvent(MotionEvent event);

    abstract protected void initialize(GLRenderer renderer);

    public void initialize(GLRenderer renderer, SoundPool soundPool, Activity activity) {
        this.screenWidth = renderer.getWidth();
        this.screenHeight = renderer.getHeight();
        this.soundPool = soundPool;
        this.activity = activity;
        this.playedPrefs = activity.getSharedPreferences("playedState", Context.MODE_PRIVATE);
        this.prefs = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        renderer.setColorscheme(getPreferences().getInt("colorschemeIndex", 0));
        initialize(renderer);
    }

    void makePlayed(int level) {
        playedPrefs.edit().putBoolean("l" + level, true).apply();
    }

    void makeUnPlayed(int level) {
        playedPrefs.edit().putBoolean("l" + level, false).apply();
    }

    void saveSteps(int level, int steps) {
        if (playedPrefs.getInt("s" + level, STEPS_NOT_SOLVED) > steps) {
            playedPrefs.edit().putInt("s" + level, steps).apply();
        }
    }

    public int loadSteps(int level) {
        return playedPrefs.getInt("s" + level, STEPS_NOT_SOLVED);
    }

    public boolean isSolved(int level) {
        return playedPrefs.getBoolean("l" + level, false);
    }

    SharedPreferences getPreferences() {
        return prefs;
    }

    public boolean isPlayable(Level level) {
        Level current = level;

        // Debug override: unlock everything
        if (BuildConfig.DEBUG_LEVELS) {
            return true;
        }

        for (int i = 0; i <= UNLOCK_NEXT_LEVELS; i++) {

            // First level in pack
            if (current.getIndexInPack() == 0) {
                return isFirstLevelPlayable(current.getPack());
            }

            // Any solved previous level unlocks this one
            if (isSolved(current.getNumber())) {
                return true;
            }

            // Move to previous level
            current = current.getPack().getLevel(current.getIndexInPack() - 1);
        }

        return false;
    }

    private boolean isFirstLevelPlayable(LevelPack pack) {
        return pack.isEasy()
                || (pack.isMedium() && isSolved(LevelPack.EASY.getFirstLevel().getNumber()))
                || ((pack.isHard() || pack.isCommunity()) && isSolved(LevelPack.MEDIUM.getFirstLevel().getNumber()));
    }
    
    public float getScreenWidth() {
        return screenWidth;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public void playSound(int resId) {
        if (getPreferences().getBoolean("volumeOn", true)) {
            soundPool.playSound(resId);
        }
    }

    Activity getActivity() {
        return activity;
    }
}
