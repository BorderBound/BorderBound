package com.github.codeworkscreativehub.borderbound

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.opengl.GLUtils
import android.util.Log
import androidx.annotation.DrawableRes
import com.github.codeworkscreativehub.borderbound.`object`.Drawable
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(private val myContext: Context?) : GLSurfaceView.Renderer {
    private val textureDrawables = intArrayOf(R.drawable.texture_colorscheme_0, R.drawable.texture_colorscheme_1)
    val numberOfColorschemes: Int = textureDrawables.size

    private var width = 0
    private var height = 0
    private val textures = IntArray(1)
    private var onViewportSetupComplete: Runnable? = null
    private val objects = ArrayList<Drawable>()

    private var currentColorschemeIndex = 0
    private var reloadTextureNextFrame = false

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        gl.glLoadIdentity()

        if (reloadTextureNextFrame) {
            loadTexture(gl, 0, textureDrawables[currentColorschemeIndex])
            reloadTextureNextFrame = false
        }

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
        for (o in objects) {
            o.draw(gl)
        }
        debugOutput(gl)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        debugOutput(gl)
        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)

        gl.glFrontFace(GL10.GL_CCW)
        gl.glEnable(GL10.GL_CULL_FACE)
        gl.glCullFace(GL10.GL_BACK)
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)

        gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f)

        gl.glActiveTexture(GL10.GL_TEXTURE0)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glGenTextures(textures.size, textures, 0)
        loadTexture(gl, 0, textureDrawables[currentColorschemeIndex])

        debugOutput(gl)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        var height = height
        Log.d(TAG, "setupViewport")
        if (height == 0)  //Prevent A Divide By Zero By
            height = 1 //Making Height Equal One


        this.width = width
        this.height = height

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        GLU.gluOrtho2D(gl, 0f, width.toFloat(), 0f, height.toFloat())
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        debugOutput(gl)

        if (onViewportSetupComplete != null) {
            onViewportSetupComplete!!.run()
            onViewportSetupComplete = null
        }
    }

    fun setColorscheme(colorschemeIndex: Int) {
        // Return to default colorscheme if given an invalid colorschemeIndex.
        // For example, if the number of colorschemes decreases after an update.
        currentColorschemeIndex = if ((0 <= colorschemeIndex) && (colorschemeIndex < numberOfColorschemes)) {
            colorschemeIndex
        } else {
            0
        }

        reloadTextureNextFrame = true
    }

    private fun loadTexture(gl: GL10, position: Int, @DrawableRes resource: Int) {
        Log.d(TAG, "loadTexture")
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[position])
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(myContext?.resources, resource), 0)
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat())
    }

    fun getWidth(): Float {
        return width.toFloat()
    }

    fun getHeight(): Float {
        return height.toFloat()
    }

    fun addDrawable(d: Drawable?) {
        objects.add(d!!)
    }

    fun setOnViewportSetupComplete(onViewportSetupComplete: Runnable?) {
        this.onViewportSetupComplete = onViewportSetupComplete
    }

    companion object {
        private const val TAG = "GLRenderer"
        private fun debugOutput(gl: GL10) {
            val code = gl.glGetError()
            if (code != 0) {
                val errorString = when (code) {
                    GL10.GL_INVALID_ENUM -> "GL_INVALID_ENUM"
                    GL10.GL_INVALID_VALUE -> "GL_INVALID_VALUE"
                    GL10.GL_INVALID_OPERATION -> "GL_INVALID_OPERATION"
                    GL10.GL_STACK_OVERFLOW -> "GL_STACK_OVERFLOW"
                    GL10.GL_STACK_UNDERFLOW -> "GL_STACK_UNDERFLOW"
                    GL10.GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY"
                    else -> "unknown"
                }

                val elem = Exception().stackTrace[1]
                Log.e(
                    TAG, ("OpenGL error: " + errorString + " (" + code + ") in " + elem.className
                            + "/" + elem.methodName + ":" + elem.lineNumber)
                )
            }
        }
    }
}