package com.teammetallurgy.metallurgyclassic.machines.furnace;

import com.teammetallurgy.metallurgyclassic.blocks.MetallurgyBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.state.property.Properties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;
import static com.teammetallurgy.metallurgyclassic.config.ModConfig.*;

public class MetalFurnaceComponent {

    private static ToIntFunction<BlockState> createLightLevelFromLitBlockState() {
        return (state) -> (Boolean)state.get(Properties.LIT) ? 13 : 0;
    }

    public enum Type {
        COPPER("copper"),
        BRONZE("bronze"),
        IRON("iron"),
        STEEL("steel");

        public final String name;

        Type(String name) {
            this.name = name;
        }
    }

    public static Map<Type, MetalFurnaceBlock> blocks = new HashMap<>();
    public static Map<Type, BlockEntityType<MetalFurnaceBlockEntity>> entityTypes = new HashMap<>();
    public static ScreenHandlerType<MetalFurnaceScreenHandler> COPPER_FURNACE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("metal_furnace"), MetalFurnaceScreenHandler::new);

    public static void onInitialize() {
        Arrays.stream(Type.values()).forEach(type -> {
            var block = new MetalFurnaceBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.5F).luminance(createLightLevelFromLitBlockState()));
            var entityType = FabricBlockEntityTypeBuilder.create(MetalFurnaceBlockEntity::new, block).build();
            MetallurgyBlocks.registerWithEntity(type.name + "_furnace", block, new FabricItemSettings().group(ItemGroup.MISC), entityType);
            blocks.put(type, block);
            entityTypes.put(type, entityType);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void onInitializerClient() {
        ScreenRegistry.register(COPPER_FURNACE_SCREEN_HANDLER, MetalFurnaceScreen::new);
    }

    public static Type getType(BlockEntity entity) {
        return Arrays.stream(Type.values()).filter(type -> entityTypes.get(type) == entity.getType()).findFirst().orElse(null);
    }

    public static BlockEntityType<MetalFurnaceBlockEntity> getEntityType(Block block) {
        Optional<Type> type = Arrays.stream(Type.values()).filter(x -> blocks.get(x) == block).findFirst();
        return type.map(value -> entityTypes.get(value)).orElse(null);
    }

    public static float getCookTimeMultiplier(MetalFurnaceBlockEntity inventory) {
        Type type = getType(inventory);
        if(type == Type.COPPER) {
            return COPPER_FURNACE_SMELT_TIME_SECONDS / 10F;
        }
        if(type == Type.BRONZE) {
            return BRONZE_FURNACE_SMELT_TIME_SECONDS / 10F;
        }
        if(type == Type.IRON) {
            return IRON_FURNACE_SMELT_TIME_SECONDS / 10F;
        }
        if(type == Type.STEEL) {
            return STEEL_FURNACE_SMELT_TIME_SECONDS / 10F;
        }
        return 1;
    }
}
