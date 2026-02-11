package com.enzorocksmith.MOTM.entity.client.MoonRock;


import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.entity.custom.MoonRockProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class MoonRockModel extends EntityModel<MoonRockProjectile> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MOTM.MOD_ID, "moon_rock"), "main");
    private final ModelPart rock;

    public MoonRockModel(ModelPart root) {
        this.rock = root.getChild("rock");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition rock = partdefinition.addOrReplaceChild("rock", CubeListBuilder.create().texOffs(-1, -1).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(-1, -1).addBox(-1.55F, -2.5F, -2.6F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.55F, 0.5F, 0.6F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        rock.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(MoonRockProjectile moonRockProjectile, float v, float v1, float v2, float v3, float v4) {
    }
}