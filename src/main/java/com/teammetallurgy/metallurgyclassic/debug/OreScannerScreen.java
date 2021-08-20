package com.teammetallurgy.metallurgyclassic.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teammetallurgy.metallurgyclassic.utils.NineSlice;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;

public class OreScannerScreen extends HandledScreen<OreScannerScreenHandler> {
    private final NineSlice background = new NineSlice(id("textures/gui/gui_background.png"), 4, 16, 16);
    private final Identifier SLOT = id("textures/gui/slot_background.png");
    private final NineSlice bar = new NineSlice(id("textures/gui/slot_background.png"), 1, 16, -18, -18);
    private final NineSlice slot = new NineSlice(id("textures/gui/slot_background.png"), 6, 18, 18);

    public OreScannerScreen(OreScannerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 266;
        this.playerInventoryTitleY = 10000;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        background.renderCentered(matrices, this.backgroundWidth, this.backgroundHeight);

        RenderSystem.setShaderTexture(0, SLOT);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;

        List<Block> countables = new ArrayList<>();
        for(Slot slot : this.handler.slots) {
            if(slot.getStack() != null && slot.getStack().getItem() != null && slot.getStack().getItem() instanceof BlockItem) {
                Block block = ((BlockItem)(slot.getStack().getItem())).getBlock();
                countables.add(block);
            }
        }

        Map<Block, Integer> counts = getBlockCount();
        Optional<Integer> maxCount = this.handler.slots.stream()
                .filter(slot -> slot instanceof OreScannerScreenHandler.DisplaySlot)
                .filter(slot -> !slot.getStack().isEmpty())
                .filter(slot -> slot.getStack().getItem() instanceof BlockItem)
                .filter(slot -> counts.containsKey(((BlockItem) slot.getStack().getItem()).getBlock()))
                .map(slot -> counts.get(((BlockItem) slot.getStack().getItem()).getBlock()))
                .max(Comparator.naturalOrder());

        int maxValue = 200;
        if(maxCount.isPresent()) {
            //maxValue = maxCount.get();
        }

        int slotY = 266 - 5 - 18 - 5;
        int slot_height = 220 - 20;
        for(Slot slot : this.handler.slots) {
            drawTexture(matrices, i + slot.x - 1, j + slot.y - 1, 0, 0, 18, 18, 18, 18);
        }
        for(Slot slot : this.handler.slots) {
            if (slot instanceof OreScannerScreenHandler.DisplaySlot) {
                if (slot.getStack() != null && slot.getStack().getItem() != null && slot.getStack().getItem() instanceof BlockItem) {
                    if(!slot.getStack().isEmpty()) {
                        Block block = ((BlockItem) (slot.getStack().getItem())).getBlock();
                        int count = 0;
                        if(counts.containsKey(block)) {
                            count = counts.get(block);
                        }
                        drawBar(matrices, x + slot.x, y + slot.y - 1, slot_height, count * 200/maxValue);
                    } else {
                        drawBar(matrices, x + slot.x, y + slot.y - 1, slot_height, 0);
                    }
                }
            }
        }
    }

    private void drawBar(MatrixStack matrices, int x, int y, int height, int value) {
        int min = 4;
        slot.render(matrices, x, y - height, 16, height);
        if(value > -1) {
            int renderedValue = value + 4;
            if(value <= 0) {
                RenderSystem.setShaderColor(0.9f, 0.1f, 0.1f, 1.0f);
            } else if (value < min) {
                RenderSystem.setShaderColor(1f, 0.7f, 0.2f, 1.0f);
            } else {
                RenderSystem.setShaderColor(0.1f, 0.9f, 0.1f, 1.0f);
            }
            bar.render(matrices, x + 1, y - renderedValue - 1, 14, renderedValue);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            String text = "" + value;
            drawCenteredText(matrices, textRenderer, text, x + 8, y - 11, Color.WHITE.getRGB());
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private Map<Block, Integer> getBlockCount() {
        var counts = new HashMap<Block, Integer>();
        if(client.world != null && client.player != null) {
            for(int x = 0; x < 16; x++) {
                for(int y = 0; y < 256; y++) {
                    for(int z = 0; z < 16; z++) {
                        BlockPos pos = new BlockPos(client.player.getChunkPos().x * 16 + x, y, client.player.getChunkPos().z * 16 + z);
                        Block block = client.world.getBlockState(pos).getBlock();

                        if(counts.containsKey(block)) {
                            counts.put(block, counts.get(block) + 1);
                        } else {
                            counts.put(block, 1);
                        }
                    }
                }
            }
        }
        return counts;
    }
}
