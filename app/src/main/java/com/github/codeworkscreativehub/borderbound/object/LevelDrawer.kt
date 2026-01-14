package com.github.codeworkscreativehub.borderbound.`object`

import android.annotation.SuppressLint
import com.github.codeworkscreativehub.borderbound.model.Color
import com.github.codeworkscreativehub.borderbound.model.Level
import com.github.codeworkscreativehub.borderbound.model.Modifier
import javax.microedition.khronos.opengles.GL10

class LevelDrawer private constructor() : Drawable() {

    private var level: Level? = null
    private lateinit var colors: Array<Plane>
    private lateinit var modifiers: Array<Plane>
    var boxSize: Float = 50f
        private set
    private var screenWidth: Float = 0f

    init {
        initialize()
    }

    private fun initialize() {
        colors = arrayOf(
            ObjectFactory.createSingleBox(8, 0, 1f),
            ObjectFactory.createSingleBox(10, 0, 1f),
            ObjectFactory.createSingleBox(12, 0, 1f),
            ObjectFactory.createSingleBox(14, 0, 1f),
            ObjectFactory.createSingleBox(8, 1, 1f),
            ObjectFactory.createSingleBox(15, 15, 1f)
        )

        modifiers = arrayOf(
            ObjectFactory.createSingleBox(9, 0, 1f),
            ObjectFactory.createSingleBox(11, 0, 1f),
            ObjectFactory.createSingleBox(13, 0, 1f),
            ObjectFactory.createSingleBox(15, 0, 1f),
            ObjectFactory.createSingleBox(9, 1, 1f),
            ObjectFactory.createSingleBox(8, 2, 1f),
            ObjectFactory.createSingleBox(10, 1, 1f),
            ObjectFactory.createSingleBox(15, 15, 1f),
            ObjectFactory.createSingleBox(10, 2, 1f),
            ObjectFactory.createSingleBox(9, 2, 1f),
            ObjectFactory.createSingleBox(11, 2, 1f),
            ObjectFactory.createSingleBox(12, 2, 1f),
            ObjectFactory.createSingleBox(13, 2, 1f),
            ObjectFactory.createSingleBox(10, 3, 1f),
            ObjectFactory.createSingleBox(9, 3, 1f),
            ObjectFactory.createSingleBox(11, 3, 1f),
            ObjectFactory.createSingleBox(12, 3, 1f)
        )
    }

    @Synchronized
    override fun draw(gl: GL10) {
        if (level == null || !isVisible) {
            return
        }

        processAnimations()

        gl.glPushMatrix()
        gl.glScalef(scale, scale, scale)

        val startY = y - boxSize
        val currentLevel = level ?: return
        for (col in 0 until currentLevel.width) {
            for (row in 0 until currentLevel.height) {
                val field = currentLevel.fieldAt(col, row)

                val color = getColorPlane(field.color)
                color.x = x + (col + 0.5f) * boxSize
                color.y = startY - row * boxSize
                color.draw(gl)

                val modifier = getModifierPlane(field.modifier)
                modifier.x = x + (col + 0.5f) * boxSize
                modifier.y = startY - row * boxSize
                modifier.draw(gl)
            }
        }

        gl.glPopMatrix()
    }

    private fun getModifierPlane(modifier: Modifier): Plane {
        return when (modifier) {
            Modifier.DARK -> modifiers[0]
            Modifier.GREEN -> modifiers[1]
            Modifier.BLUE -> modifiers[2]
            Modifier.ORANGE -> modifiers[3]
            Modifier.RED -> modifiers[4]
            Modifier.FLOOD -> modifiers[5]
            Modifier.EMPTY -> modifiers[6]
            Modifier.UP -> modifiers[8]
            Modifier.RIGHT -> modifiers[9]
            Modifier.LEFT -> modifiers[10]
            Modifier.DOWN -> modifiers[11]
            Modifier.ROTATE_UP -> modifiers[13]
            Modifier.ROTATE_RIGHT -> modifiers[14]
            Modifier.ROTATE_LEFT -> modifiers[15]
            Modifier.ROTATE_DOWN -> modifiers[16]
            Modifier.BOMB -> modifiers[12]
            else -> modifiers[7]
        }
    }

    private fun getColorPlane(color: Color): Plane {
        return when (color) {
            Color.DARK -> colors[0]
            Color.GREEN -> colors[1]
            Color.BLUE -> colors[2]
            Color.ORANGE -> colors[3]
            Color.RED -> colors[4]
            else -> colors[5]
        }
    }

    @Synchronized
    fun setLevel(level: Level) {
        this.level = level
        recalculateSizes()
    }

    private fun recalculateSizes() {
        val currentLevel = level ?: return

        this.boxSize = this.screenWidth / (currentLevel.width + 1).toFloat()
        for (color in colors) {
            color.scale = boxSize
        }
        for (modifier in modifiers) {
            modifier.scale = boxSize
        }
    }

    fun setScreenWidth(screenWidth: Float) {
        this.screenWidth = screenWidth
        recalculateSizes()
    }

    val height: Float
        get() = (level?.height ?: 0) * boxSize

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: LevelDrawer? = null

        fun getInstance(): LevelDrawer {
            if (instance == null) {
                instance = LevelDrawer()
            }
            return instance!!
        }
    }
}
