package com.enzorocksmith.MOTM.events;


import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.entity.ModEntities;
import com.enzorocksmith.MOTM.entity.client.MoonRock.MoonRockModel;
import com.enzorocksmith.MOTM.entity.custom.*;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(MoonRockModel.LAYER_LOCATION, MoonRockModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.REMNANTHEART.get(), RemnantHeart.createAttributes().build());
        event.put(ModEntities.SHAMBLER.get(), Shambler.createAttributes().build());
        event.put(ModEntities.SLINGER.get(), Slinger.createAttributes().build());
        event.put(ModEntities.CRESCENTKNIGHT.get(), CrescentKnight.createAttributes().build());
        event.put(ModEntities.SWARMER.get(), Leaper.createAttributes().build());
    }

    @SubscribeEvent
    public static void spawnRestriction(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.SHAMBLER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Shambler::shamblerSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);

        event.register(ModEntities.SLINGER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Slinger::slingerSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);

        event.register(ModEntities.CRESCENTKNIGHT.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CrescentKnight::crescentKnightSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);

        event.register(ModEntities.SWARMER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Leaper::leaperSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

}
