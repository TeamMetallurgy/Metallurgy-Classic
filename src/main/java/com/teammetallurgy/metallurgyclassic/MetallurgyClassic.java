package com.teammetallurgy.metallurgyclassic;

import com.teammetallurgy.metallurgyclassic.blocks.MetallurgyBlocks;
import com.teammetallurgy.metallurgyclassic.entity.CustomizableTntEntity;
import com.teammetallurgy.metallurgyclassic.items.MetallurgyItems;
import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestComponent;
import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
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
		MetallurgyBlocks.initialize();
		MetallurgyItems.initialize();

		MetalFurnaceComponent.onInitialize();
		MetalChestComponent.onInitialize();
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
