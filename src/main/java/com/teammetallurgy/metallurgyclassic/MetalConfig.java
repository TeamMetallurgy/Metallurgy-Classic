package com.teammetallurgy.metallurgyclassic;

import net.minecraft.util.Pair;
import org.apache.commons.lang3.Range;

import java.util.List;

public class MetalConfig {
    public enum MetalSet {
        BASE,
        PRECIOUS,
        NETHER,
        ENDER,
        UTILITY,
        FANTASY;

        public static MetalSet get(String name) {
            if(name.toLowerCase().contains("base")) {
                return BASE;
            }
            if(name.toLowerCase().contains("precious")) {
                return PRECIOUS;
            }
            if(name.toLowerCase().contains("nether")) {
                return NETHER;
            }
            if(name.toLowerCase().contains("fantasy")) {
                return UTILITY;
            }
            if(name.toLowerCase().contains("utility")) {
                return FANTASY;
            }
            if(name.toLowerCase().contains("ender")) {
                return ENDER;
            }
            return null;
        }
    }

    public enum MetalType {
        ORE,
        CATALYST,
        ALLOY,
        UTILITY;

        public static MetalType get(String name) {
            if(name.toLowerCase().contains("ore")) {
                return ORE;
            }
            if(name.toLowerCase().contains("catalyst")) {
                return CATALYST;
            }
            if(name.toLowerCase().contains("alloy")) {
                return ALLOY;
            }
            if(name.toLowerCase().contains("drop")) {
                return UTILITY;
            }
            return null;
        }

        public boolean hasTools() {
            return this == ORE || this == ALLOY;
        }

        public boolean hasOre() {
            return this == ORE || this == CATALYST || this == UTILITY;
        }

        public boolean hasIngot() { return this != UTILITY; }
    }

    public String name;
    public MetalSet set;
    public MetalType type;
    public Pair<String, String> alloyRecipe;

    // Tools
    public int blockLevel;
    public int pickLevel;
    public int toolDurability;
    public int toolDamage;
    public int toolSpeed;
    public int enchantability;

    // Armor
    public int helmetArmor;
    public int chestplateArmor;
    public int leggingsArmor;
    public int bootsArmor;
    public int armorDurability;

    // World
    public int veinsPerChunk;
    public int oresPerVein;
    public int minLevel;
    public int maxLevel;
    public List<Range<Integer>> dimensions;
}
