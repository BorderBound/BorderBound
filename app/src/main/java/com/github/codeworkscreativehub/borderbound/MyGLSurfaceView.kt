package com.github.codeworkscreativehub.borderbound

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MyGLSurfaceView : GLSurfaceView {
    val renderer: GLRenderer

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        this.renderer = GLRenderer(context)
        this.setRenderer(this.renderer)
    }

    constructor(context: Context?) : super(context) {
        this.renderer = GLRenderer(context)
        this.setRenderer(this.renderer)
    }
}