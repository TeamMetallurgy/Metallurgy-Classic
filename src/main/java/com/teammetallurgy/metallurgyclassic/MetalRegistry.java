package com.teammetallurgy.metallurgyclassic;

import com.teammetallurgy.metallurgyclassic.materials.MetallurgyArmorMaterial;
import com.teammetallurgy.metallurgyclassic.materials.MetallurgyToolMaterial;
import com.teammetallurgy.metallurgyclassic.tools.MetallurgyAxeItem;
import com.teammetallurgy.metallurgyclassic.tools.MetallurgyHoeItem;
import com.teammetallurgy.metallurgyclassic.tools.MetallurgyPickaxeItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.teammetallurgy.metallurgyclassic.utils.RangeUtils.rangeFromString;

public class MetalRegistry {
    public enum StoneTypes {
        STONE,
        GRANITE,
        ANDESITE,
        DIORITE,
        DEEPSLATE,
        NETHERRACK,
        BASALT,
        END_STONE
    }

    private class MetalEntry {
        public MetalConfig config;
        public Map<String, Block> ores = new HashMap<>();
        public Map<String, Item> items = new HashMap<>();
        public Map<String, Block> blocks = new HashMap<>();
    }

    private static final MetalRegistry instance = new MetalRegistry();

    private final Map<String, MetalEntry> registry = new HashMap<>();

    public static MetalRegistry instance() {
        return instance;
    }

    private MetalRegistry() {

    }

    public MetalConfig getConfig(String name) {
        if (registry.containsKey(name)) {
            return registry.get(name).config;
        }
        return null;
    }

    public Set<String> metals() {
        return registry.keySet();
    }

    public @Nullable Item getItem(String metal, String item) {
        if(registry.containsKey(metal)) {
            if(registry.get(metal).items.containsKey(item)) {
                return registry.get(metal).items.get(item);
            }
        }
        return null;
    }

    public Collection<Block> getOres(String metal) {
        return registry.get(metal).ores.values();
    }

    public Block getBlock(String metal, String block) {
        return registry.get(metal).blocks.get(block);
    }

    public void parseCSV(String filename) {
        InputStream is = getClass().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            var headersString = reader.readLine().split(",");
            System.out.println(headersString[0]);
            var headers = IntStream.range(0, headersString.length).boxed().collect(Collectors.toMap(i -> headersString[i].toLowerCase(), i -> i));
            reader.lines().forEach(str -> {
                System.out.println(str);
                var row = str.split(",");
                var config = new MetalConfig();
                var set = MetalConfig.MetalSet.get(row[headers.get("metal set")]);
                if (set != null) {
                    config.set = set;
                    config.name = row[headers.get("name")].toLowerCase().replace(" ", "_");
                    config.type = MetalConfig.MetalType.get(row[headers.get("type")]);
                    if(config.type == MetalConfig.MetalType.ALLOY) {
                        var alloyRecipeString = row[headers.get("alloy recipe")];
                        var alloyRecipeArray = alloyRecipeString.split("\" \"");
                        alloyRecipeArray[0] = alloyRecipeArray[0].replace("\"", "").toLowerCase();
                        alloyRecipeArray[1] = alloyRecipeArray[1].replace("\"", "").toLowerCase();
                        config.alloyRecipe = new Pair<>(alloyRecipeArray[0], alloyRecipeArray[1]);
                    }
                    config.blockLevel = Integer.parseInt(row[headers.get("block lvl")]);
                    if(config.type.hasTools()) {
                        config.pickLevel = Integer.parseInt(row[headers.get("pick lvl")]);
                        config.toolDurability = Integer.parseInt(row[headers.get("durability")]);
                        config.toolDamage = Integer.parseInt(row[headers.get("damage")]);
                        config.toolSpeed = Integer.parseInt(row[headers.get("speed")]);
                        config.enchantability = Integer.parseInt(row[headers.get("enchantability")]);
                        config.helmetArmor = Integer.parseInt(row[headers.get("helmet armor")]);
                        config.chestplateArmor = Integer.parseInt(row[headers.get("chestplate armor")]);
                        config.leggingsArmor = Integer.parseInt(row[headers.get("leggings armor")]);
                        config.bootsArmor = Integer.parseInt(row[headers.get("boots armor")]);
                        config.armorDurability = Integer.parseInt(row[headers.get("durability multiplier")]);
                    }
                    if(config.type.hasOre()) {
                        config.veinsPerChunk = Integer.parseInt(row[headers.get("veins per chunk")]);
                        config.oresPerVein = Integer.parseInt(row[headers.get("ores per vein")]);
                        config.minLevel = Integer.parseInt(row[headers.get("min level")]);
                        config.maxLevel = Integer.parseInt(row[headers.get("max level")]);
                        var dimensionsString = row[headers.get("dimensions")];
                        var dimensionsRangeArray = dimensionsString.split(" ");
                        config.dimensions = Arrays.stream(dimensionsRangeArray).map(dim -> rangeFromString(dim, "-")).collect(Collectors.toList());
                    }
                    // Copper has become a vanilla metal, but the sheet still lists it as a base metal.
                    if(config.name.equalsIgnoreCase("copper")) {
                        createCopper(config);
                    } else {
                        createMetal(config);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createGold() {
        var entry = new MetalEntry();
        Item dust = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
        Item tinyDust = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
        Block bricks = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));

        entry.blocks.put(Constants.BlockBlock, Blocks.GOLD_BLOCK);
        entry.blocks.put(Constants.BlockBricks, bricks);
        entry.items.put(Constants.ItemDust, dust);
        entry.items.put(Constants.ItemIngot, Items.GOLD_INGOT);
        entry.items.put(Constants.ItemTinyDust, tinyDust);
        entry.items.put(Constants.ItemNugget, Items.GOLD_NUGGET);

        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "gold_dust"), dust);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "gold_tiny_dust"), tinyDust);
        Registry.register(Registry.BLOCK, new Identifier(MetallurgyClassic.MOD_ID, "gold_bricks"), bricks);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "gold_bricks"), new BlockItem(bricks, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        entry.blocks.put(Constants.BlockOre, Blocks.GOLD_ORE);
        entry.items.put(Constants.ItemRawOre, Items.RAW_GOLD);
        entry.items.put(Constants.ItemShovel, Items.GOLDEN_SHOVEL);
        entry.items.put(Constants.ItemSword, Items.GOLDEN_SWORD);
        entry.items.put(Constants.ItemHoe, Items.GOLDEN_HOE);
        entry.items.put(Constants.ItemAxe, Items.GOLDEN_AXE);
        entry.items.put(Constants.ItemPickaxe, Items.GOLDEN_PICKAXE);
        entry.items.put(Constants.ItemHelmet, Items.GOLDEN_HELMET);
        entry.items.put(Constants.ItemChestplate, Items.GOLDEN_CHESTPLATE);
        entry.items.put(Constants.ItemLeggings, Items.GOLDEN_LEGGINGS);
        entry.items.put(Constants.ItemBoots, Items.GOLDEN_BOOTS);

        registry.put("gold", entry);
    }

    public void createIron() {
        var entry = new MetalEntry();
        Item dust = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
        Item tinyDust = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
        Block bricks = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));

        entry.blocks.put(Constants.BlockBlock, Blocks.IRON_BLOCK);
        entry.blocks.put(Constants.BlockBricks, bricks);
        entry.items.put(Constants.ItemDust, dust);
        entry.items.put(Constants.ItemIngot, Items.IRON_INGOT);
        entry.items.put(Constants.ItemTinyDust, tinyDust);
        entry.items.put(Constants.ItemNugget, Items.IRON_NUGGET);

        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "iron_dust"), dust);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "iron_tiny_dust"), tinyDust);
        Registry.register(Registry.BLOCK, new Identifier(MetallurgyClassic.MOD_ID, "iron_bricks"), bricks);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "iron_bricks"), new BlockItem(bricks, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        entry.blocks.put(Constants.BlockOre, Blocks.IRON_ORE);
        entry.items.put(Constants.ItemRawOre, Items.RAW_IRON);
        entry.items.put(Constants.ItemShovel, Items.IRON_SHOVEL);
        entry.items.put(Constants.ItemSword, Items.IRON_SWORD);
        entry.items.put(Constants.ItemHoe, Items.IRON_HOE);
        entry.items.put(Constants.ItemAxe, Items.IRON_AXE);
        entry.items.put(Constants.ItemPickaxe, Items.IRON_PICKAXE);
        entry.items.put(Constants.ItemHelmet, Items.IRON_HELMET);
        entry.items.put(Constants.ItemChestplate, Items.IRON_CHESTPLATE);
        entry.items.put(Constants.ItemLeggings, Items.IRON_LEGGINGS);
        entry.items.put(Constants.ItemBoots, Items.IRON_BOOTS);

        registry.put("iron", entry);
    }

    private void createCopper(MetalConfig config) {
        var entry = new MetalEntry();
        Item ingot = Items.COPPER_INGOT;
        Item dust = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
        Item tinyDust = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
        Item nugget = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
        Block bricks = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, config.blockLevel).strength(3.0f, 3.0f));

        entry.blocks.put(Constants.BlockBlock, Blocks.COPPER_BLOCK);
        entry.blocks.put(Constants.BlockBricks, bricks);
        entry.items.put(Constants.ItemDust, dust);
        entry.items.put(Constants.ItemIngot, ingot);
        entry.items.put(Constants.ItemTinyDust, tinyDust);
        entry.items.put(Constants.ItemNugget, nugget);

        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "copper_dust"), dust);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "copper_tiny_dust"), tinyDust);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "copper_nugget"), nugget);
        Registry.register(Registry.BLOCK, new Identifier(MetallurgyClassic.MOD_ID, "copper_bricks"), bricks);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_bricks"), new BlockItem(bricks, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        Item shovel = new ShovelItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
        Item hoe = new MetallurgyHoeItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
        Item axe = new MetallurgyAxeItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
        Item pickaxe = new MetallurgyPickaxeItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
        Item sword = new SwordItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.COMBAT));
        var armorMaterial = new MetallurgyArmorMaterial(config.name, config.armorDurability, new int[]{config.helmetArmor, config.chestplateArmor, config.leggingsArmor, config.bootsArmor}, config.enchantability, 0f, ingot);
        Item helmet = new ArmorItem(armorMaterial, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT));
        Item chestplate = new ArmorItem(armorMaterial, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT));
        Item leggings = new ArmorItem(armorMaterial, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT));
        Item boots = new ArmorItem(armorMaterial, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT));
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_shovel"), shovel);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_sword"), sword);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_hoe"), hoe);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_axe"), axe);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_pickaxe"), pickaxe);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_helmet"), helmet);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_chestplate"), chestplate);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_leggings"), leggings);
        Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_boots"), boots);

        entry.blocks.put(Constants.BlockOre, Blocks.COPPER_ORE);
        entry.items.put(Constants.ItemRawOre, Items.RAW_COPPER);
        entry.items.put(Constants.ItemShovel, shovel);
        entry.items.put(Constants.ItemSword, sword);
        entry.items.put(Constants.ItemHoe, hoe);
        entry.items.put(Constants.ItemAxe, axe);
        entry.items.put(Constants.ItemPickaxe, pickaxe);
        entry.items.put(Constants.ItemHelmet, helmet);
        entry.items.put(Constants.ItemChestplate, chestplate);
        entry.items.put(Constants.ItemLeggings, leggings);
        entry.items.put(Constants.ItemBoots, boots);

        registry.put("copper", entry);
    }

    public void createMetal(MetalConfig config) {
        var entry = new MetalEntry();
        entry.config = config;
        if(config.type.hasOre()) {
            var ores = createOres(config);
            ores.forEach((name, ore) -> {
                entry.ores.put(name.name().toLowerCase(), ore);
            });
            Item rawOre = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
            entry.items.put(Constants.ItemRawOre, rawOre);

            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, "raw_" + config.name), rawOre);
        }

        if(config.type.hasIngot()) {
            Block block = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, config.blockLevel).strength(3.0f, 3.0f));
            Block bricks = new Block(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES, config.blockLevel).strength(3.0f, 3.0f));
            Item dust = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
            Item ingot = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
            Item tinyDust = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
            Item tinyNugget = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));
            entry.blocks.put(Constants.BlockBlock, block);
            entry.blocks.put(Constants.BlockBricks, bricks);
            entry.items.put(Constants.ItemDust, dust);
            entry.items.put(Constants.ItemIngot, ingot);
            entry.items.put(Constants.ItemTinyDust, tinyDust);
            entry.items.put(Constants.ItemNugget, tinyNugget);

            Registry.register(Registry.BLOCK, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_block"), block);
            Registry.register(Registry.BLOCK, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_bricks"), bricks);
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_block"), new BlockItem(block, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_bricks"), new BlockItem(bricks, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_dust"), dust);
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_ingot"), ingot);
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_tiny_dust"), tinyDust);
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_nugget"), tinyNugget);

            if(config.type.hasTools()) {
                Item shovel = new ShovelItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
                Item hoe = new MetallurgyHoeItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
                Item axe = new MetallurgyAxeItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
                Item pickaxe = new MetallurgyPickaxeItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.TOOLS));
                Item sword = new SwordItem(new MetallurgyToolMaterial(config.toolDurability, config.toolSpeed, config.toolDamage, config.pickLevel, config.enchantability, ingot), config.toolDamage, config.toolSpeed, new Item.Settings().group(ItemGroup.COMBAT));
                var armorMaterial = new MetallurgyArmorMaterial(config.name.replace("_", ""), config.armorDurability, new int[]{config.helmetArmor, config.chestplateArmor, config.leggingsArmor, config.bootsArmor}, config.enchantability, 0f, ingot);
                Item helmet = new ArmorItem(armorMaterial, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT));
                Item chestplate = new ArmorItem(armorMaterial, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT));
                Item leggings = new ArmorItem(armorMaterial, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT));
                Item boots = new ArmorItem(armorMaterial, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT));
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_shovel"), shovel);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_sword"), sword);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_hoe"), hoe);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_axe"), axe);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_pickaxe"), pickaxe);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_helmet"), helmet);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_chestplate"), chestplate);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_leggings"), leggings);
                Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_boots"), boots);
                entry.items.put(Constants.ItemShovel, shovel);
                entry.items.put(Constants.ItemSword, sword);
                entry.items.put(Constants.ItemHoe, hoe);
                entry.items.put(Constants.ItemAxe, axe);
                entry.items.put(Constants.ItemPickaxe, pickaxe);
                entry.items.put(Constants.ItemHelmet, helmet);
                entry.items.put(Constants.ItemChestplate, chestplate);
                entry.items.put(Constants.ItemLeggings, leggings);
                entry.items.put(Constants.ItemBoots, boots);
            }
        }
        registry.put(config.name, entry);
    }

    private Map<StoneTypes, Block> createOres(MetalConfig config) {
        var ores = new HashMap<StoneTypes, Block>();
        for (StoneTypes type : StoneTypes.values()) {
            Block ore = new OreBlock(FabricBlockSettings.of(Material.STONE).nonOpaque().breakByTool(FabricToolTags.PICKAXES, 1).strength(3.0f, 3.0f));

            Registry.register(Registry.BLOCK, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_ore_" + type.name().toLowerCase()), ore);
            Registry.register(Registry.ITEM, new Identifier(MetallurgyClassic.MOD_ID, config.name + "_ore_" + type.name().toLowerCase()), new BlockItem(ore, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
            ores.put(type, ore);
        }
        MetallurgyOreGeneration.register(config, ores);
        return ores;
    }


    @Environment(EnvType.CLIENT)
    public void setOreRenderLayers() {
        registry.forEach((name, entry) -> {
            entry.ores.forEach((stone, ore) -> {
                BlockRenderLayerMap.INSTANCE.putBlock(ore, RenderLayer.getCutoutMipped());
            });
        });
    }

    private BipedEntityModel<LivingEntity> armorModel;

    public void fixArmorTextures() {
        List<ArmorItem> items = new ArrayList<>();
        registry.forEach((name, metal) -> {
            if(!(name.equals("iron") || name.equals("gold"))) {
                metal.items.values().forEach(item -> {
                    if(item instanceof ArmorItem armor) {
                        items.add(armor);
                    }
                });
            }
        });
        ArmorItem[] array = new ArmorItem[items.size()];
        ArmorRenderer.register(((matrices, vertexConsumers, stack, entity, slot, light, model) -> {
            if (armorModel == null) {
                armorModel = new BipedEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_OUTER_ARMOR));
            }
            if(stack.getItem() instanceof ArmorItem item) {
                model.setAttributes(armorModel);
                armorModel.setVisible(false);
                armorModel.body.visible = slot == EquipmentSlot.CHEST;
                armorModel.leftArm.visible = slot == EquipmentSlot.CHEST;
                armorModel.rightArm.visible = slot == EquipmentSlot.CHEST;
                armorModel.head.visible = slot == EquipmentSlot.HEAD;
                armorModel.leftLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
                armorModel.rightLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
                if(slot == EquipmentSlot.LEGS) {
                    ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorModel, MetallurgyClassic.id("textures/models/armor/" + item.getMaterial().getName() + "_layer_2.png"));
                }
                else {
                    ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorModel, MetallurgyClassic.id("textures/models/armor/" + item.getMaterial().getName() + "_layer_1.png"));
                }
            }
        }), items.toArray(array));
    }
}
