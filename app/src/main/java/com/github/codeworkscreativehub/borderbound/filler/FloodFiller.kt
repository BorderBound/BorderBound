package com.github.codeworkscreativehub.borderbound.filler

import com.github.codeworkscreativehub.borderbound.Converter
import com.github.codeworkscreativehub.borderbound.R
import com.github.codeworkscreativehub.borderbound.model.Field
import com.github.codeworkscreativehub.borderbound.model.Level
import com.github.codeworkscreativehub.borderbound.model.Modifier
import com.github.codeworkscreativehub.borderbound.state.State
import java.util.ArrayDeque
import kotlin.concurrent.thread

class FloodFiller(
    private val levelData: Level,
    private val col: Int,
    private val row: Int,
    private val state: State
) : Filler() {

    private var somethingWasFilled = false
    private var fillFrom = Modifier.EMPTY
    private var fillTo = Modifier.BLUE

    override fun fill() {
        thread {
            try {
                somethingWasFilled = false
                fillFrom = Modifier.EMPTY
                fillTo = Converter.convertColor(levelData.fieldAt(col, row).color) ?: Modifier.BLUE
                floodBFS(col, row)

                if (!somethingWasFilled) {
                    fillFrom = Converter.convertColor(levelData.fieldAt(col, row).color) ?: Modifier.BLUE
                    fillTo = Modifier.EMPTY
                    floodBFS(col, row)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            runOnFinished()
        }
    }

    private inner class BfsNode(val col: Int, val row: Int, val distance: Int) {
        val field: Field
            get() = levelData.fieldAt(col, row)
    }

    @Throws(InterruptedException::class)
    private fun floodBFS(col: Int, row: Int) {
        levelData.unvisitAll()
        val queue = ArrayDeque<BfsNode>()

        levelData.fieldAt(col, row).isVisited = true
        queue.add(BfsNode(col, row, 0))
        var lastDistance = 1

        while (!queue.isEmpty()) {
            val node = queue.poll()
            // Java `poll` can return null, but ArrayDeque in Java throws if empty? 
            // `poll` returns null if empty. Kotlin assumes platform type.
            // But we check `!queue.isEmpty()` so it's safe.
            if (node == null) continue

            for (neighbor in getNeighbors(node)) {
                if (!neighbor.field.isVisited) {
                    neighbor.field.isVisited = true

                    if (neighbor.field.modifier == fillFrom) {
                        if (lastDistance != neighbor.distance) {
                            lastDistance = neighbor.distance
                            sleep(60)
                            state.playSound(R.raw.fill)
                        }
                        neighbor.field.modifier = fillTo
                        queue.add(neighbor)
                        somethingWasFilled = true
                    }
                }
            }
        }
    }

    private fun getNeighbors(node: BfsNode): ArrayList<BfsNode> {
        val neighbors = ArrayList<BfsNode>()
        if (node.col > 0) {
            neighbors.add(BfsNode(node.col - 1, node.row, node.distance + 1))
        }
        if (node.col < levelData.width - 1) {
            neighbors.add(BfsNode(node.col + 1, node.row, node.distance + 1))
        }
        if (node.row > 0) {
            neighbors.add(BfsNode(node.col, node.row - 1, node.distance + 1))
        }
        if (node.row < levelData.height - 1) {
            neighbors.add(BfsNode(node.col, node.row + 1, node.distance + 1))
        }
        return neighbors
    }
}
