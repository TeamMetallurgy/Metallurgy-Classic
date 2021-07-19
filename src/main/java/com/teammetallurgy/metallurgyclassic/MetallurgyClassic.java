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

	public static final Block TIN_ORE = new OreBlock(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));
	public static final Block TIN_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));
	public static final Block TIN_BRICKS = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));
	public static final Item RAW_TIN = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
	public static final Item TIN_DUST = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
	public static final Item TIN_INGOT = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "tin_ore"), TIN_ORE);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "tin_block"), TIN_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "tin_bricks"), TIN_BRICKS);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "tin_ore"), new BlockItem(TIN_ORE, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "tin_block"), new BlockItem(TIN_BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "tin_bricks"), new BlockItem(TIN_BRICKS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "raw_tin"), RAW_TIN);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "tin_dust"), TIN_DUST);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "tin_ingot"), TIN_INGOT);
	}
}
