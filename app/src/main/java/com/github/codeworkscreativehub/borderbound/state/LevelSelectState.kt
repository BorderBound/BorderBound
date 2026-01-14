package com.github.codeworkscreativehub.borderbound.state;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

import com.github.codeworkscreativehub.borderbound.GLRenderer;
import com.github.codeworkscreativehub.borderbound.R;
import com.github.codeworkscreativehub.borderbound.animation.Animation;
import com.github.codeworkscreativehub.borderbound.animation.TranslateAnimation;
import com.github.codeworkscreativehub.borderbound.model.LevelPack;
import com.github.codeworkscreativehub.borderbound.object.LevelList;
import com.github.codeworkscreativehub.borderbound.object.Plane;
import com.github.codeworkscreativehub.borderbound.object.TextureCoordinates;
import com.github.codeworkscreativehub.borderbound.util.ScrollHelper;

public class LevelSelectState extends State {
    @SuppressLint("StaticFieldLeak")
    private static LevelSelectState instance;
    private State nextState = this;

    private LevelPack pack;
    private Plane selectLevelText;
    private LevelList levelList;
    private ScrollHelper scrollHelper;
    private boolean pressed = false;

    private LevelSelectState() {

    }

    public static LevelSelectState getInstance() {
        if (instance == null) {
            instance = new LevelSelectState();
        }
        return instance;
    }

    @Override
    protected void initialize(GLRenderer glRenderer) {
        TextureCoordinates coordinatesLogo = TextureCoordinates.getFromBlocks(0, 11, 6, 13);
        selectLevelText = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), glRenderer.getWidth() / 3, coordinatesLogo);
        selectLevelText.setVisible(false);

        float boxSize = getScreenWidth() / (5 + 2 + 2);
        levelList = new LevelList(boxSize, this);
        scrollHelper = new ScrollHelper(levelList, false, true);

        glRenderer.addDrawable(levelList);
        glRenderer.addDrawable(selectLevelText);
    }

    @Override
    public void entry() {
        nextState = this;
        pressed = false;

        selectLevelText.cancelAnimations();
        selectLevelText.setY(getScreenHeight());
        selectLevelText.setVisible(true);
        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelText, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        logoAnimation.setTo(0, getScreenHeight() - selectLevelText.getHeight());
        logoAnimation.start();

        levelList.setPack(pack);
        scrollHelper.setMaxima(0, getScreenHeight() - selectLevelText.getHeight(), 0, levelList.getHeight());

        float levelListPos = getScreenHeight() - selectLevelText.getHeight();
        float lastScrollPos = getPreferences().getFloat("scroll_state_" + pack.id(), levelListPos);
        TranslateAnimation listAnimation = new TranslateAnimation(levelList, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        listAnimation.setTo(0, scrollHelper.clampY(lastScrollPos));
        listAnimation.start();
    }

    @Override
    public void exit() {
        selectLevelText.cancelAnimations();
        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelText, Animation.DURATION_SHORT, 0);
        logoAnimation.setTo(0, getScreenHeight());
        logoAnimation.setHideAfter(true);
        logoAnimation.start();

        levelList.cancelAnimations();
        TranslateAnimation listAnimation = new TranslateAnimation(levelList, Animation.DURATION_SHORT, 0);
        listAnimation.setTo(0, 0);
        listAnimation.start();
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onBackPressed() {
        nextState = LevelPackSelectState.getInstance();
        playSound(R.raw.click);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pressed = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP && !scrollHelper.isScrolling() && pressed) {
            if (levelList.collides(event, getScreenHeight())) {
                GameState.getInstance().setLevel(levelList.getCollision(event, getScreenHeight()));
                nextState = GameState.getInstance();
                playSound(R.raw.click);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            pressed = false;
            getPreferences().edit().putFloat("scroll_state_" + pack.id(), levelList.getY()).apply();
        }
        scrollHelper.onTouchEvent(event);
    }

    public LevelPack getPack() {
        return pack;
    }

    public void setPack(LevelPack pack) {
        this.pack = pack;
    }
}
