package com.github.codeworkscreativehub.borderbound.`object`

import com.github.codeworkscreativehub.borderbound.animation.Animation
import java.util.concurrent.ConcurrentLinkedQueue
import javax.microedition.khronos.opengles.GL10

abstract class Drawable {
    var x: Float = 0f
    var y: Float = 0f
    var scale: Float = 1f
    var isVisible: Boolean = true

    private val animations = ConcurrentLinkedQueue<Animation>()

    fun addAnimation(anim: Animation) {
        if (!animations.contains(anim)) {
            synchronized(animations) {
                animations.add(anim)
            }
        }
    }

    internal fun processAnimations() {
        synchronized(animations) {
            val i = animations.iterator()
            while (i.hasNext()) {
                val anim = i.next()

                if (anim.shouldBeDeleted) {
                    i.remove()
                } else {
                    anim.tick()
                }
            }
        }
    }

    fun cancelAnimations() {
        synchronized(animations) {
            animations.clear()
        }
    }

    abstract fun draw(gl: GL10)
}
