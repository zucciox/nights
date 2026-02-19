package com.enzorocksmith.MOTM.events;

import com.enzorocksmith.MOTM.blockMap.BlockBreakMap;
import com.enzorocksmith.MOTM.entity.custom.Leaper;
import com.enzorocksmith.MOTM.entity.custom.MoonMob;
import com.enzorocksmith.MOTM.entity.custom.RemnantHeart;
import com.enzorocksmith.MOTM.nightprogression.NightProgression;
import com.enzorocksmith.MOTM.MOTM;
import com.enzorocksmith.MOTM.utilities.MOTMUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MOTM.MOD_ID)
public class GameEvents {

    @SubscribeEvent
    public static void levelTick(TickEvent.LevelTickEvent event) {

        // Set Basic Data
        NightProgression.day = (int) (event.level.getDayTime() / 24000);
        NightProgression.isnight = !event.level.isDay();
        BlockBreakMap.tickTickers();

        // Spawn Horde

        if (NightProgression.heart == null) return;
        if (NightProgression.heart.level().isClientSide()) return;

        if (!NightProgression.isHordeSpawned() && (NightProgression.heart != null) && MOTMUtils.isNight(event.level)) {

            NightProgression.heart.hordeSpawned = 1;
            NightProgression.spawnHorde();
        }
    }

    @SubscribeEvent
    public static void heartTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof RemnantHeart) {
            NightProgression.heart = (RemnantHeart) event.getEntity();

            if (NightProgression.heart.level() instanceof ServerLevel) {

                ServerLevel level = (ServerLevel) NightProgression.heart.level();

                for (int i = 0; i < NightProgression.spawnRing.size(); i++) {
                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER, NightProgression.spawnRing.get(i).x, NightProgression.spawnRing.get(i).y + .25, NightProgression.spawnRing.get(i).z, 1, 0, 0, 0, 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void entityhurt(LivingDamageEvent event) {
        if (event.getEntity() instanceof MoonMob && !event.getEntity().isDeadOrDying()) {
            ((MoonMob) event.getEntity()).triggerAnim("hurt", "hurt");
        }
    }

    @SubscribeEvent
    public static void fallDamage(LivingFallEvent event) {
        if (event.getEntity() instanceof Leaper) event.setCanceled(true);
    }
}
