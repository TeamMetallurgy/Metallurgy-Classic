package com.teammetallurgy.metallurgyclassic;

import com.teammetallurgy.metallurgyclassic.items.IgniterItem;
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
import org.lwjgl.system.CallbackI;

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
		MetalRegistry.instance().parseCSV("/metals_data.csv");

		var magnesium_igniter = new IgniterItem(new FabricItemSettings().maxDamage(256).group(ItemGroup.TOOLS));
		var match = new IgniterItem(new FabricItemSettings().maxDamage(1).group(ItemGroup.TOOLS));
		var tar = new Item(new FabricItemSettings().group(ItemGroup.MISC));
		var fertilizer = new BoneMealItem(new FabricItemSettings().group(ItemGroup.MISC));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "match"), match);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "magnesium_igniter"), magnesium_igniter);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "tar"), tar);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "fertilizer"), fertilizer);
	}
}
