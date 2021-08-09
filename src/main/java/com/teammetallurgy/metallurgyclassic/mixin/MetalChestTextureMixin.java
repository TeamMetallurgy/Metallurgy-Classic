package com.teammetallurgy.metallurgyclassic.mixin;

import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestComponent;
import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TexturedRenderLayers.class)
public class MetalChestTextureMixin {
//    Lnet/minecraft/client/render/TexturedRenderLayers;getChestTexture(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(at = @At("RETURN"), method = "getChestTexture(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier", cancellable = true)
    private static void getChestTexture(BlockEntity blockEntity, ChestType type, boolean christmas, CallbackInfoReturnable<SpriteIdentifier> cir) {
        if(blockEntity instanceof MetalChestEntity) {
            cir.setReturnValue(MetalChestComponent.getTexture(MetalChestComponent.getType(blockEntity.getType())));
        }
    }
}
