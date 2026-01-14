package com.github.codeworkscreativehub.borderbound.model

import android.content.Context
import com.github.codeworkscreativehub.borderbound.BuildConfig
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class LevelPack private constructor(
    val id: Int,
    fileName: String,
    context: Context
) {
    private val levels = ArrayList<Level>()

    init {
        try {
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val doc = db.parse(context.assets.open("$fileName.compressed"))
            doc.documentElement.normalize()

            val levelList = doc.documentElement.childNodes
            var indexInPack = 0

            for (i in 0 until levelList.length) {
                val node = levelList.item(i)
                if (node.nodeType != Node.ELEMENT_NODE) continue

                val levelEl = node as Element
                val number = if (BuildConfig.DEBUG_LEVELS) {
                    levelEl.getAttribute("number").toInt()
                } else {
                    indexInPack + 1
                }
                val colors = levelEl.getAttribute("color")
                val modifiers = levelEl.getAttribute("modifier")

                var optimalSteps = 0
                if (levelEl.hasAttribute("solution")) {
                    optimalSteps = levelEl.getAttribute("solution").split(",").size
                }

                levels.add(Level(indexInPack, number, this, colors, modifiers, optimalSteps))
                indexInPack++
            }

        } catch (e: Exception) {
            throw RuntimeException("Error loading level pack $fileName", e)
        }
    }

    fun getLevel(indexInPack: Int): Level {
        return levels[indexInPack]
    }

    fun size(): Int {
        return levels.size
    }

    val firstLevel: Level?
        get() = if (levels.isEmpty()) null else levels[0]

    fun isEasy(): Boolean = this === EASY
    fun isMedium(): Boolean = this === MEDIUM
    fun isHard(): Boolean = this === HARD
    fun isCommunity(): Boolean = this === COMMUNITY

    companion object {
        lateinit var EASY: LevelPack
        lateinit var MEDIUM: LevelPack
        lateinit var HARD: LevelPack
        lateinit var COMMUNITY: LevelPack

        /**
         * Initialize all LevelPack singletons
         */
        fun parsePacks(context: Context) {
            EASY = LevelPack(1, "levelsEasy.xml", context)
            MEDIUM = LevelPack(2, "levelsMedium.xml", context)
            HARD = LevelPack(3, "levelsHard.xml", context)
            COMMUNITY = LevelPack(4, "levelsCommunity.xml", context)
        }
    }
}
