package com.github.codeworkscreativehub.borderbound

import com.github.codeworkscreativehub.borderbound.model.Color
import com.github.codeworkscreativehub.borderbound.model.Modifier

object Converter {

    /**
     * Converts a Modifier enum into a Color enum.
     * @param m The Modifier to convert.
     * @return The corresponding Color, or null if no match is found.
     */
    @JvmStatic
    fun convertColor(m: Modifier): Color? {
        return when (m) {
            Modifier.DARK -> Color.DARK
            Modifier.GREEN -> Color.GREEN
            Modifier.BLUE -> Color.BLUE
            Modifier.ORANGE -> Color.ORANGE
            Modifier.RED -> Color.RED
            Modifier.EMPTY -> Color.EMPTY
            else -> null
        }
    }

    /**
     * Converts a Color enum into a Modifier enum.
     * @param c The Color to convert.
     * @return The corresponding Modifier, or null if no match is found.
     */
    @JvmStatic
    fun convertColor(c: Color): Modifier? {
        return when (c) {
            Color.DARK -> Modifier.DARK
            Color.GREEN -> Modifier.GREEN
            Color.BLUE -> Modifier.BLUE
            Color.ORANGE -> Modifier.ORANGE
            Color.RED -> Modifier.RED
            Color.EMPTY -> Modifier.EMPTY
            // Added else to ensure safety and exhaustive matching
        }
    }
}