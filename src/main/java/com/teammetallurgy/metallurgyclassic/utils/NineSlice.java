package com.teammetallurgy.metallurgyclassic.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class NineSlice extends DrawableHelper {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int leftWidth;
    private final int rightWidth;
    private final int topHeight;
    private final int bottomHeight;
    private final int centerWidth;
    private final int centerHeight;
    private final int textureWidth;
    private final int textureHeight;

    // TODO: Modes scaling/tiling
    // TODO: Add ability to scale

    public NineSlice(Identifier texture, int width, int textureWidth, int textureHeight) {
        this.texture = texture;
        this.u = 0;
        this.v = 0;
        this.leftWidth = width;
        this.rightWidth = width;
        this.bottomHeight = width;
        this.topHeight = width;
        this.centerHeight = width;
        this.centerWidth = width;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public void renderCentered(MatrixStack matrices, int width, int height) {
        if(MinecraftClient.getInstance().currentScreen != null) {
            int x = (MinecraftClient.getInstance().currentScreen.width - width) / 2;
            int y = (MinecraftClient.getInstance().currentScreen.height - height) / 2;
            render(matrices, x, y, width, height);
        }
    }

    public void render(MatrixStack matrices, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, texture);
        // Top Left
        drawTexture(matrices, x, y, u, v, leftWidth, topHeight, textureWidth, textureHeight);
        // Top Center
        drawTexture(matrices, x + leftWidth, y, width - leftWidth - rightWidth, topHeight, u + leftWidth, v, centerWidth, centerHeight, textureWidth, textureHeight);
        // Top Right
        drawTexture(matrices, x + width - rightWidth, y, u + leftWidth + centerWidth, v, rightWidth, topHeight, textureWidth, textureHeight);
        // Center Left
        drawTexture(matrices, x, y + topHeight, leftWidth, height - bottomHeight - topHeight, u, v + topHeight, leftWidth, centerHeight, textureWidth, textureHeight);
        // Center Center
        drawTexture(matrices, x + leftWidth, y + topHeight, width - leftWidth - rightWidth, height - bottomHeight - topHeight, u + leftWidth, v + topHeight, centerWidth, centerHeight, textureWidth, textureHeight);
        // Center Right
        drawTexture(matrices, x + width - rightWidth, y + topHeight, rightWidth, height - bottomHeight - topHeight, u + leftWidth + centerWidth, v + topHeight, leftWidth, centerHeight, textureWidth, textureHeight);
        // Bottom Left
        drawTexture(matrices, x, y + height - bottomHeight, u, v + topHeight + centerHeight, leftWidth, bottomHeight, textureWidth, textureHeight);
        // Bottom Center
        drawTexture(matrices, x + 4, y + height - bottomHeight, width - leftWidth - rightWidth, bottomHeight, u + leftWidth, v + topHeight + centerHeight, centerWidth, bottomHeight, textureWidth, textureHeight);
        // Bottom Right
        drawTexture(matrices, x + width - rightWidth, y + height - bottomHeight, u + leftWidth + centerWidth, v + topHeight + bottomHeight, rightWidth, bottomHeight, textureWidth, textureHeight);
    }
}
