package com.enzorocksmith.MOTM.entity.client.shambler;

import com.enzorocksmith.MOTM.entity.custom.RemnantHeart;
import com.enzorocksmith.MOTM.entity.custom.Shambler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShamblerRenderer extends GeoEntityRenderer<Shambler> {

    public ShamblerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShamblerModel());
    }

    @Override
    protected float getDeathMaxRotation(Shambler entityLivingBaseIn) {
        return 0.0F;
    }

}
