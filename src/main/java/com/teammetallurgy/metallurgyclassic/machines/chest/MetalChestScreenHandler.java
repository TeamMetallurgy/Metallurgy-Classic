package com.teammetallurgy.metallurgyclassic.machines.chest;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class MetalChestScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final MetalChestComponent.Type metalType;
    private final int rows;
    private final int cols;

    public static ScreenHandlerRegistry.SimpleClientHandlerFactory<MetalChestScreenHandler> getScreenHandlerFactory(MetalChestComponent.Type metalType) {
        return (syncId, inventory) -> new MetalChestScreenHandler(MetalChestComponent.getScreenHandler(metalType), syncId, inventory, metalType);
    }

    private MetalChestScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, MetalChestComponent.Type metalType) {
        this(type, syncId, playerInventory, new SimpleInventory(metalType.cols * metalType.rows), metalType);
    }

    public MetalChestScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, MetalChestComponent.Type metalType) {
        super(type, syncId);
        checkSize(inventory, metalType.cols * metalType.rows);
        this.inventory = inventory;
        this.metalType = metalType;
        this.rows = metalType.rows;
        this.cols = metalType.cols;
        inventory.onOpen(playerInventory.player);
        int i = (this.rows - 4) * 18;
        int j = (this.cols - 9) * 18 / 2;

        for(int y = 0; y < this.rows; ++y) {
            for(int x = 0; x < this.cols; ++x) {
                this.addSlot(new Slot(inventory, x + y * cols, 8 + x * 18, 18 + y * 18));
            }
        }

        int playerOffset = 103;
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18 + j, playerOffset + y * 18 + i));
            }
        }

        for(int y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, 8 + y * 18 + j, playerOffset + 58 + i));
        }

    }

    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < this.rows * 9) {
                if (!this.insertItem(itemStack2, this.rows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, this.rows * 9, false)) {
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

    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getRows() {
        return this.rows;
    }

    public int getCols() {
        return this.cols;
    }

    public MetalChestComponent.Type getMetalType() { return this.metalType; }
}
