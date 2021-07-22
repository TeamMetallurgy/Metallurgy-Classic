package com.teammetallurgy.metallurgyclassic.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.explosion.Explosion;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;

public class MetallurgyBlocks {
    public static Block HE_TNT;
    public static Block LE_TNT;

    public static void initialize() {
        HE_TNT = register("he_tnt", new CustomizableTntBlock(FabricBlockSettings.of(Material.TNT).breakInstantly().sounds(BlockSoundGroup.GRASS), new CustomizableTntBlock.TntSettings().power(16.0f).destructionType(Explosion.DestructionType.DESTROY)), new FabricItemSettings().group(ItemGroup.REDSTONE));
        LE_TNT = register("le_tnt", new CustomizableTntBlock(FabricBlockSettings.of(Material.TNT).breakInstantly().sounds(BlockSoundGroup.GRASS), new CustomizableTntBlock.TntSettings().power(4.0f).destructionType(Explosion.DestructionType.NONE)), new FabricItemSettings().group(ItemGroup.REDSTONE));
    }

    public static Block register(String name, Block block, Item.Settings settings) {
        Registry.register(Registry.BLOCK, id(name), block);
        Registry.register(Registry.ITEM, id(name), new BlockItem(block, settings));
        return block;
    }
}
