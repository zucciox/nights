package com.enzorocksmith.MOTM.entity.client.crescent_knight;

import com.enzorocksmith.MOTM.entity.custom.CrescentKnight;
import com.enzorocksmith.MOTM.entity.custom.Shambler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CrescentKnightRenderer extends GeoEntityRenderer<CrescentKnight> {

    public CrescentKnightRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CrescentKnightModel());
    }

    @Override
    protected float getDeathMaxRotation(CrescentKnight entityLivingBaseIn) {
        return 0.0F;
    }

}
