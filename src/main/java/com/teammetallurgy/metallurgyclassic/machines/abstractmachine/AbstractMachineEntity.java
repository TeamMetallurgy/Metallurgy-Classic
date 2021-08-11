package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractMachineEntity<S, T> extends LockableContainerBlockEntity {

    protected Consumer<S, T> consumer;
    protected Processor processor;
    protected Producer<T> producer;

    protected AbstractMachineEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractMachineEntity<?, ?> blockEntity) {
        blockEntity.tick();
    }

    public void tick() {
        T potentialOutput = consumer.getResult();
        boolean canContinue = consumer.canConsume() && producer.canProduce(potentialOutput) && processor.canProcess();
        processor.process(canContinue);
        T output = consumer.consume(canContinue);
        producer.produce(canContinue, output);
        this.markDirty();
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        consumer.readNbt(nbt.getCompound("consumer"));
        processor.readNbt(nbt.getCompound("processor"));
        producer.readNbt(nbt.getCompound("producer"));
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("consumer", consumer.writeNbt(new NbtCompound()));
        nbt.put("processor", processor.writeNbt(new NbtCompound()));
        nbt.put("producer", producer.writeNbt(new NbtCompound()));
        return nbt;
    }

    @Override
    public int size() {
        return consumer.size() + processor.size() + producer.size();
    }

    @Override
    public boolean isEmpty() {
        return consumer.isEmpty() && processor.isEmpty() && producer.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < consumer.size()) {
            return consumer.getStack(slot);
        } else {
            slot -= consumer.size();
            if (slot < processor.size()) {
                return processor.getStack(slot);
            } else {
                slot -= processor.size();
                return producer.getStack(slot);
            }
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < consumer.size()) {
            return consumer.removeStack(slot, amount);
        } else {
            slot -= consumer.size();
            if (slot < processor.size()) {
                return processor.removeStack(slot, amount);
            } else {
                slot -= processor.size();
                return producer.removeStack(slot, amount);
            }
        }
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < consumer.size()) {
            return consumer.removeStack(slot);
        } else {
            slot -= consumer.size();
            if (slot < processor.size()) {
                return processor.removeStack(slot);
            } else {
                slot -= processor.size();
                return producer.removeStack(slot);
            }
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < consumer.size()) {
            consumer.setStack(slot, stack);
        } else {
            slot -= consumer.size();
            if (slot < processor.size()) {
                processor.setStack(slot, stack);
            } else {
                slot -= processor.size();
                producer.setStack(slot, stack);
            }
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clear() {
        consumer.clear();
        processor.clear();
        producer.clear();
    }
}
