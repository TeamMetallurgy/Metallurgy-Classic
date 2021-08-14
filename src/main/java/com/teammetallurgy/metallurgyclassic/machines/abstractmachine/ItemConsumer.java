package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemConsumer<T> extends AbstractMachineComponent implements Consumer<ItemStack, T> {

    protected List<AbstractRecipe<ItemStack, T>> recipes = new ArrayList<>();
    public int initialProcessingTime;
    public int currentProcessingTime;

    public ItemConsumer() {
        super(1);
    }

    public void addRecipe(AbstractRecipe<ItemStack, T> recipe) {
        this.recipes.add(recipe);
    }

    @Override
    public @Nullable T consume(boolean canContinue) {
        if (canContinue) {
            if (currentProcessingTime > 0) {
                currentProcessingTime--;
            } else if(initialProcessingTime == 0) {
                var recipe = getMatchingRecipe(input());
                initialProcessingTime = recipe.processingTime;
                currentProcessingTime = recipe.processingTime;
            } else {
                var recipe = getMatchingRecipe(input());
                input().decrement(recipe.input.getCount());
                var next_recipe = getMatchingRecipe(input());
                if (next_recipe != null) {
                    initialProcessingTime = recipe.processingTime;
                    currentProcessingTime = recipe.processingTime;
                } else {
                    initialProcessingTime = 0;
                    currentProcessingTime = 0;
                }
                return recipe.output;
            }
        } else {
            this.currentProcessingTime = 0;
            this.initialProcessingTime = 0;
        }
        return null;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.currentProcessingTime = nbt.getInt("current_progress");
        this.initialProcessingTime = nbt.getInt("initial_progress");
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt = super.writeNbt(nbt);
        nbt.putInt("current_progress", this.currentProcessingTime);
        nbt.putInt("initial_progress", this.initialProcessingTime);
        return nbt;
    }

    @Override
    public boolean canConsume() {
        return getMatchingRecipe(input()) != null;
    }

    public AbstractRecipe<ItemStack, T> getMatchingRecipe(ItemStack stack) {
        for(var recipe : recipes) {
            if (recipe.input.isOf(stack.getItem()) && stack.getCount() >= recipe.input.getCount()) {
                return recipe;
            }
        }
        return null;
    }

    public T getResult() {
        var t = getMatchingRecipe(input());
        if (t != null) {
            return t.output;
        }
        return null;
    }

    public ItemStack input() {
        if (inventory.size() > 0) {
            return inventory.get(0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if(getMatchingRecipe(stack) != null) {
            return true;
        }
        return false;
    }

    public float getProgress() {
        if (this.initialProcessingTime == 0) {
            return 0;
        }
        return this.currentProcessingTime / (float) this.initialProcessingTime;
    }
}
