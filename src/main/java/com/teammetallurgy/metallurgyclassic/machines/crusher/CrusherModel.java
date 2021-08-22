// Made with Model Converter by Globox_Z
// Generate all required imports
// Made with Blockbench 3.9.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports
package com.teammetallurgy.metallurgyclassic.machines.crusher;

import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class CrusherModel extends EntityModel<Entity> {
    private static final float DEGREES_45 = (float) (Math.PI/4F);
    private static final float DEGREES_90 = (float) (Math.PI/2F);
    private static final float DEGREES_360 = (float) (2 * Math.PI);
    private final ModelPart rollerbone1;
    private final ModelPart rollerbone2;
    private final ModelPart bb_main;

    public CrusherModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
        this.rollerbone1 = bb_main.getChild("rollerbone1");
        this.rollerbone2 = bb_main.getChild("rollerbone2");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData base = root.addChild("bb_main", ModelPartBuilder.create().uv(0,0).cuboid(0, 0, 0, 16.0F, 13.0F, 16.0F), ModelTransform.pivot(0.0F,0.0F,0.0F));

        ModelPartData roller1 = base.addChild("rollerbone1", ModelPartBuilder.create().uv(0,30).cuboid(-7.0F, -2.0F, -2.0F, 14.0F, 4.0F, 4.0F), ModelTransform.of(8.0F,13.0F,4.0F, DEGREES_45, 0.0F, 0.0F));
        roller1.addChild("teeth1", ModelPartBuilder.create().uv(0,38).cuboid(-7.0F, 2.0F, 0.0F, 14.0F, 1.0F, 0.0F), ModelTransform.rotation(0.0F,0.0F,0.0F));
        roller1.addChild("teeth2", ModelPartBuilder.create().uv(0,38).cuboid(-7.0F, 2.0F, 0.0F, 14.0F, 1.0F, 0.0F), ModelTransform.rotation(DEGREES_90,0.0F,0.0F));
        roller1.addChild("teeth3", ModelPartBuilder.create().uv(0,38).cuboid(-7.0F, 2.0F, 0.0F, 14.0F, 1.0F, 0.0F), ModelTransform.rotation(2 * DEGREES_90,0.0F,0.0F));
        roller1.addChild("teeth4", ModelPartBuilder.create().uv(0,38).cuboid(-7.0F, 2.0F, 0.0F, 14.0F, 1.0F, 0.0F), ModelTransform.rotation(3 * DEGREES_90,0.0F,0.0F));

        ModelPartData roller2 = base.addChild("rollerbone2", ModelPartBuilder.create().uv(0,30).cuboid(-7.0F, -2.0F, -2.0F, 14.0F, 4.0F, 4.0F), ModelTransform.of(8.0F,13.0F,12.0F, (float) (Math.PI/4F), 0.0F, 0.0F));
        roller2.addChild("teeth1", ModelPartBuilder.create().uv(0,38).cuboid(-7.0F, 2.0F, 0.0F, 14.0F, 1.0F, 0.0F), ModelTransform.rotation(0.0F,0.0F,0.0F));
        roller2.addChild("teeth2", ModelPartBuilder.create().uv(0,38).cuboid(-7.0F, 2.0F, 0.0F, 14.0F, 1.0F, 0.0F), ModelTransform.rotation(DEGREES_90,0.0F,0.0F));
        roller2.addChild("teeth3", ModelPartBuilder.create().uv(0,38).cuboid(-7.0F, 2.0F, 0.0F, 14.0F, 1.0F, 0.0F), ModelTransform.rotation(2 * DEGREES_90,0.0F,0.0F));
        roller2.addChild("teeth4", ModelPartBuilder.create().uv(0,38).cuboid(-7.0F, 2.0F, 0.0F, 14.0F, 1.0F, 0.0F), ModelTransform.rotation(3 * DEGREES_90,0.0F,0.0F));

        return TexturedModelData.of(modelData,64,64);
    }

    public void setAngles(CrusherBlockEntity entity, boolean lit) {
        rollerbone1.pitch = DEGREES_45;
        rollerbone2.pitch = DEGREES_45;
        if(lit || Math.abs(rollerbone1.pitch - DEGREES_45) > 0.1) {
            rollerbone1.pitch = DEGREES_360 * (System.currentTimeMillis() % 4000 / 4000F);
            rollerbone2.pitch = -2 * DEGREES_360 * (System.currentTimeMillis() % 4000 / 4000F);
        } else {
            rollerbone1.pitch = DEGREES_45;
            rollerbone2.pitch = DEGREES_45;
        }
    }

    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        rollerbone1.render(matrixStack, buffer, packedLight, packedOverlay);
        rollerbone2.render(matrixStack, buffer, packedLight, packedOverlay);
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}