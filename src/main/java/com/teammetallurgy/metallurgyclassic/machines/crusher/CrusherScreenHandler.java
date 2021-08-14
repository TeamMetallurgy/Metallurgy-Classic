package com.teammetallurgy.metallurgyclassic.machines.crusher;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class CrusherScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public CrusherScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(CrusherComponent.CRUSHER_SCREEN_HANDLER, syncId, playerInventory, new ArrayPropertyDelegate(4));
    }

    private CrusherScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, PropertyDelegate propertyDelegate) {
        this(type, syncId, playerInventory, new SimpleInventory(3), propertyDelegate);
    }

    public CrusherScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(type, syncId);
        checkSize(inventory, 3);
        checkDataCount(propertyDelegate, 4);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        this.addSlot(new Slot(inventory, 0, 56, 17) {
            @Override
            public boolean canInsert(ItemStack itemStack) {
                return inventory.isValid(0, itemStack);
            }
        });
        this.addSlot(new Slot(inventory, 1, 56, 53) {
            @Override
            public boolean canInsert(ItemStack itemStack) {
                return inventory.isValid(1, itemStack);
            }
        });
        this.addSlot(new Slot(inventory, 2, 116, 35) {
            @Override
            public boolean canInsert(ItemStack itemStack) {
                return inventory.isValid(2, itemStack);
            }
        });

        int playerOffset = 84;
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, playerOffset + y * 18));
            }
        }

        for(int y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, 8 + y * 18, playerOffset + 58));
        }

        this.addProperties(propertyDelegate);
    }

    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 3) {
                if (!this.insertItem(itemStack2, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 3, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemStack;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public float getFuelLevel() {
        if(propertyDelegate.get(2) == 0) {
            return 0;
        }
        return propertyDelegate.get(3) / (float) propertyDelegate.get(2);
    }

    public float getProgress() {
        if(propertyDelegate.get(1) == 0) {
            return 0;
        }
        return (propertyDelegate.get(0) - propertyDelegate.get(1)) / (float) propertyDelegate.get(0);
    }
}
