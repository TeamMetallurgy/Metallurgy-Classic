package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

import net.minecraft.item.ItemStack;

public class ItemProducer extends AbstractMachineComponent implements Producer<ItemStack> {

    public ItemProducer() {
        super(1);
    }

    @Override
    public void produce(boolean canContinue, ItemStack recipeOutput) {
        if (canContinue) {
            if (recipeOutput != null && !recipeOutput.isEmpty()) {
                if (output().isEmpty()) {
                    this.setStack(0, recipeOutput.copy());
                } else {
                    output().increment(recipeOutput.getCount());
                }
            }
        }
    }

    @Override
    public boolean canProduce(ItemStack recipeOutput) {
        if (recipeOutput == null || recipeOutput.isEmpty())
        {
            return false;
        }
        if(output().isEmpty()) {
            return true;
        }
        if(output().isOf(recipeOutput.getItem()) && output().getMaxCount() >= output().getCount() + recipeOutput.getCount()) {
            return true;
        }
        return false;
    }

    public ItemStack output() {
        if (inventory.size() > 0) {
            return inventory.get(0);
        }
        return ItemStack.EMPTY;
    }
}
