package com.teammetallurgy.metallurgyclassic.machines.crusher;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import static com.teammetallurgy.metallurgyclassic.MetallurgyClassic.id;
import static net.minecraft.client.render.TexturedRenderLayers.CHEST_ATLAS_TEXTURE;

public class CrusherEntityRenderer implements BlockEntityRenderer<CrusherBlockEntity> {
    private final CrusherModel model;

    public CrusherEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new CrusherModel(CrusherModel.getTexturedModelData().createModel());
    }

    public void render(CrusherBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        boolean worldExists = world != null;
        BlockState blockState = worldExists ? entity.getCachedState() : CrusherComponent.getBlock(CrusherComponent.Type.STONE).getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        Block block = blockState.getBlock();
        if (block instanceof CrusherBlock crusherBlock) {
            matrices.push();
            float f = blockState.get(CrusherBlock.FACING).asRotation();
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrices.translate(-0.5D, -0.5D, -0.5D);

            boolean lit = blockState.get(CrusherBlock.LIT);
            String isLitSuffix = lit ? "_on" : "";
            SpriteIdentifier spriteIdentifier = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, id("entity/crusher/crusher_" + CrusherComponent.getType(entity).name + isLitSuffix));
            VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
            model.setAngles(entity, lit);
            model.render(matrices, vertexConsumer, light, overlay, 1, 1, 1, 1);

            matrices.pop();
        }
    }
}