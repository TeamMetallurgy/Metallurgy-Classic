package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.lwjgl.system.CallbackI;

import java.util.Map;

public class ItemFuelProcessor extends AbstractMachineComponent implements Processor {

    private final Map<Item, Integer> fuelTimes;

    public ItemFuelProcessor() {
        super(1);
        this.fuelTimes = AbstractFurnaceBlockEntity.createFuelTimeMap();
    }

    @Override
    public State state() {
        return null;
    }

    public int initialFuelBurnTime;
    public int currentFuelBurnTime;

    @Override
    public void process(boolean canContinue) {
        System.out.println(canContinue);
        System.out.println(currentFuelBurnTime + " / " + initialFuelBurnTime);
        System.out.println(getFuelTime(input()));
        if(currentFuelBurnTime > 0) {
            currentFuelBurnTime--;
        } else if (canContinue) {
            int fuelBurnTime = getFuelTime(input());
            if(fuelBurnTime > 0) {
                initialFuelBurnTime = fuelBurnTime;
                currentFuelBurnTime = initialFuelBurnTime;
                input().decrement(1);
            }
        }
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.currentFuelBurnTime = nbt.getInt("current_progress");
        this.initialFuelBurnTime = nbt.getInt("initial_progress");
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt = super.writeNbt(nbt);
        nbt.putInt("current_progress", this.currentFuelBurnTime);
        nbt.putInt("initial_progress", this.initialFuelBurnTime);
        return nbt;
    }


    @Override
    public boolean canProcess() {
        if (currentFuelBurnTime > 0) {
            return true;
        } else {
            return fuelTimes.containsKey(input().getItem());
        }
    }

    private int getFuelTime(ItemStack stack) {
        return fuelTimes.getOrDefault(stack.getItem(), 0);
    }

    public ItemStack input() {
        if (inventory.size() > 0) {
            return inventory.get(0);
        }
        return ItemStack.EMPTY;
    }

    public float getFuelLevel() {
        if (this.initialFuelBurnTime == 0) {
            return 0;
        }
        return this.currentFuelBurnTime / (float)this.initialFuelBurnTime;
    }
}
