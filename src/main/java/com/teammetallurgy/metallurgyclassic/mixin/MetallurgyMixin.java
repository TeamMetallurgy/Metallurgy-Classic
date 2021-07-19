package com.teammetallurgy.metallurgyclassic.mixin;

import com.teammetallurgy.metallurgyclassic.MetallurgyOreGeneration;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Function;

@Mixin(OreFeature.class)
public class MetallurgyMixin {
    @Inject(at = @At("RETURN"), method = "shouldPlace(Lnet/minecraft/block/BlockState;Ljava/util/function/Function;Ljava/util/Random;Lnet/minecraft/world/gen/feature/OreFeatureConfig;Lnet/minecraft/world/gen/feature/OreFeatureConfig$Target;Lnet/minecraft/util/math/BlockPos$Mutable;)Z", cancellable = true)
    private static void density(BlockState state, Function<BlockPos, BlockState> posToState, Random random, OreFeatureConfig config, OreFeatureConfig.Target target, BlockPos.Mutable pos, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            float density = MetallurgyOreGeneration.getDensity(target.state);
            if(density < 0.99) {
                cir.setReturnValue(random.nextFloat() < density);
            }
        }
    }
}
