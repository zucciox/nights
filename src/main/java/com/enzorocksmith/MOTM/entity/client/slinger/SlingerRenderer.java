package com.enzorocksmith.MOTM.entity.client.slinger;

import com.enzorocksmith.MOTM.entity.custom.Slinger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SlingerRenderer extends GeoEntityRenderer<Slinger> {

    public SlingerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SlingerModel());
    }

    @Override
    protected float getDeathMaxRotation(Slinger entityLivingBaseIn) {
        return 0.0F;
    }

}
