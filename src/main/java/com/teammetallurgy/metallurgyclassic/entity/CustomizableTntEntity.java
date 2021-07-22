package com.teammetallurgy.metallurgyclassic.entity;

import com.teammetallurgy.metallurgyclassic.MetallurgyClassic;
import com.teammetallurgy.metallurgyclassic.blocks.CustomizableTntBlock;
import com.teammetallurgy.metallurgyclassic.network.CustomizableTntSpawnPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class CustomizableTntEntity extends Entity {
    private static final TrackedData<Integer> FUSE;
    private static final int DEFAULT_FUSE = 80;
    @Nullable
    private LivingEntity causingEntity;
    public Block tntBlock;
    public CustomizableTntBlock.TntSettings tntSettings;

    public CustomizableTntEntity(EntityType<? extends CustomizableTntEntity> entityType, World world) {
        super(entityType, world);
        this.inanimate = true;
    }

    public CustomizableTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, CustomizableTntBlock.TntSettings tntSettings, Block tntBlock) {
        this(MetallurgyClassic.TNT_ENTITY_TYPE, world);
        this.setPosition(x, y, z);
        double d = world.random.nextDouble() * 6.2831854820251465D;
        this.setVelocity(-Math.sin(d) * 0.02D, 0.20000000298023224D, -Math.cos(d) * 0.02D);
        this.setFuse(80);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.causingEntity = igniter;
        this.tntSettings = tntSettings;
        this.tntBlock = tntBlock;
    }

    public String getBlock() {
        return Registry.BLOCK.getId(tntBlock).toString();
    }

    public void setBlock(String blockId) {
        this.tntBlock = Registry.BLOCK.get(new Identifier(blockId));
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking(FUSE, 80);
    }

    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    public boolean collides() {
        return !this.isRemoved();
    }

    public void tick() {
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));
        }

        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98D));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.7D, -0.5D, 0.7D));
        }

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.world.isClient) {
                this.explode();
            }
        } else {
            this.updateWaterState();
            if (this.world.isClient) {
                this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    private void explode() {
        this.world.createExplosion(this, this.getX(), this.getBodyY(0.1625D), this.getZ(), this.tntSettings.power, false, this.tntSettings.destructionType);
    }

    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putShort("Fuse", (short)this.getFuse());
        nbt.putString("Block", Registry.BLOCK.getId(tntBlock).toString());
        nbt.putFloat("Power", tntSettings.power);
        nbt.putString("DestructionType", tntSettings.destructionType.name());
    }

    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setFuse(nbt.getShort("Fuse"));
        this.tntBlock = Registry.BLOCK.get(new Identifier(nbt.getString("Block")));
        this.tntSettings = new CustomizableTntBlock.TntSettings();
        this.tntSettings.power = nbt.getFloat("Power");
        this.tntSettings.destructionType = Explosion.DestructionType.valueOf(nbt.getString("DestructionType"));
    }

    @Nullable
    public LivingEntity getCausingEntity() {
        return this.causingEntity;
    }

    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.15F;
    }

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    public int getFuse() {
        return (Integer)this.dataTracker.get(FUSE);
    }

    public Packet<?> createSpawnPacket() {
        return new CustomizableTntSpawnPacket(this);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        if(packet instanceof CustomizableTntSpawnPacket) {
            setBlock(((CustomizableTntSpawnPacket) packet).getBlockId());
        }
    }

    static {
        FUSE = DataTracker.registerData(CustomizableTntEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
