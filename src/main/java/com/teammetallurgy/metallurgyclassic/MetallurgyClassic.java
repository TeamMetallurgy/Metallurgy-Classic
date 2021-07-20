package com.teammetallurgy.metallurgyclassic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MetallurgyClassic implements ModInitializer {
	public static final String MOD_ID = "metallurgyclassic";

	@Override
	public void onInitialize() {
		createMetal("tin", "catalyst");
		createMetal("silver", "metal");
		createMetal("bronze", "alloy");
	}

	public void createMetal(String name, String type) {
		if(!type.equals("alloy")) {
			Block ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));
			Item RAW_ORE = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));

			Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name + "_ore"), ORE);
			Registry.register(Registry.ITEM, new Identifier(MOD_ID, name + "_ore"), new BlockItem(ORE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
			Registry.register(Registry.ITEM, new Identifier(MOD_ID, "raw_" + name), RAW_ORE);

			var tinOreGenConfig = new MetallurgyOreConfig(ORE, name).size(16).amount(20).density(0.5f).minHeight(0).maxHeight(64);
			MetallurgyOreGeneration.register(tinOreGenConfig);
		}

		Block BLOCK = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));
		Block BRICKS = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));
		Item DUST = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
		Item INGOT = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));

		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name + "_block"), BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name + "_bricks"), BRICKS);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, name +  "_block"), new BlockItem(BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, name + "_bricks"), new BlockItem(BRICKS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, name + "_dust"), DUST);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, name + "_ingot"), INGOT);

	}
}
