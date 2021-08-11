package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface AbstractComponentInterface {
    int size();
    ItemStack getStack(int slot);
    ItemStack removeStack(int slot, int amount);
    ItemStack removeStack(int slot);
    void setStack(int slot, ItemStack stack);
    boolean isEmpty();
    void clear();
    void readNbt(NbtCompound nbt);
    NbtCompound writeNbt(NbtCompound nbt);
}
