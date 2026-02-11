package com.enzorocksmith.MOTM.entity.client.crescent_knight;

import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.entity.custom.CrescentKnight;
import com.enzorocksmith.MOTM.entity.custom.Shambler;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CrescentKnightModel extends DefaultedEntityGeoModel<CrescentKnight> {

    public CrescentKnightModel() {
        super(new ResourceLocation(MOTM.MOD_ID, "crescent_knight"), true);
    }

    /*

    @Override
    public ResourceLocation getModelResource(Shambler animatable) {
        return new ResourceLocation(motm.MOD_ID,
                "geo/shambler.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Shambler animatable) {
        return new ResourceLocation(motm.MOD_ID,
                "textures/shambler.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Shambler animatable) {
        return new ResourceLocation(motm.MOD_ID,
                "animations/shambler.animation.json");
    }


     */
}

