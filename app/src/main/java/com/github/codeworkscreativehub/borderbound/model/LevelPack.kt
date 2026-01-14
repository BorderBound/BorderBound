package com.github.codeworkscreativehub.borderbound.model;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LevelPack {

    public static LevelPack EASY;
    public static LevelPack MEDIUM;
    public static LevelPack HARD;
    public static LevelPack COMMUNITY;
    private final List<Level> levels = new ArrayList<>();
    private final int id;

    private LevelPack(int id, String fileName, Context context) {
        this.id = id;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(context.getAssets().open(fileName + ".compressed"));
            doc.getDocumentElement().normalize();

            NodeList levelList = doc.getDocumentElement().getChildNodes();
            int indexInPack = 0;

            for (int i = 0; i < levelList.getLength(); i++) {
                Node node = levelList.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;

                Element levelEl = (Element) node;
                int number = Integer.parseInt(levelEl.getAttribute("number"));
                String colors = levelEl.getAttribute("color");
                String modifiers = levelEl.getAttribute("modifier");

                int optimalSteps = 0;
                if (levelEl.hasAttribute("solution")) {
                    optimalSteps = levelEl.getAttribute("solution").split(",").length;
                }

                levels.add(new Level(indexInPack, number, this, colors, modifiers, optimalSteps));
                indexInPack++;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading level pack " + fileName, e);
        }
    }

    /**
     * Initialize all LevelPack singletons
     */
    public static void parsePacks(Context context) {
        EASY = new LevelPack(1, "levelsEasy.xml", context);
        MEDIUM = new LevelPack(2, "levelsMedium.xml", context);
        HARD = new LevelPack(3, "levelsHard.xml", context);
        COMMUNITY = new LevelPack(4, "levelsCommunity.xml", context);
    }

    public Level getLevel(int indexInPack) {
        return levels.get(indexInPack);
    }

    public int size() {
        return levels.size();
    }

    public int id() {
        return id;
    }

    /**
     * Returns the first level in this pack
     */
    public Level getFirstLevel() {
        return levels.isEmpty() ? null : levels.get(0);
    }

    /**
     * Check if this LevelPack instance equals one of the predefined packs
     */
    public boolean isEasy() {
        return this == EASY;
    }

    public boolean isMedium() {
        return this == MEDIUM;
    }

    public boolean isHard() {
        return this == HARD;
    }

    public boolean isCommunity() {
        return this == COMMUNITY;
    }
}
