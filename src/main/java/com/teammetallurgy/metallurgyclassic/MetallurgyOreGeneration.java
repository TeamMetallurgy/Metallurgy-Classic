package com.teammetallurgy.metallurgyclassic;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.impl.biome.modification.BiomeSelectionContextImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class MetallurgyOreGeneration {
    public static Map<BlockState, Float> densityMap = new HashMap<>();

    public static void register(MetalConfig config, Map<MetalRegistry.StoneTypes, Block> ores) {

        var ore_targets = new ArrayList<OreFeatureConfig.Target>();
        ores.forEach((type, block) -> {
            if (type == MetalRegistry.StoneTypes.STONE) {
                ore_targets.add(OreFeatureConfig.createTarget(Rules.STONE_REPLACEABLES, block.getDefaultState()));
            }
            if (type == MetalRegistry.StoneTypes.ANDESITE) {
                ore_targets.add(OreFeatureConfig.createTarget(Rules.ANDESITE_REPLACEABLES, block.getDefaultState()));
            }
            if (type == MetalRegistry.StoneTypes.DIORITE) {
                ore_targets.add(OreFeatureConfig.createTarget(Rules.DIORITE_REPLACEABLES, block.getDefaultState()));
            }
            if (type == MetalRegistry.StoneTypes.GRANITE) {
                ore_targets.add(OreFeatureConfig.createTarget(Rules.GRANITE_REPLACEABLES, block.getDefaultState()));
            }
            if (type == MetalRegistry.StoneTypes.DEEPSLATE) {
                ore_targets.add(OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, block.getDefaultState()));
            }
            if (type == MetalRegistry.StoneTypes.NETHERRACK) {
                ore_targets.add(OreFeatureConfig.createTarget(OreFeatureConfig.Rules.NETHERRACK, block.getDefaultState()));
            }
            if (type == MetalRegistry.StoneTypes.BASALT) {
                ore_targets.add(OreFeatureConfig.createTarget(Rules.BASALT_REPLACEABLES, block.getDefaultState()));
            }
            if (type == MetalRegistry.StoneTypes.END_STONE) {
                ore_targets.add(OreFeatureConfig.createTarget(Rules.END_STONE_REPLACEABLES, block.getDefaultState()));
            }
        });

        Predicate<BiomeSelectionContext> biomeSeelctors = null;
        if(config.dimensions.stream().anyMatch(integerRange -> integerRange.contains(0))) {
            biomeSeelctors = BiomeSelectors.foundInOverworld();
        }
        if(config.dimensions.stream().anyMatch(integerRange -> integerRange.contains(-1))) {
            biomeSeelctors = BiomeSelectors.foundInTheNether();
        }
        if(config.dimensions.stream().anyMatch(integerRange -> integerRange.contains(1))) {
            biomeSeelctors = BiomeSelectors.foundInTheEnd();
        }

        var feature = Feature.ORE
                .configure(new OreFeatureConfig(ore_targets, config.oresPerVein))
                .range(new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.aboveBottom(config.minLevel), YOffset.fixed(config.maxLevel))))
                .spreadHorizontally()
                .repeat(config.veinsPerChunk);

        String path = "ore_overworld_" + MetallurgyClassic.MOD_ID + config.name;
        var featureKey = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(MetallurgyClassic.MOD_ID, path));

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, featureKey.getValue(), feature);
        BiomeModifications.addFeature(biomeSeelctors, GenerationStep.Feature.UNDERGROUND_DECORATION, featureKey);
    }

    public static void register(MetalConfig config, BlockState ore) {
        if(config.dimensions.stream().anyMatch(integerRange -> integerRange.contains(0))) {
            register(config, ore, OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, BiomeSelectors.foundInOverworld());
        }
        if(config.dimensions.stream().anyMatch(integerRange -> integerRange.contains(-1))) {
            register(config, ore, OreFeatureConfig.Rules.NETHERRACK, BiomeSelectors.foundInTheNether());
        }
        if(config.dimensions.stream().anyMatch(integerRange -> integerRange.contains(1))) {
            register(config, ore, new BlockMatchRuleTest(Blocks.END_STONE), BiomeSelectors.foundInTheEnd());
        }
    }

    private static void register(MetalConfig config, BlockState ore, RuleTest rule, Predicate<BiomeSelectionContext> biomeSelectors) {

        var feature = Feature.ORE
                .configure(new OreFeatureConfig(rule, ore, config.oresPerVein))
                .range(new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.aboveBottom(config.minLevel), YOffset.fixed(config.maxLevel))))
                .spreadHorizontally()
                .repeat(config.veinsPerChunk);

        String path = "ore_overworld_" + MetallurgyClassic.MOD_ID + config.name;
        var featureKey = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(MetallurgyClassic.MOD_ID, path));

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, featureKey.getValue(), feature);
        BiomeModifications.addFeature(biomeSelectors, GenerationStep.Feature.UNDERGROUND_ORES, featureKey);
        //densityMap.put(ore, config.density); // Metallurgy 3 only had 100% dense ores by default
    }

    public static float getDensity(BlockState ore) {
        if(densityMap.containsKey(ore)) {
            return densityMap.get(ore);
        }
        return 1;
    }

    public static final class Rules {
        public static final RuleTest STONE_REPLACEABLES;
        public static final RuleTest GRANITE_REPLACEABLES;
        public static final RuleTest ANDESITE_REPLACEABLES;
        public static final RuleTest DIORITE_REPLACEABLES;
        public static final RuleTest END_STONE_REPLACEABLES;
        public static final RuleTest BASALT_REPLACEABLES;

        static {
            STONE_REPLACEABLES = new BlockMatchRuleTest(Blocks.STONE);
            GRANITE_REPLACEABLES = new BlockMatchRuleTest(Blocks.GRANITE);
            ANDESITE_REPLACEABLES = new BlockMatchRuleTest(Blocks.ANDESITE);
            DIORITE_REPLACEABLES = new BlockMatchRuleTest(Blocks.DIORITE);
            END_STONE_REPLACEABLES = new BlockMatchRuleTest(Blocks.END_STONE);
            BASALT_REPLACEABLES = new BlockMatchRuleTest(Blocks.SMOOTH_BASALT);
        }
    }
}
