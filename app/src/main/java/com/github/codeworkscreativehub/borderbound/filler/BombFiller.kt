package com.github.codeworkscreativehub.borderbound.filler

import com.github.codeworkscreativehub.borderbound.Converter
import com.github.codeworkscreativehub.borderbound.R
import com.github.codeworkscreativehub.borderbound.model.Level
import com.github.codeworkscreativehub.borderbound.model.Modifier
import com.github.codeworkscreativehub.borderbound.state.State
import kotlin.concurrent.thread

class BombFiller(
    private val levelData: Level,
    private val col: Int,
    private val row: Int,
    private val state: State
) : Filler() {

    private var fillTo: Modifier = Modifier.BLUE

    override fun fill() {
        thread {
            fillTo = Converter.convertColor(levelData.fieldAt(col, row).color) ?: Modifier.BLUE

            try {
                sleep(100)
                state.playSound(R.raw.fill)

                doFill(col, row)

                sleep(100)

                doFill(col + 1, row)
                doFill(col, row + 1)
                doFill(col - 1, row)
                doFill(col, row - 1)

                sleep(100)

                doFill(col + 1, row - 1)
                doFill(col + 1, row + 1)
                doFill(col - 1, row - 1)
                doFill(col - 1, row + 1)

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            runOnFinished()
        }
    }

    private fun doFill(col: Int, row: Int) {
        if (row >= 0 && col >= 0 && row < levelData.height && col < levelData.width) {
            val f = levelData.fieldAt(col, row)
            if (f.modifier != Modifier.TRANSPARENT) {
                f.modifier = fillTo
            }
        }
    }
}
