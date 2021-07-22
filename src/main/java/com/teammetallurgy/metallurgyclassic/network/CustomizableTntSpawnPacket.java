package com.teammetallurgy.metallurgyclassic.network;

import com.teammetallurgy.metallurgyclassic.entity.CustomizableTntEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;

public class CustomizableTntSpawnPacket extends EntitySpawnS2CPacket {
    public static final Identifier ID;
    private String blockId;

    public CustomizableTntSpawnPacket(PacketByteBuf buf) {
        super(buf);
    }

    public CustomizableTntSpawnPacket(CustomizableTntEntity entity) {
        super(entity);
        this.setBlockId(entity.getBlock());
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeString(blockId);
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getBlockId() {
        return blockId;
    }

    @Environment(EnvType.CLIENT)
    public static void onPacket(PacketContext ctx, PacketByteBuf byteBuf) {
        CustomizableTntSpawnPacket packet = new CustomizableTntSpawnPacket(byteBuf);
        EntityType<?> entityType = packet.getEntityTypeId();
        ctx.getTaskQueue().execute(() -> {
            Entity entity = entityType.create(MinecraftClient.getInstance().world);
            if (entity != null) {
                entity.onSpawnPacket(packet);
                int i = packet.getId();
                MinecraftClient.getInstance().world.addEntity(i, entity);
            }
        });
    }

    static {
        ID = id("spawn_tnt");
    }
}
