package com.teammetallurgy.metallurgyclassic.tools;

import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class MetallurgyPickaxeItem extends PickaxeItem {
    public MetallurgyPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }
}
