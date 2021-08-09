package com.teammetallurgy.metallurgyclassic.mixin;

import com.teammetallurgy.metallurgyclassic.items.MetallurgyItems;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z

@Mixin(CreeperEntity.class)
public class UtilityCreeperMixin {
    @Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean orUtilityLighter(ItemStack itemStack, Item item) {
        return itemStack.isOf(item) || itemStack.isOf(MetallurgyItems.MATCH) || itemStack.isOf(MetallurgyItems.MAGNESIUM_IGNITER);
    }
}
