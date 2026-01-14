package com.github.codeworkscreativehub.borderbound.`object`

import javax.microedition.khronos.opengles.GL10

class Container : Drawable() {
    private val children = ArrayList<Drawable>()

    fun addDrawable(d: Drawable) {
        children.add(d)
    }

    override fun draw(gl: GL10) {
        if (!isVisible) {
            return
        }
        processAnimations()

        gl.glPushMatrix()
        gl.glTranslatef(x, y, 0f)
        gl.glScalef(scale, scale, scale)

        for (child in children) {
            child.draw(gl)
        }

        gl.glPopMatrix()
    }
}
