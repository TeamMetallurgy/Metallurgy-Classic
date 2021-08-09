package com.teammetallurgy.metallurgyclassic.machines.chest;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teammetallurgy.metallurgyclassic.utils.NineSlice;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;

public class MetalChestScreen extends HandledScreen<MetalChestScreenHandler> implements ScreenHandlerProvider<MetalChestScreenHandler> {
    private final NineSlice background = new NineSlice(id("textures/gui/gui_background.png"), 4, 16, 16);
    private final Identifier SLOT = id("textures/gui/slot_background.png");

    public MetalChestScreen(MetalChestScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.passEvents = false;
        this.backgroundHeight = 114 + handler.getRows() * 18;
        this.backgroundWidth = 14 + handler.getCols() * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        background.renderCentered(matrices, this.backgroundWidth, this.backgroundHeight);

        RenderSystem.setShaderTexture(0, SLOT);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        for(Slot slot : this.handler.slots) {
            drawTexture(matrices, i + slot.x - 1, j + slot.y - 1, 0, 0, 18, 18, 18, 18);
        }
    }
}
