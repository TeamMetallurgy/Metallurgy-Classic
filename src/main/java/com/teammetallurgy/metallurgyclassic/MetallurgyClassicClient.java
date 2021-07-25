package com.teammetallurgy.metallurgyclassic;

import com.teammetallurgy.metallurgyclassic.client.render.entities.CustomizableTntRenderer;
import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceComponent;
import com.teammetallurgy.metallurgyclassic.network.CustomizableTntSpawnPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

@Environment(EnvType.CLIENT)
public class MetallurgyClassicClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(MetallurgyClassic.TNT_ENTITY_TYPE, CustomizableTntRenderer::new);
        ClientSidePacketRegistry.INSTANCE.register(CustomizableTntSpawnPacket.ID, CustomizableTntSpawnPacket::onPacket);

        MetalFurnaceComponent.onInitializerClient();
    }
}
