package com.teammetallurgy.metallurgyclassic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class MetallurgyOreConfig {
    public String name;
    public BlockState ore;
    public int size;
    public float density;
    public int amount;
    public int minHeight;
    public int maxHeight;

    public MetallurgyOreConfig(Block ore) {
        this.ore = ore.getDefaultState();
        this.name = ore.getName().asString();
    }

    public MetallurgyOreConfig size(int size) {
        this.size = size;
        return this;
    }

    public MetallurgyOreConfig amount(int amount) {
        this.amount = amount;
        return this;
    }

    public MetallurgyOreConfig density(float density) {
        this.density = density;
        return this;
    }

    public MetallurgyOreConfig minHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public MetallurgyOreConfig maxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }
}
