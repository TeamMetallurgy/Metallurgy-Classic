package com.teammetallurgy.metallurgyclassic.machines.crusher;

import com.teammetallurgy.metallurgyclassic.machines.abstractmachine.AbstractMachineBlock;
import com.teammetallurgy.metallurgyclassic.machines.abstractmachine.AbstractMachineEntity;
import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceBlockEntity;
import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceComponent;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CrusherBlock extends AbstractMachineBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty LIT;
    protected static final VoxelShape SINGLE_SHAPE;

    static {
        FACING = HorizontalFacingBlock.FACING;
        LIT = Properties.LIT;
        SINGLE_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
    }

    protected CrusherBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrusherBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SINGLE_SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, CrusherComponent.getEntityType(state.getBlock()), AbstractMachineEntity::tick);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrusherBlockEntity) {
            player.openHandledScreen((NamedScreenHandlerFactory)blockEntity);
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            double d = (double) pos.getX() + 0.5D;
            double e = (double) pos.getY() + 1.0D;
            double f = (double) pos.getZ() + 0.5D;
            if (random.nextDouble() < 0.1D) {
                world.playSound(d, e, f, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.get(FACING);
            Direction.Axis axis = direction.getAxis();
            double g = 0.52D;
            double h = random.nextDouble() * 0.8D - 0.4D;
            double minRandom = random.nextDouble() * 0.3D - 0.15D;
            double i = axis == Direction.Axis.X ? minRandom : h;
            double j = 0; //random.nextDouble() * 6.0D / 16.0D;
            double k = axis == Direction.Axis.Z ? minRandom : h;
            world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0D, 0.0D, 0.0D);
            if (random.nextDouble() < 0.9f)
                world.addParticle(ParticleTypes.FLAME, d + i, e + j - 0.1f, f + k, 0.0D, 0.0D, 0.0D);
            //entity.getCrushingBlock();
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.IRON_ORE.getDefaultState()), d + i, e + j, f + k, 0.0D, 0.0D, 0.0D);
        }
    }
}
