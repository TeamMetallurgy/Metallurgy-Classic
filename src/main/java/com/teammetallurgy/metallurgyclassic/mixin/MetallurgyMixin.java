package com.teammetallurgy.metallurgyclassic.mixin;

import com.teammetallurgy.metallurgyclassic.MetalRegistry;
import com.teammetallurgy.metallurgyclassic.MetallurgyOreGeneration;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
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

//    private static String generating;
//    private static int count = 0;
//
//    private static int total = 0;
//    private static int veins = 0;
//
//    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"), method = "generateVeinPart(Lnet/minecraft/world/StructureWorldAccess;Ljava/util/Random;Lnet/minecraft/world/gen/feature/OreFeatureConfig;DDDDDDIIIII)Z")
//    private void count(StructureWorldAccess structureWorldAccess, Random random, OreFeatureConfig config, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int horizontalSize, int verticalSize, CallbackInfoReturnable<Boolean> cir) {
//        MetallurgyMixin.generating = config.targets.get(0).state.toString();
//        MetallurgyMixin.count++;
//        MetallurgyMixin.total++;
//    }
//
//    @Inject(at = @At("RETURN"), method = "generate(Lnet/minecraft/world/gen/feature/util/FeatureContext;)Z")
//    private void end(FeatureContext<OreFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {
//
//        MetallurgyMixin.veins++;
//        System.out.println("Generated " + MetallurgyMixin.generating + ": " + MetallurgyMixin.count);
//        System.out.println("Average " + MetallurgyMixin.generating + ": " + (MetallurgyMixin.total/(float)MetallurgyMixin.veins));
//        MetallurgyMixin.count = 0;
//    }
}
