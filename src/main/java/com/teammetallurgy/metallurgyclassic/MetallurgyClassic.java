package com.teammetallurgy.metallurgyclassic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetallurgyClassic implements ModInitializer {
	public static final String MOD_ID = "metallurgyclassic";

	@Override
	public void onInitialize() {
		// TODO: Move all of this to it's own class and store config, blocks and items in registry
		InputStream is = getClass().getResourceAsStream("/metals_data.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {
			var headersString = reader.readLine().split(",");
			System.out.println(headersString[0]);
			var headers = IntStream.range(0, headersString.length).boxed().collect(Collectors.toMap(i -> headersString[i].toLowerCase(), i -> i));
			reader.lines().forEach(str -> {
				System.out.println(str);
				var row = str.split(",");
				var config = new MetallurgyMetalConfig();
				var set = MetallurgyMetalConfig.MetalSet.get(row[headers.get("metal set")]);
				if (set != null) {
					config.set = set;
					config.name = row[headers.get("name")].toLowerCase().replace(" ", "_");
					// Copper has become a vanilla metal, but the sheet still lists it as a base metal.
					if(config.name.equals("copper")) {
						return;
					}
					config.type = MetallurgyMetalConfig.MetalType.get(row[headers.get("type")]);
					if(config.type == MetallurgyMetalConfig.MetalType.ALLOY) {
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

	public void createMetal(MetallurgyMetalConfig config) {
		if(config.type.hasOre()) {
			Block ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));
			Item RAW_ORE = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));

			Registry.register(Registry.BLOCK, new Identifier(MOD_ID, config.name + "_ore"), ORE);
			Registry.register(Registry.ITEM, new Identifier(MOD_ID, config.name + "_ore"), new BlockItem(ORE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
			Registry.register(Registry.ITEM, new Identifier(MOD_ID, "raw_" + config.name), RAW_ORE);

			MetallurgyOreGeneration.register(config, ORE.getDefaultState());
		}

		Block BLOCK = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, config.blockLevel).strength(3.0f, 3.0f));
		Block BRICKS = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, config.blockLevel).strength(3.0f, 3.0f));
		Item DUST = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
		Item INGOT = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));

		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, config.name + "_block"), BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, config.name + "_bricks"), BRICKS);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, config.name +  "_block"), new BlockItem(BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, config.name + "_bricks"), new BlockItem(BRICKS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, config.name + "_dust"), DUST);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, config.name + "_ingot"), INGOT);

		if(config.type.hasTools()) {
			Item PICKAXE = new ShovelItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, INGOT), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
			Registry.register(Registry.ITEM, new Identifier(MOD_ID, config.name + "_shovel"), PICKAXE);
		}
	}
}
