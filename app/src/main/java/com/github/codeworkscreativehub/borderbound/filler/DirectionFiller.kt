package com.github.codeworkscreativehub.borderbound.filler

import com.github.codeworkscreativehub.borderbound.Converter
import com.github.codeworkscreativehub.borderbound.R
import com.github.codeworkscreativehub.borderbound.model.Level
import com.github.codeworkscreativehub.borderbound.model.Modifier
import com.github.codeworkscreativehub.borderbound.state.State
import kotlin.concurrent.thread

class DirectionFiller(
    private val levelData: Level,
    private val col: Int,
    private val row: Int,
    private val dx: Int,
    private val dy: Int,
    private val state: State
) : Filler() {

    private var somethingWasFilled = false
    private var fillFrom = Modifier.EMPTY
    private var fillTo = Modifier.BLUE

    override fun fill() {
        thread {
            somethingWasFilled = false

            fillFrom = Modifier.EMPTY
            fillTo = Converter.convertColor(levelData.fieldAt(col, row).color) ?: Modifier.BLUE

            doFill(col, row)
            if (!somethingWasFilled) {
                fillFrom = Converter.convertColor(levelData.fieldAt(col, row).color) ?: Modifier.BLUE
                fillTo = Modifier.EMPTY
                doFill(col, row)
            }

            runOnFinished()
        }
    }

    private fun doFill(col: Int, row: Int) {
        var x = col + dx
        var y = row + dy

        while (y >= 0 && x >= 0 && y < levelData.height && x < levelData.width
            && levelData.fieldAt(x, y).modifier == fillFrom
        ) {
            val f = levelData.fieldAt(x, y)
            somethingWasFilled = true
            f.modifier = fillTo
            state.playSound(R.raw.fill)
            sleep(40)

            x += dx
            y += dy
        }
    }
}
