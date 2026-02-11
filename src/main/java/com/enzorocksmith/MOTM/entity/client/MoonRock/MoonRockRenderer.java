package com.enzorocksmith.MOTM.entity.client.MoonRock;

import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.entity.custom.MoonRockProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class MoonRockRenderer extends EntityRenderer<MoonRockProjectile> {
    private MoonRockModel model;

    public MoonRockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new MoonRockModel(context.bakeLayer(MoonRockModel.LAYER_LOCATION));
    }

    @Override
    public void render(MoonRockProjectile pEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight){
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(pEntity.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(pEntity.getXRot()));


        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
                buffer, this.model.renderType(this.getTextureLocation(pEntity)), false, false);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, .8f);
        poseStack.popPose();
    }


    @Override
    public ResourceLocation getTextureLocation(MoonRockProjectile entity) {
        return ResourceLocation.fromNamespaceAndPath(MOTM.MOD_ID, "textures/entity/moon_rock.png");
    }
}
