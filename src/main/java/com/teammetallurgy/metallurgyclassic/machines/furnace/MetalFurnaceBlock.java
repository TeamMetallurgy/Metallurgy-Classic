package com.teammetallurgy.metallurgyclassic.machines.furnace;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class MetalFurnaceBlock extends AbstractFurnaceBlock {
    protected MetalFurnaceBlock(Settings settings) {
        super(settings);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MetalFurnaceBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(world, type, MetalFurnaceComponent.getEntityType(state.getBlock()));
    }

    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MetalFurnaceBlockEntity) {
            player.openHandledScreen((NamedScreenHandlerFactory)blockEntity);
            player.incrementStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;
            if (random.nextDouble() < 0.1D) {
                world.playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.get(FACING);
            Direction.Axis axis = direction.getAxis();
            double offset = random.nextDouble() * 0.6D - 0.3D;
            double offsetX = axis == Direction.Axis.X ? direction.getOffsetX() * 0.52D : offset;
            double offsetY = random.nextDouble() * 6.0D / 16.0D;
            double offsetZ = axis == Direction.Axis.Z ? direction.getOffsetZ() * 0.52D : offset;
            world.addParticle(ParticleTypes.SMOKE, x + offsetX, y + offsetY, z + offsetZ, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.FLAME, x + offsetX, y + offsetY, z + offsetZ, 0.0D, 0.0D, 0.0D);
        }
    }
}
