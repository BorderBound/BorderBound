package com.github.codeworkscreativehub.borderbound.`object`

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

abstract class Mesh : Drawable() {
    private var mVerticesBuffer: FloatBuffer? = null
    private var mIndicesBuffer: ShortBuffer? = null
    private var mTextureBuffer: FloatBuffer? = null
    private var mNumOfIndices = -1

    override fun draw(gl: GL10) {
        if (!isVisible) {
            return
        }
        processAnimations()

        gl.glPushMatrix()
        gl.glTranslatef(x, y, 0f)
        gl.glScalef(scale, scale, scale)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticesBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer)
        gl.glDrawElements(GL10.GL_TRIANGLES, mNumOfIndices, GL10.GL_UNSIGNED_SHORT, mIndicesBuffer)

        gl.glPopMatrix()
    }

    internal fun setVertices(vertices: FloatArray) {
        // a float is 4 bytes, therefore we multiply the number if
        // vertices with 4.
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        mVerticesBuffer = vbb.asFloatBuffer()
        mVerticesBuffer?.put(vertices)
        mVerticesBuffer?.position(0)
    }

    internal fun setIndices(indices: ShortArray) {
        // short is 2 bytes, therefore we multiply the number if
        // vertices with 2.
        val ibb = ByteBuffer.allocateDirect(indices.size * 2)
        ibb.order(ByteOrder.nativeOrder())
        mIndicesBuffer = ibb.asShortBuffer()
        mIndicesBuffer?.put(indices)
        mIndicesBuffer?.position(0)
        mNumOfIndices = indices.size
    }

    internal fun setTextureCoordinates(textureCoords: FloatArray) {
        // float is 4 bytes, therefore we multiply the number of vertices with 4.
        val byteBuf = ByteBuffer.allocateDirect(textureCoords.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        mTextureBuffer = byteBuf.asFloatBuffer()
        mTextureBuffer?.put(textureCoords)
        mTextureBuffer?.position(0)
    }
}
