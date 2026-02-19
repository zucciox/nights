package com.enzorocksmith.MOTM.entity.client.leaper;

import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.entity.custom.Leaper;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SwarmerModel extends DefaultedEntityGeoModel<Leaper> {

    public SwarmerModel() {
        super(new ResourceLocation(MOTM.MOD_ID, "leaper"), true);
    }

    /*

    @Override
    public ResourceLocation getModelResource(Swarmer animatable) {
        return new ResourceLocation(motm.MOD_ID,
                "geo/shambler.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Swarmer animatable) {
        return new ResourceLocation(motm.MOD_ID,
                "textures/shambler.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Swarmer animatable) {
        return new ResourceLocation(motm.MOD_ID,
                "animations/shambler.animation.json");
    }


     */
}

