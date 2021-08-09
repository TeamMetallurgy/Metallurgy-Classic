package com.teammetallurgy.metallurgyclassic.mixin;

import com.teammetallurgy.metallurgyclassic.items.MetallurgyItems;
import net.minecraft.block.TntBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z

@Mixin(TntBlock.class)
public class UtilityTNTMixin {
    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean orUtilityLighter(ItemStack itemStack, Item item) {
        return itemStack.isOf(item) || itemStack.isOf(MetallurgyItems.MATCH) || itemStack.isOf(MetallurgyItems.MAGNESIUM_IGNITER);
    }
}
