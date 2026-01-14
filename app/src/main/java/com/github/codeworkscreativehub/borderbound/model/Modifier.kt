package com.github.codeworkscreativehub.borderbound.model

enum class Modifier {
    DARK, GREEN, BLUE, ORANGE, RED, EMPTY, TRANSPARENT,
    FLOOD, BOMB,
    UP, RIGHT, LEFT, DOWN,
    ROTATE_UP, ROTATE_RIGHT, ROTATE_LEFT, ROTATE_DOWN;

    fun isRotating(): Boolean {
        return this == ROTATE_DOWN || this == ROTATE_UP
                || this == ROTATE_LEFT || this == ROTATE_RIGHT
    }

    fun rotate(): Modifier {
        return when (this) {
            ROTATE_UP -> ROTATE_RIGHT
            ROTATE_RIGHT -> ROTATE_DOWN
            ROTATE_LEFT -> ROTATE_UP
            ROTATE_DOWN -> ROTATE_LEFT
            else -> TRANSPARENT
        }
    }
}
