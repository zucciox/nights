package com.enzorocksmith.MOTM.entity.client.remnant_heart;

import com.enzorocksmith.MOTM.entity.custom.RemnantHeart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RemnantHeartRenderer extends GeoEntityRenderer<RemnantHeart> {

    public RemnantHeartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RemnantHeartModel());
    }

    @Override
    protected float getDeathMaxRotation(RemnantHeart entityLivingBaseIn) {
        return 0.0F;
    }

}
