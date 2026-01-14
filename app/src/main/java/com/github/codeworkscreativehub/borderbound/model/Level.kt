package com.github.codeworkscreativehub.borderbound.model

class Level(
    val indexInPack: Int,
    val number: Int,
    val pack: LevelPack,
    color: String,
    modifier: String,
    val optimalSteps: Int
) {
    private val originalMap: Array<Array<Field>>
    private val map: Array<Array<Field>>

    val width: Int
        get() = map.size

    val height: Int
        get() = map[0].size

    init {
        val cleanColor = color.replace(Regex("\\s"), "")
        val cleanModifier = modifier.replace(Regex("\\s"), "")

        var w = 5
        var h = 6

        if (cleanColor.length == 6 * 8 && cleanModifier.length == 6 * 8) {
            w = 6
            h = 8
        }

        originalMap = Array(w) { col ->
            Array(h) { row ->
                val index = col + row * w
                Field(cleanColor[index], cleanModifier[index])
            }
        }

        // Initialize map with clones of originalMap
        map = Array(w) { col ->
            Array(h) { row ->
                originalMap[col][row].clone()
            }
        }
    }

    fun reset() {
        for (col in 0 until width) {
            for (row in 0 until height) {
                map[col][row] = originalMap[col][row].clone()
            }
        }
    }

    fun fieldAt(x: Int, y: Int): Field {
        return map[x][y]
    }

    fun unvisitAll() {
        for (col in 0 until width) {
            for (row in 0 until height) {
                map[col][row].isVisited = false
            }
        }
    }
}
