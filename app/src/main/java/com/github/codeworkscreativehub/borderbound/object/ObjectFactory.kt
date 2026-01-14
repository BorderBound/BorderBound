package com.github.codeworkscreativehub.borderbound.`object`

class ObjectFactory {
    companion object {
        fun createSingleBox(texX: Int, texY: Int, boxSize: Float): Plane {
            val coordinates = TextureCoordinates.getFromBlocks(texX, texY, texX + 1, texY + 1)
            return Plane(0f, 0f, boxSize, boxSize, coordinates)
        }
    }
}
