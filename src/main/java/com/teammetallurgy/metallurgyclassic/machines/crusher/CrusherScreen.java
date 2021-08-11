package com.teammetallurgy.metallurgyclassic.machines.crusher;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teammetallurgy.metallurgyclassic.machines.chest.MetalChestScreenHandler;
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

public class CrusherScreen extends HandledScreen<CrusherScreenHandler> {
    private final NineSlice background = new NineSlice(id("textures/gui/gui_background.png"), 4, 16, 16);
    private final Identifier fuel_off = id("textures/gui/crusher_fuel_off.png");
    private final Identifier fuel_on = id("textures/gui/crusher_fuel_on.png");
    private final Identifier arrows = id("textures/gui/crusher_arrows.png");
    private final Identifier SLOT = id("textures/gui/slot_background.png");

    public CrusherScreen(CrusherScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        System.out.println("Test screen");
        this.passEvents = false;
        this.backgroundHeight = 166;
        this.backgroundWidth = 176;
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

        RenderSystem.setShaderTexture(0, fuel_off);
        drawTexture(matrices, i + 56, j + 35, 0, 0, 16, 16, 16, 16);
        RenderSystem.setShaderTexture(0, fuel_on);
        int segmentSize = 12;
        float fuelLevel = handler.getFuelLevel();
        int pixels = (int) (segmentSize * fuelLevel);
        drawTexture(matrices, i + 57, j + 37 + (segmentSize - pixels), 1, 2 + (segmentSize - pixels), 13, 13 - (segmentSize - pixels), 16, 16);

        RenderSystem.setShaderTexture(0, arrows);
        float processingTime = handler.getProgress();
        pixels = (int) (22 * processingTime);
        drawTexture(matrices, i + 80, j + 36, 5, 2, 22, 12, 32, 32);
        drawTexture(matrices, i + 80, j + 36, 5, 18, 22 - (22 - pixels), 12, 32, 32);
    }
}
