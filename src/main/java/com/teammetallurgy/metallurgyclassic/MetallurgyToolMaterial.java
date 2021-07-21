package com.teammetallurgy.metallurgyclassic;

import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class MetallurgyToolMaterial implements ToolMaterial {

    private final int durability;
    private final float speed;
    private final float damage;
    private final int miningLevel;
    private final int enchantability;
    private final Ingredient repairIngredient;

    public MetallurgyToolMaterial(int durability, float speed, float damage, int miningLevel, int enchantability, Item ingot) {
        this.durability = durability;
        this.speed = speed;
        this.damage = damage;
        this.miningLevel = miningLevel;
        this.enchantability = enchantability;
        this.repairIngredient = Ingredient.ofItems(ingot);
    }


    @Override
    public int getDurability() {
        return this.durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.speed;
    }

    @Override
    public float getAttackDamage() {
        return this.damage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }
}
