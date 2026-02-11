package com.enzorocksmith.MOTM.entity.client.slinger;

import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.entity.custom.Slinger;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SlingerModel extends DefaultedEntityGeoModel<Slinger> {

    public SlingerModel() {
        super(new ResourceLocation(MOTM.MOD_ID, "slinger"), true);
    }

    /*

    @Override
    public ResourceLocation getModelResource(Slinger animatable) {
        return new ResourceLocation(motm.MOD_ID,
                "geo/shambler.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Slinger animatable) {
        return new ResourceLocation(motm.MOD_ID,
                "textures/shambler.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Slinger animatable) {
        return new ResourceLocation(motm.MOD_ID,
                "animations/shambler.animation.json");
    }


     */
}

