package com.teammetallurgy.metallurgyclassic.machines.crusher;

import com.teammetallurgy.metallurgyclassic.machines.abstractmachine.AbstractMachineBlock;
import com.teammetallurgy.metallurgyclassic.machines.abstractmachine.AbstractMachineEntity;
import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceBlockEntity;
import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrusherBlock extends AbstractMachineBlock {
    protected CrusherBlock(Settings settings) {
        super(settings);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrusherBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, CrusherComponent.getEntityType(state.getBlock()), AbstractMachineEntity::tick);
    }

    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        System.out.println("test1");
        if (blockEntity instanceof CrusherBlockEntity) {
            System.out.println("test2");
            player.openHandledScreen((NamedScreenHandlerFactory)blockEntity);
            player.incrementStat(Stats.INTERACT_WITH_FURNACE);
        }
    }
}
