package com.github.codeworkscreativehub.borderbound.`object`

import android.view.MotionEvent

class Plane(
    x: Float,
    y: Float,
    val width: Float,
    val height: Float,
    coordinates: TextureCoordinates
) : Mesh() {

    init {
        this.x = x
        this.y = y

        setIndices(
            shortArrayOf(
                0, 2, 1, 2, 3, 1
            )
        )
        setVertices(
            floatArrayOf(
                /* X,  Y,      Z */
                0.0f, 0f, 0.0f, // 0 - l.u.
                0.0f, height, 0.0f, // 1 - l.o.
                width, 0f, 0.0f, // 2 - r.u.
                width, height, 0.0f // 3 - r.o.
            )
        )
        updateTextureCoordinates(coordinates)
    }

    fun updateTextureCoordinates(coordinates: TextureCoordinates) {
        setTextureCoordinates(
            floatArrayOf(
                coordinates.fromX, coordinates.toY,    // 0 - l.u.
                coordinates.fromX, coordinates.fromY,  // 1 - l.o.
                coordinates.toX, coordinates.toY,    // 2 - r.u.
                coordinates.toX, coordinates.fromY   // 3 - r.o.
            )
        )
    }

    fun collides(event: MotionEvent, screenHeight: Float): Boolean {
        return collides(event.x, event.y, screenHeight)
    }

    fun collides(x: Float, y: Float, screenHeight: Float): Boolean {
        return y < screenHeight - this.y &&
                y > screenHeight - (this.y + height) &&
                x > this.x &&
                x < this.x + width
    }
}
