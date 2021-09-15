package com.teammetallurgy.metallurgyclassic;

import com.teammetallurgy.metallurgyclassic.client.render.entities.CustomizableTntRenderer;
import com.teammetallurgy.metallurgyclassic.debug.OreScannerKeybind;
import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestComponent;
import com.teammetallurgy.metallurgyclassic.machines.crusher.CrusherComponent;
import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceComponent;
import com.teammetallurgy.metallurgyclassic.network.CustomizableTntSpawnPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;

@Environment(EnvType.CLIENT)
public class MetallurgyClassicClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MetalRegistry.instance().setOreRenderLayers();
        EntityRendererRegistry.INSTANCE.register(MetallurgyClassic.TNT_ENTITY_TYPE, CustomizableTntRenderer::new);
        ClientSidePacketRegistry.INSTANCE.register(CustomizableTntSpawnPacket.ID, CustomizableTntSpawnPacket::onPacket);

        MetalFurnaceComponent.onInitializerClient();
        MetalChestComponent.onInitializerClient();
        CrusherComponent.onInitializerClient();

        OreScannerKeybind.setup();

        MetalRegistry.instance().fixArmorTextures();
    }
}
