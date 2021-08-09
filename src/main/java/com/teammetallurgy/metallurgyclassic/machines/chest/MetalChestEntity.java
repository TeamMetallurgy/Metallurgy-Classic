package com.teammetallurgy.metallurgyclassic.machines.chest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.MOD_ID;

public class MetalChestEntity extends LootableContainerBlockEntity implements ChestAnimationProgress {
    private final MetalChestComponent.Type metalType;
    private DefaultedList<ItemStack> inventory;
    private final ChestStateManager stateManager;
    private final ChestLidAnimator lidAnimator;

    protected MetalChestEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.metalType = MetalChestComponent.getType(blockEntityType);
        this.inventory = DefaultedList.ofSize(metalType.cols * metalType.rows, ItemStack.EMPTY);
        this.stateManager = new ChestStateManager() {
            protected void onChestOpened(World world, BlockPos pos, BlockState state) {
                MetalChestEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_OPEN);
            }

            protected void onChestClosed(World world, BlockPos pos, BlockState state) {
                MetalChestEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_CLOSE);
            }

            protected void onInteracted(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                MetalChestEntity.this.onInvOpenOrClose(world, pos, state, oldViewerCount, newViewerCount);
            }

            protected boolean isPlayerViewing(PlayerEntity player) {
                if (!(player.currentScreenHandler instanceof MetalChestScreenHandler)) {
                    return false;
                } else {
                    Inventory inventory = ((MetalChestScreenHandler)player.currentScreenHandler).getInventory();
                    return inventory == MetalChestEntity.this;
                }
            }
        };
        this.lidAnimator = new ChestLidAnimator();
    }

    public MetalChestEntity(BlockPos pos, BlockState state) {
        this(MetalChestComponent.getChestEntityType(MetalChestComponent.getType(state.getBlock())), pos, state);
    }

    public int size() {
        return metalType.rows * metalType.cols;
    }

    protected Text getContainerName() {
        return new TranslatableText(MOD_ID + ".container.chest." + metalType.name);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }

    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }

        return nbt;
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, MetalChestEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

    static void playSound(World world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        double X = pos.getX() + 0.5D;
        double Y = pos.getY() + 0.5D;
        double Z = pos.getZ() + 0.5D;

        world.playSound(null, X, Y, Z, soundEvent, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.lidAnimator.setOpen(data > 0);
            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
    }

    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openChest(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeChest(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    public float getAnimationProgress(float tickDelta) {
        return this.lidAnimator.getProgress(tickDelta);
    }

    public static int getPlayersLookingInChestCount(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MetalChestEntity) {
                return ((MetalChestEntity)blockEntity).stateManager.getViewerCount();
            }
        }

        return 0;
    }

    public static void copyInventory(MetalChestEntity from, MetalChestEntity to) {
        DefaultedList<ItemStack> defaultedList = from.getInvStackList();
        from.setInvStackList(to.getInvStackList());
        to.setInvStackList(defaultedList);
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        MetalChestComponent.Type metalType = MetalChestComponent.getType(this.getType());
        return new MetalChestScreenHandler(MetalChestComponent.getScreenHandler(metalType), syncId, playerInventory, this, metalType);
    }

    public void onScheduledTick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    protected void onInvOpenOrClose(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.addSyncedBlockEvent(pos, block, 1, newViewerCount);
    }
}
