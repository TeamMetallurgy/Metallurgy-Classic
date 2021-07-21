package com.teammetallurgy.metallurgyclassic;

import com.teammetallurgy.metallurgyclassic.materials.MetallurgyArmorMaterial;
import com.teammetallurgy.metallurgyclassic.materials.MetallurgyToolMaterial;
import com.teammetallurgy.metallurgyclassic.tools.MetallurgyAxeItem;
import com.teammetallurgy.metallurgyclassic.tools.MetallurgyHoeItem;
import com.teammetallurgy.metallurgyclassic.tools.MetallurgyPickaxeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetalRegistry {
    private class MetalEntry {
        public MetalConfig config;
        public Map<String, Item> items = new HashMap<>();
        public Map<String, Block> blocks = new HashMap<>();
    }

    private static final MetalRegistry instance = new MetalRegistry();

    private final Map<String, MetalEntry> registry = new HashMap<>();

    public static MetalRegistry instance() {
        return instance;
    }

    private MetalRegistry() {

    }

    public MetalConfig getConfig(String name) {
        if (registry.containsKey(name)) {
            return registry.get(name).config;
        }
        return null;
    }

    public Item getItem(String metal, String item) {
        return registry.get(metal).items.get(item);
    }

    public Block getBlock(String metal, String block) {
        return registry.get(metal).blocks.get(block);
    }

    public void parseCSV(String filename) {
        InputStream is = getClass().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            var headersString = reader.readLine().split(",");
            System.out.println(headersString[0]);
            var headers = IntStream.range(0, headersString.length).boxed().collect(Collectors.toMap(i -> headersString[i].toLowerCase(), i -> i));
            reader.lines().forEach(str -> {
                System.out.println(str);
                var row = str.split(",");
                var config = new MetalConfig();
                var set = MetalConfig.MetalSet.get(row[headers.get("metal set")]);
                if (set != null) {
                    config.set = set;
                    config.name = row[headers.get("name")].toLowerCase().replace(" ", "_");
                    // Copper has become a vanilla metal, but the sheet still lists it as a base metal.
                    if(config.name.equals("copper")) {
                        return;
                    }
                    config.type = MetalConfig.MetalType.get(row[headers.get("type")]);
                    if(config.type == MetalConfig.MetalType.ALLOY) {
                        var alloyRecipeString = row[headers.get("alloy recipe")];
                        var alloyRecipeArray = alloyRecipeString.split("\" \"");
                        alloyRecipeArray[0] = alloyRecipeArray[0].replace("\"", "").toLowerCase();
                        alloyRecipeArray[1] = alloyRecipeArray[1].replace("\"", "").toLowerCase();
                        config.alloyRecipe = new Pair<>(alloyRecipeArray[0], alloyRecipeArray[1]);
                    }
                    config.blockLevel = Integer.parseInt(row[headers.get("block lvl")]);
                    if(config.type.hasTools()) {
                        config.pickLevel = Integer.parseInt(row[headers.get("pick lvl")]);
                        config.toolDurability = Integer.parseInt(row[headers.get("durability")]);
                        config.toolDamage = Integer.parseInt(row[headers.get("damage")]);
                        config.toolSpeed = Integer.parseInt(row[headers.get("speed")]);
                        config.enchantability = Integer.parseInt(row[headers.get("enchantability")]);
                        config.helmetArmor = Integer.parseInt(row[headers.get("helmet armor")]);
                        config.chestplateArmor = Integer.parseInt(row[headers.get("chestplate armor")]);
                        config.leggingsArmor = Integer.parseInt(row[headers.get("leggings armor")]);
                        config.bootsArmor = Integer.parseInt(row[headers.get("boots armor")]);
                        config.armorDurability = Integer.parseInt(row[headers.get("durability multiplier")]);
                    }
                    if(config.type.hasOre()) {
                        config.veinsPerChunk = Integer.parseInt(row[headers.get("veins per chunk")]);
                        config.oresPerVein = Integer.parseInt(row[headers.get("ores per vein")]);
                        config.minLevel = Integer.parseInt(row[headers.get("min level")]);
                        config.maxLevel = Integer.parseInt(row[headers.get("max level")]);
                    }
                    createMetal(config);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMetal(MetalConfig config) {
        var entry = new MetalEntry();
        entry.config = config;
        if(config.type.hasOre()) {
            Block ore = new OreBlock(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));
            entry.blocks.put(Constants.BlockOre, ore);
            Item rawOre = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
            entry.items.put(Constants.ItemRawOre, rawOre);

            Registry.register(Registry.BLOCK, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_ore"), ore);
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_ore"), new BlockItem(ore, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "raw_" + config.name), rawOre);

            MetallurgyOreGeneration.register(config, ore.getDefaultState());
        }

        if(config.type.hasIngot()) {
            Block block = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, config.blockLevel).strength(3.0f, 3.0f));
            Block bricks = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, config.blockLevel).strength(3.0f, 3.0f));
            Item dust = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
            Item ingot = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
            entry.blocks.put(Constants.BlockBlock, block);
            entry.blocks.put(Constants.BlockBricks, bricks);
            entry.items.put(Constants.ItemDust, dust);
            entry.items.put(Constants.ItemIngot, ingot);

            Registry.register(Registry.BLOCK, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_block"), block);
            Registry.register(Registry.BLOCK, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_bricks"), bricks);
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_block"), new BlockItem(block, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_bricks"), new BlockItem(bricks, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_dust"), dust);
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_ingot"), ingot);

            if(config.type.hasTools()) {
                Item shovel = new ShovelItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
                Item hoe = new MetallurgyHoeItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
                Item axe = new MetallurgyAxeItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
                Item pickaxe = new MetallurgyPickaxeItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
                Item sword = new SwordItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.COMBAT));
                var armorMaterial = new MetallurgyArmorMaterial(config.name, config.armorDurability, new int[]{config.helmetArmor, config.chestplateArmor, config.leggingsArmor, config.bootsArmor}, config.enchantability, 0f, ingot);
                Item helmet = new ArmorItem(armorMaterial, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT));
                Item chestplate = new ArmorItem(armorMaterial, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT));
                Item leggings = new ArmorItem(armorMaterial, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT));
                Item boots = new ArmorItem(armorMaterial, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT));
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_shovel"), shovel);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_sword"), sword);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_hoe"), hoe);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_axe"), axe);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_pickaxe"), pickaxe);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_helmet"), helmet);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_chestplate"), chestplate);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_leggings"), leggings);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_boots"), boots);
                entry.items.put(Constants.ItemShovel, shovel);
                entry.items.put(Constants.ItemSword, sword);
                entry.items.put(Constants.ItemHoe, hoe);
                entry.items.put(Constants.ItemAxe, axe);
                entry.items.put(Constants.ItemPickaxe, pickaxe);
                entry.items.put(Constants.ItemHelmet, helmet);
                entry.items.put(Constants.ItemChestplate, chestplate);
                entry.items.put(Constants.ItemLeggings, leggings);
                entry.items.put(Constants.ItemBoots, boots);
            }
        }
        registry.put(config.name, entry);
    }
}
