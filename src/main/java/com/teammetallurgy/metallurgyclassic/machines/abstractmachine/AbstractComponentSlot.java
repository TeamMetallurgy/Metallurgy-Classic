package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class AbstractComponentSlot extends Slot {

    public AbstractComponentSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }
}
