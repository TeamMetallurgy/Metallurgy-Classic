package com.teammetallurgy.metallurgyclassic.machines.furnace;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class MetalFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public MetalFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(MetalFurnaceComponent.getEntityType(state.getBlock()), pos, state, RecipeType.SMELTING);
    }

    protected Text getContainerName() {
        return new TranslatableText("container.furnace");
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new MetalFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
