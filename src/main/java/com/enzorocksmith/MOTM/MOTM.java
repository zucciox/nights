package com.enzorocksmith.MOTM;

import com.enzorocksmith.MOTM.entity.ModEntities;
import com.enzorocksmith.MOTM.entity.client.MoonRock.MoonRockRenderer;
import com.enzorocksmith.MOTM.entity.client.crescent_knight.CrescentKnightRenderer;
import com.enzorocksmith.MOTM.entity.client.remnant_heart.RemnantHeartRenderer;
import com.enzorocksmith.MOTM.entity.client.shambler.ShamblerRenderer;
import com.enzorocksmith.MOTM.entity.client.slinger.SlingerRenderer;
import com.enzorocksmith.MOTM.entity.custom.MoonRockProjectile;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOTM.MOD_ID)
public class MOTM
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "motm";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public MOTM(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModEntities.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.REMNANTHEART.get(), RemnantHeartRenderer::new);
            EntityRenderers.register(ModEntities.SHAMBLER.get(), ShamblerRenderer::new);
            EntityRenderers.register(ModEntities.SLINGER.get(), SlingerRenderer::new);
            EntityRenderers.register(ModEntities.CRESCENTKNIGHT.get(), CrescentKnightRenderer::new);

            EntityRenderers.register(ModEntities.MOONROCKPROJECTILE.get(), MoonRockRenderer::new);
        }

    }

    private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tick, Runnable action) {
    if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
        workQueue.add(new Tuple<>(action, tick));
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
        workQueue.forEach(work -> {
            work.setB(work.getB() - 1);
            if (work.getB() == 0)
                actions.add(work);
        });
        actions.forEach(e -> e.getA().run());
        workQueue.removeAll(actions);
    }
}
