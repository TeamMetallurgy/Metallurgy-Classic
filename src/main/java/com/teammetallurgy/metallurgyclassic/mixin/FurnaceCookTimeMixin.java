package com.teammetallurgy.metallurgyclassic.mixin;

import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceBlockEntity;
import com.teammetallurgy.metallurgyclassic.machines.furnace.MetalFurnaceComponent;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public class FurnaceCookTimeMixin {

    @Inject(at = @At("RETURN"), method = "getCookTime(Lnet/minecraft/world/World;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;)I", cancellable = true)
    private static void cookTime(World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory, CallbackInfoReturnable<Integer> cir) {
        if(inventory instanceof MetalFurnaceBlockEntity) {
            cir.setReturnValue((int) (cir.getReturnValueI() * MetalFurnaceComponent.getCookTimeMultiplier((MetalFurnaceBlockEntity) inventory)));
        }
    }
}
