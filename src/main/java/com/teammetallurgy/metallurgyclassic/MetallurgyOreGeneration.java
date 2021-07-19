package com.teammetallurgy.metallurgyclassic;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

import java.util.HashMap;
import java.util.Map;

public class MetallurgyOreGeneration {
    public static Map<BlockState, Float> densityMap = new HashMap<>();

    public static void register(MetallurgyOreConfig config) {
        var feature = Feature.ORE
                .configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, config.ore, config.size))
                .range(new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.aboveBottom(config.minHeight), YOffset.fixed(config.maxHeight))))
                .spreadHorizontally()
                .repeat(config.size);

        String path = "ore_overworld_" + MetallurgyClassic.MOD_ID + config.name;
        var featureKey = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(MetallurgyClassic.MOD_ID, path));

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, featureKey.getValue(), feature);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, featureKey);
        densityMap.put(config.ore, config.density);
    }

    public static float getDensity(BlockState ore) {
        if(densityMap.containsKey(ore)) {
            return densityMap.get(ore);
        }
        return 1;
    }
}
