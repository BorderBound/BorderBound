package com.github.codeworkscreativehub.borderbound.`object`

import javax.microedition.khronos.opengles.GL10

class Number : Drawable() {

    private var value = 0
    private var fontSize = 10f

    override fun draw(gl: GL10) {
        if (!isVisible) {
            return
        }
        processAnimations()

        gl.glPushMatrix()
        gl.glTranslatef(x, y, 0f)
        gl.glScalef(
            scale * fontSize * 1.5f / LETTER_SIZE,
            scale * fontSize * 1.5f / LETTER_SIZE,
            scale * fontSize * 1.5f / LETTER_SIZE
        )

        if (value == VALUE_NAN) {
            LETTERS[LETTER_INDEX_NAN].draw(gl)
        } else {
            val valueString = value.toString()
            for (i in valueString.indices) {
                LETTERS[valueString[i] - '0'].draw(gl)
                gl.glTranslatef(LETTER_SIZE * 0.6f, 0f, 0f)
            }
        }

        gl.glPopMatrix()
    }

    fun getValue(): Int {
        return value
    }

    fun setValue(value: Int) {
        this.value = value
    }

    fun increment() {
        this.value++
    }

    fun setFontSize(size: Float) {
        this.fontSize = size
    }

    companion object {
        const val VALUE_NAN = -42424242
        private const val LETTER_SIZE = 100f
        private const val LETTER_INDEX_NAN = 10
        private val LETTERS = arrayOf(
            ObjectFactory.createSingleBox(11, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(12, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(13, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(14, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(15, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(11, 9, LETTER_SIZE),
            ObjectFactory.createSingleBox(12, 9, LETTER_SIZE),
            ObjectFactory.createSingleBox(13, 9, LETTER_SIZE),
            ObjectFactory.createSingleBox(14, 9, LETTER_SIZE),
            ObjectFactory.createSingleBox(15, 9, LETTER_SIZE),
            ObjectFactory.createSingleBox(15, 10, LETTER_SIZE)
        )
    }
}
