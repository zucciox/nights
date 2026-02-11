package com.enzorocksmith.MOTM.entity.client.remnant_heart;

import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.entity.custom.RemnantHeart;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RemnantHeartModel extends GeoModel<RemnantHeart> {

    @Override
    public ResourceLocation getModelResource(RemnantHeart animatable) {
        return new ResourceLocation(MOTM.MOD_ID,
                "geo/entity/remnant_heart.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RemnantHeart animatable) {
        return new ResourceLocation(MOTM.MOD_ID,
                "textures/entity/remnant_heart.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RemnantHeart animatable) {
        return new ResourceLocation(MOTM.MOD_ID,
                "animations/entity/remnant_heart.animation.json");
    }
}

