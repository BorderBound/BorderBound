package com.github.codeworkscreativehub.borderbound.filler;

import com.github.codeworkscreativehub.borderbound.Converter;
import com.github.codeworkscreativehub.borderbound.R;
import com.github.codeworkscreativehub.borderbound.model.Field;
import com.github.codeworkscreativehub.borderbound.model.Level;
import com.github.codeworkscreativehub.borderbound.model.Modifier;
import com.github.codeworkscreativehub.borderbound.state.State;

public class BombFiller extends Filler {
    private Modifier fillTo = Modifier.BLUE;
    private final Level levelData;
    private final State state;
    private final int col, row;

    BombFiller(Level levelData, int col, int row, State state) {
        this.levelData = levelData;
        this.state = state;
        this.col = col;
        this.row = row;
    }

    public void fill() {
        new Thread() {
            public void run() {
                fillTo = Converter.convertColor(levelData.fieldAt(col, row).getColor());

                try {
                    sleep(100);
                    state.playSound(R.raw.fill);

                    doFill(col, row);

                    sleep(100);

                    doFill(col + 1, row);
                    doFill(col, row + 1);
                    doFill(col - 1, row);
                    doFill(col, row - 1);

                    sleep(100);

                    doFill(col + 1, row - 1);
                    doFill(col + 1, row + 1);
                    doFill(col - 1, row - 1);
                    doFill(col - 1, row + 1);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnFinished();
            }
        }.start();
    }

    private void doFill(int col, int row) {
        if (row >= 0 && col >= 0 && row < levelData.getHeight() && col < levelData.getWidth()) {
            Field f = levelData.fieldAt(col, row);
            if (f.getModifier() != Modifier.TRANSPARENT) {
                f.setModifier(fillTo);
            }
        }
    }
}
