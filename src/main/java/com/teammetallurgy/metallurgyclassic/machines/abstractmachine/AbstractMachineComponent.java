package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import org.lwjgl.system.Pointer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AbstractMachineComponent implements AbstractComponentInterface {

    protected DefaultedList<ItemStack> inventory;

    public AbstractMachineComponent() {
        inventory = DefaultedList.ofSize(0);
    }

    public AbstractMachineComponent(int size) {
        inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    public void readNbt(NbtCompound nbt) {
        int size = nbt.getInt("inventory_size");
        inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("inventory_size", inventory.size());
        Inventories.writeNbt(nbt, inventory);
        return nbt;
    }

    public int size() {
        return inventory.size();
    }

    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        this.inventory.set(slot, stack);
        if (stack.getCount() > stack.getMaxCount()) {
            stack.setCount(stack.getMaxCount());
        }
    }

    public boolean isEmpty() {
        Iterator var1 = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack)var1.next();
        } while(itemStack.isEmpty());

        return false;
    }

    public void clear() {
        this.inventory.clear();
    }
}
