package com.enzorocksmith.MOTM.entity.client.leaper;

import com.enzorocksmith.MOTM.entity.custom.Leaper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SwarmerRenderer extends GeoEntityRenderer<Leaper> {

    public SwarmerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SwarmerModel());
    }

    @Override
    protected float getDeathMaxRotation(Leaper entityLivingBaseIn) {
        return 0.0F;
    }

}
