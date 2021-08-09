package com.teammetallurgy.metallurgyclassic.machines.chest;

import com.teammetallurgy.metallurgyclassic.blocks.MetallurgyBlocks;
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
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;
import static net.minecraft.client.render.TexturedRenderLayers.CHEST_ATLAS_TEXTURE;

public class MetalChestComponent {

    public enum Type {
        BRASS("brass", 6, 9),
        SILVER("silver", 8, 9),
        GOLD("gold", 9, 9),
        ELECTRUM("electrum", 9, 10),
        PLATINUM("platinum", 9, 12);

        public final String name;
        public final int rows;
        public final int cols;

        Type(String name, int rows, int cols) {
            this.name = name;
            this.rows = rows;
            this.cols = cols;
        }
    }

    private static class RegistryEntry {
        public Type type;
        public SpriteIdentifier spriteIdentifier;
        public MetalChestBlock block;
        public BlockEntityType<MetalChestEntity> blockEntityType;
        public ScreenHandlerType<MetalChestScreenHandler> screenHandlerType;
    }

    private static final Map<Type, RegistryEntry> chests = new HashMap<>();

    static {
        for(Type type : Type.values()) {
            RegistryEntry entry = new RegistryEntry();
            entry.type = type;
            entry.spriteIdentifier = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, id("entity/chest/" + type.name + "chest"));
            entry.block = new MetalChestBlock(FabricBlockSettings.of(Material.METAL).strength(2.5F).sounds(BlockSoundGroup.METAL), new Supplier<BlockEntityType<? extends MetalChestEntity>>() {
                @Override
                public BlockEntityType<? extends MetalChestEntity> get() {
                    return MetalChestComponent.getChestEntityType(type);
                }
            });
            entry.blockEntityType = FabricBlockEntityTypeBuilder.create(MetalChestEntity::new, entry.block).build();
            entry.screenHandlerType = ScreenHandlerRegistry.registerSimple(id(type.name + "_chest"), MetalChestScreenHandler.getScreenHandlerFactory(entry.type));
            chests.put(type, entry);
        }
    }

    public static void onInitialize() {
        for(RegistryEntry entry : chests.values()) {
            MetallurgyBlocks.registerWithEntity(entry.type.name + "_chest", entry.block, new FabricItemSettings().group(ItemGroup.MISC), entry.blockEntityType);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void onInitializerClient() {
        for(RegistryEntry entry : chests.values()) {
            ClientSpriteRegistryCallback.event(CHEST_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
                registry.register(entry.spriteIdentifier.getTextureId());
            });
            BlockEntityRendererRegistry.INSTANCE.register(entry.blockEntityType, MetalChestEntityRenderer::new);
            BuiltinItemRendererRegistry.INSTANCE.register(entry.block.asItem(), (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                MetalChestEntity renderEntity = new MetalChestEntity(entry.blockEntityType, BlockPos.ORIGIN, entry.block.getDefaultState());
                MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(renderEntity, matrices, vertexConsumers, light, overlay);
            });
            ScreenRegistry.register(entry.screenHandlerType, MetalChestScreen::new);
        }
    }

    public static Block getChestBlock(Type type) {
        return chests.get(type).block;
    }

    public static BlockEntityType<MetalChestEntity> getChestEntityType(Type type) {
        return chests.get(type).blockEntityType;
    }

    public static SpriteIdentifier getTexture(Type type) {
        return chests.get(type).spriteIdentifier;
    }

    public static ScreenHandlerType<?> getScreenHandler(Type type) {
        return chests.get(type).screenHandlerType;
    }

    public static Type getType(BlockEntityType<?> block) {
        for(RegistryEntry entry : chests.values()) {
            if (entry.blockEntityType == block) {
                return entry.type;
            }
        }
        return Type.BRASS;
    }

    public static Type getType(Block block) {
        for(RegistryEntry entry : chests.values()) {
            if (entry.block == block) {
                return entry.type;
            }
        }
        return Type.BRASS;
    }
}
