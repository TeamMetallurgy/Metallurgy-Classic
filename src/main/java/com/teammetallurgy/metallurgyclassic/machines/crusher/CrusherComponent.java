package com.teammetallurgy.metallurgyclassic.machines.crusher;

import com.teammetallurgy.metallurgyclassic.blocks.MetallurgyBlocks;
import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestComponent;
import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestEntity;
import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestEntityRenderer;
import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestScreen;
import com.teammetallurgy.metallurgyclassic.machines.furnace.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;
import static com.teammetallurgy.metallurgyclassic.config.ModConfig.*;
import static com.teammetallurgy.metallurgyclassic.config.ModConfig.STEEL_FURNACE_SMELT_TIME_SECONDS;
import static net.minecraft.client.render.TexturedRenderLayers.CHEST_ATLAS_TEXTURE;

public class CrusherComponent {

    private static ToIntFunction<BlockState> createLightLevelFromLitBlockState() {
        return (state) -> (Boolean)state.get(Properties.LIT) ? 13 : 0;
    }

    public enum Type {
        STONE("stone"),
        COPPER("copper"),
        BRONZE("bronze"),
        IRON("iron"),
        STEEL("steel");

        public final String name;

        Type(String name) {
            this.name = name;
        }
    }

    public static Map<CrusherComponent.Type, CrusherBlock> blocks = new HashMap<>();
    public static Map<CrusherComponent.Type, BlockEntityType<CrusherBlockEntity>> entityTypes = new HashMap<>();
    public static ScreenHandlerType<CrusherScreenHandler> CRUSHER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("crusher"), CrusherScreenHandler::new);

    public static void onInitialize() {
        Arrays.stream(CrusherComponent.Type.values()).forEach(type -> {
            var block = new CrusherBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.5F).luminance(createLightLevelFromLitBlockState()));
            var entityType = FabricBlockEntityTypeBuilder.create(CrusherBlockEntity::new, block).build();
            MetallurgyBlocks.registerWithEntity(type.name + "_crusher", block, new FabricItemSettings().group(ItemGroup.MISC), entityType);
            blocks.put(type, block);
            entityTypes.put(type, entityType);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void onInitializerClient() {
        ScreenRegistry.register(CRUSHER_SCREEN_HANDLER, CrusherScreen::new);

        for(Type type : Type.values()) {
            SpriteIdentifier spriteIdentifier = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, id("entity/crusher/crusher_" + type.name));
            SpriteIdentifier spriteIdentifierLit = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, id("entity/crusher/crusher_" + type.name + "_lit"));
            ClientSpriteRegistryCallback.event(CHEST_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
                registry.register(spriteIdentifier.getTextureId());
                registry.register(spriteIdentifierLit.getTextureId());
            });

            BlockEntityRendererRegistry.INSTANCE.register(entityTypes.get(type), CrusherEntityRenderer::new);
            BuiltinItemRendererRegistry.INSTANCE.register(blocks.get(type).asItem(), (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                CrusherBlockEntity renderEntity = new CrusherBlockEntity(BlockPos.ORIGIN, blocks.get(type).getDefaultState());
                MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(renderEntity, matrices, vertexConsumers, light, overlay);
            });
        }
    }

    public static CrusherComponent.Type getType(BlockEntity entity) {
        return Arrays.stream(CrusherComponent.Type.values()).filter(type -> entityTypes.get(type) == entity.getType()).findFirst().orElse(null);
    }

    public static BlockEntityType<CrusherBlockEntity> getEntityType(Block block) {
        Optional<CrusherComponent.Type> type = Arrays.stream(CrusherComponent.Type.values()).filter(x -> blocks.get(x) == block).findFirst();
        return type.map(value -> entityTypes.get(value)).orElse(null);
    }

    public static CrusherBlock getBlock(Type type) {
        return blocks.get(type);
    }


    public static float getBaseTimeSeconds(CrusherBlockEntity inventory) {
        CrusherComponent.Type type = getType(inventory);
        if(type == CrusherComponent.Type.STONE) {
            return STONE_CRUSHER_TIME_SECONDS;
        }
        if(type == CrusherComponent.Type.COPPER) {
            return COPPER_CRUSHER_TIME_SECONDS;
        }
        if(type == CrusherComponent.Type.BRONZE) {
            return BRONZE_CRUSHER_TIME_SECONDS;
        }
        if(type == CrusherComponent.Type.IRON) {
            return IRON_CRUSHER_TIME_SECONDS;
        }
        if(type == CrusherComponent.Type.STEEL) {
            return STEEL_CRUSHER_TIME_SECONDS;
        }
        return 1;
    }
}
