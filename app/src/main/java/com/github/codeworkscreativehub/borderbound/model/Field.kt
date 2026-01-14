package com.github.codeworkscreativehub.borderbound.model

class Field {
    var color: Color
    var modifier: Modifier
    var isVisited: Boolean = false

    constructor(color: Char, modifier: Char) {
        this.color = when (color) {
            'r' -> Color.RED
            'g' -> Color.GREEN
            'b' -> Color.BLUE
            'o' -> Color.ORANGE
            'd' -> Color.DARK
            else -> Color.EMPTY
        }
        this.modifier = when (modifier) {
            'r' -> Modifier.RED
            'g' -> Modifier.GREEN
            'b' -> Modifier.BLUE
            'o' -> Modifier.ORANGE
            'd' -> Modifier.DARK
            'F' -> Modifier.FLOOD
            'U' -> Modifier.UP
            'R' -> Modifier.RIGHT
            'L' -> Modifier.LEFT
            'D' -> Modifier.DOWN
            'w' -> Modifier.ROTATE_UP
            'x' -> Modifier.ROTATE_RIGHT
            'a' -> Modifier.ROTATE_LEFT
            's' -> Modifier.ROTATE_DOWN
            'B' -> Modifier.BOMB
            '0' -> Modifier.EMPTY
            else -> Modifier.TRANSPARENT
        }
    }

    constructor(color: Color, modifier: Modifier) {
        this.color = color
        this.modifier = modifier
    }

    fun clone(): Field {
        return Field(color, modifier)
    }
}
