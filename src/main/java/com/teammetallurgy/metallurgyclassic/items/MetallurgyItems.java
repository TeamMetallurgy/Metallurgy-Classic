package com.teammetallurgy.metallurgyclassic.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;

public class MetallurgyItems {

    public static IgniterItem MAGNESIUM_IGNITER;
    public static IgniterItem MATCH;
    public static Item TAR;
    public static Item FERTILIZER;

    public static void initialize() {
        MAGNESIUM_IGNITER = (IgniterItem) register("magnesium_igniter", new IgniterItem(new FabricItemSettings().maxDamage(256).group(ItemGroup.TOOLS)));
        MATCH = (IgniterItem) register("match", new IgniterItem(new FabricItemSettings().maxDamage(1).group(ItemGroup.TOOLS)));
        TAR = register("tar", new Item(new FabricItemSettings().group(ItemGroup.MISC)));
        FERTILIZER = register("fertilizer", new BoneMealItem(new FabricItemSettings().group(ItemGroup.MISC)));

        MAGNESIUM_IGNITER.registerDispenserBehavior();
        MATCH.registerDispenserBehavior();
    }

	public static Item register(String name, Item item) {
        Registry.register(Registry.ITEM, id(name), item);
        return item;
    }
}
