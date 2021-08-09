package com.teammetallurgy.metallurgyclassic.machines.furnace;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.MOD_ID;

public class MetalFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public MetalFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(MetalFurnaceComponent.getEntityType(state.getBlock()), pos, state, RecipeType.SMELTING);
    }

    protected Text getContainerName() {
        return new TranslatableText(MOD_ID + ".container.furnace." + MetalFurnaceComponent.getType(this).name);
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new MetalFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
