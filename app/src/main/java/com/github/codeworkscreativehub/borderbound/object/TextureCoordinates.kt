package com.github.codeworkscreativehub.borderbound.`object`

class TextureCoordinates(
    var fromX: Float,
    var fromY: Float,
    var toX: Float,
    var toY: Float
) {

    companion object {
        fun getFromBlocks(fromX: Int, fromY: Int, toX: Int, toY: Int): TextureCoordinates {
            val blockPercentage = 128f / 2048f
            val padding = 1f / 2048f
            return TextureCoordinates(
                fromX.toFloat() * blockPercentage + padding,
                fromY.toFloat() * blockPercentage + padding,
                toX.toFloat() * blockPercentage - padding,
                toY.toFloat() * blockPercentage - padding
            )
        }
    }
}
