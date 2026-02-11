package com.enzorocksmith.MOTM.entity;


import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.entity.custom.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOTM.MOD_ID);

    public static final Supplier<EntityType<RemnantHeart>> REMNANTHEART =
            ENTITIES.register("remnant_heart",
                    () -> EntityType.Builder.of(RemnantHeart::new, MobCategory.CREATURE)
                            .sized(1f, 2.5f)
                            .build("remnant_heart"));

    public static final Supplier<EntityType<Shambler>> SHAMBLER =
            ENTITIES.register("shambler",
                    () -> EntityType.Builder.of(Shambler::new, MobCategory.CREATURE)
                            .sized(.5f, 1.8f)
                            .build("shambler"));
    public static final Supplier<EntityType<Slinger>> SLINGER =
            ENTITIES.register("slinger",
                    () -> EntityType.Builder.of(Slinger::new, MobCategory.CREATURE)
                            .sized(.5f, 1.7f)
                            .build("slinger"));
    public static final Supplier<EntityType<MoonRockProjectile>> MOONROCKPROJECTILE =
            ENTITIES.register("moon_rock_projectile",
                    () -> EntityType.Builder.<MoonRockProjectile>of(MoonRockProjectile::new, MobCategory.MISC)
                            .sized(.25f, .25f)
                            .build("moon_rock_projectile"));
    public static final Supplier<EntityType<CrescentKnight>> CRESCENTKNIGHT =
            ENTITIES.register("crescent_knight",
                    () -> EntityType.Builder.of(CrescentKnight::new, MobCategory.CREATURE)
                            .sized(.8f, 2.7f)
                            .build("crescent_knight"));


    public static void register(IEventBus eventBus){ ENTITIES.register(eventBus);
    }
}