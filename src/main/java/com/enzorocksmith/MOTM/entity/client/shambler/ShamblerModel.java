package com.enzorocksmith.MOTM.entity.client.shambler;

import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.entity.custom.RemnantHeart;
import com.enzorocksmith.MOTM.entity.custom.Shambler;
import com.enzorocksmith.MOTM.utilities.MOTMUtils;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class ShamblerModel extends DefaultedEntityGeoModel<Shambler> {

    public ShamblerModel() {
        super(new ResourceLocation(MOTM.MOD_ID, "shambler"), true);
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

