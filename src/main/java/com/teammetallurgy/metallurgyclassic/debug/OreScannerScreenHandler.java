package com.teammetallurgy.metallurgyclassic.debug;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;

public class OreScannerScreenHandler extends ScreenHandler {

    static List<DisplaySlot> scanSlots = new ArrayList<>();

    static {
        int slotY = 266 - 5 - 18;
        int scannerOffset = -22;
        scanSlots.add(new DisplaySlot(Items.IRON_ORE.getDefaultStack(), 8, slotY + scannerOffset));
        scanSlots.add(new DisplaySlot(Items.DIAMOND_ORE.getDefaultStack(), 8 + 18, slotY + scannerOffset));
        scanSlots.add(new DisplaySlot(Items.COAL_ORE.getDefaultStack(), 8 + 18 * 2, slotY + scannerOffset));
        scanSlots.add(new DisplaySlot(Items.DIRT.getDefaultStack(), 8 + 18 * 3, slotY + scannerOffset));
        scanSlots.add(new DisplaySlot(Items.GRAVEL.getDefaultStack(), 8 + 18 * 4, slotY + scannerOffset));
        scanSlots.add(new DisplaySlot(ItemStack.EMPTY, 8 + 18 * 5, slotY + scannerOffset));
        scanSlots.add(new DisplaySlot(ItemStack.EMPTY, 8 + 18 * 6, slotY + scannerOffset));
        scanSlots.add(new DisplaySlot(ItemStack.EMPTY, 8 + 18 * 7, slotY + scannerOffset));
        scanSlots.add(new DisplaySlot(ItemStack.EMPTY, 8 + 18 * 8, slotY + scannerOffset));
    }

    public static class DisplaySlot extends Slot {
        public DisplaySlot(ItemStack itemStack, int x, int y) {
            super(new SimpleInventory(itemStack), 0, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return false;
        }

//        @Override
//        public ItemStack insertStack(ItemStack stack, int count) {
//            return stack;
//        }
    }

    public OreScannerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(OreScannerKeybind.ORE_SCANNER_SCREEN_HANDLER, syncId);


        int slotY = 266 - 5 - 18;
        for(int y = 0; y < 9; ++y) {
            this.addSlot(new Slot(playerInventory, y, 8 + y * 18, slotY));
        }
        scanSlots.forEach(this::addSlot);
    }

    protected OreScannerScreenHandler(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        try {
            if(slots.get(slotIndex) instanceof DisplaySlot) {
                slots.get(slotIndex).setStack(this.getCursorStack().copy());
            } else {
            }
        } catch(Exception e) {
            System.out.println(e);
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }
}
