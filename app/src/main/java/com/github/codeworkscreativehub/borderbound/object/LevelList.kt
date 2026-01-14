package com.github.codeworkscreativehub.borderbound.`object`

import android.view.MotionEvent
import com.github.codeworkscreativehub.borderbound.BuildConfig
import com.github.codeworkscreativehub.borderbound.model.Level
import com.github.codeworkscreativehub.borderbound.model.LevelPack
import com.github.codeworkscreativehub.borderbound.state.State
import javax.microedition.khronos.opengles.GL10

class LevelList(
    boxSize: Float,
    private val context: State
) : Drawable() {
    private val planeLevel: Plane
    private val planeLevelDone: Plane
    private val planeLevelPerfect: Plane
    private val planeLevelLocked: Plane
    private val number: Number
    private val boxHeight: Float = boxSize
    private val boxWidth: Float = boxSize
    private var pack: LevelPack? = null

    init {
        val coordinatesLevel = TextureCoordinates.getFromBlocks(6, 0, 7, 1)
        val coordinatesLevelDone = TextureCoordinates.getFromBlocks(7, 0, 8, 1)
        val coordinatesLevelPerfect = TextureCoordinates.getFromBlocks(6, 1, 7, 2)
        val coordinatesLevelLocked = TextureCoordinates.getFromBlocks(7, 1, 8, 2)
        planeLevel = Plane(0f, 0f, boxSize, boxSize, coordinatesLevel)
        planeLevelDone = Plane(0f, 0f, boxSize, boxSize, coordinatesLevelDone)
        planeLevelPerfect = Plane(0f, 0f, boxSize, boxSize, coordinatesLevelPerfect)
        planeLevelLocked = Plane(0f, 0f, boxSize, boxSize, coordinatesLevelLocked)
        number = Number()
        number.setFontSize(boxSize / 3)
    }

    val height: Float
        get() {
            val num = pack?.size() ?: 0
            return boxHeight * (num / 3) * 1.5f + boxHeight
        }

    private fun getXFor(num: Int): Float {
        return if (num % 3 == 0) {
            boxWidth / 2
        } else if (num % 3 == 1) {
            context.getScreenWidth() / 3 + boxWidth / 2
        } else {
            (context.getScreenWidth() / 3) * 2 + boxWidth / 2
        }
    }

    private fun getYFor(num: Int): Float {
        return -(num / 3) * boxHeight * 1.5f - boxHeight
    }

    private fun drawButton(indexInPack: Int, level: Level, gl: GL10) {
        val draw: Plane = if (context.isSolved(level.number)) {
            if (level.optimalSteps != 0 && context.loadSteps(level.number) <= level.optimalSteps) {
                planeLevelPerfect
            } else {
                planeLevelDone
            }
        } else if (!context.isPlayable(level)) {
            planeLevelLocked
        } else {
            planeLevel
        }

        draw.x = getXFor(indexInPack)
        draw.y = getYFor(indexInPack)
        draw.draw(gl)

        if (BuildConfig.DEBUG_LEVELS) {
            number.setValue(level.number)
        } else {
            number.setValue(indexInPack + 1)
        }
        number.x = draw.x + boxWidth + boxWidth / 4
        number.y = draw.y + boxHeight / 3
        number.draw(gl)
    }

    override fun draw(gl: GL10) {
        if (!isVisible) {
            return
        }
        processAnimations()

        gl.glPushMatrix()
        gl.glTranslatef(x, y, 0f)
        gl.glScalef(scale, scale, scale)

        if (pack != null) {
            val p = pack!!
            for (i in 0 until p.size()) {
                drawButton(i, p.getLevel(i), gl)
            }
        }

        gl.glPopMatrix()
    }

    fun setPack(pack: LevelPack) {
        this.pack = pack
    }

    fun collides(event: MotionEvent, height: Float): Boolean {
        return getCollision(event, height) != null
    }

    fun getCollision(event: MotionEvent, height: Float): Level? {
        val p = pack ?: return null
        for (i in 0 until p.size()) {
            planeLevel.x = getXFor(i)
            planeLevel.y = getYFor(i)
            // Use getY() which corresponds to 'y' property of this LevelList (likely translation)
            if (planeLevel.collides(event.x, event.y + y, height)) { // + y because of scroll offset likely
                return p.getLevel(i)
            }
        }
        return null
    }
}
