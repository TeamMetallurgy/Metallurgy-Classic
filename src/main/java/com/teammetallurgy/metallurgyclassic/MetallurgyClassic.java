package com.teammetallurgy.metallurgyclassic;

import com.teammetallurgy.metallurgyclassic.blocks.MetallurgyBlocks;
import com.teammetallurgy.metallurgyclassic.entity.CustomizableTntEntity;
import com.teammetallurgy.metallurgyclassic.items.MetallurgyItems;
import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestComponent;
import com.teammetallurgy.metallurgyclassic.machines.crusher.CrusherComponent;
import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MetallurgyClassic implements ModInitializer {
	public static final String MOD_ID = "metallurgyclassic";

	public static final EntityType<CustomizableTntEntity> TNT_ENTITY_TYPE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("metallurgyclassic", "metal_tnt"),
			FabricEntityTypeBuilder.<CustomizableTntEntity>create(SpawnGroup.MISC, CustomizableTntEntity::new).build()
	);

	@Override
	public void onInitialize() {
		MetalRegistry.instance().parseCSV("/metals_data.csv");
		MetalRegistry.instance().createGold();
		MetalRegistry.instance().createIron();
		MetallurgyBlocks.initialize();
		MetallurgyItems.initialize();

		MetalFurnaceComponent.onInitialize();
		MetalChestComponent.onInitialize();
		CrusherComponent.onInitialize();
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
