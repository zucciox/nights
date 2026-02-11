package com.enzorocksmith.MOTM.nightprogression;

import com.enzorocksmith.MOTM.entity.ModEntities;
import com.enzorocksmith.MOTM.entity.custom.RemnantHeart;
import com.enzorocksmith.MOTM.entity.custom.Shambler;
import com.enzorocksmith.MOTM.utilities.MOTMUtils;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class NightProgression {

    public static int day;
    public static boolean isnight;
    public static Player[] players;
    public static Vec3 heartLoc;
    public static RemnantHeart heart;
    public static final Map<Integer, Set<Mob>> hordeMobMap = Maps.newHashMap();
    public static ArrayList<Vec3> spawnRing = new ArrayList<Vec3>();
    public static Random random = new Random();

    public static void spawnHorde() {

        MOTMUtils.printToChat("spawnHorde was called");

        switch (day) {
            case 10:
                genSpawnRing(25);

                for (int i = 0; i < 20; i++) {

                    Vec3 ringPos = spawnRing.get(random.nextInt(0, spawnRing.size()));

                    Shambler shambler = new Shambler(ModEntities.SHAMBLER.get(), heart.level());
                    shambler.setPos(ringPos);
                    heart.level().addFreshEntity(shambler);
                    //Nightsutils.printToChat("Spawned zombie at " + ringPos.x + ", " + ringPos.y + ", " + ringPos.z);
                    shambler.setTarget(heart);

                }
        }
    }

    public static void genSpawnRing(int radius) {

        int centerX = (int) heart.position().x, centerZ = (int) heart.position().z;
        Vec3 tracker = Vec3.ZERO;

        for (int angle = 1; angle <= 360; angle++) {
            double radians = Math.toRadians(angle);
            // Calculate next point
            int x = (int) (centerX + radius * Math.cos(radians));
            int z = (int) (centerZ + radius * Math.sin(radians));

            tracker = new Vec3(x, heart.level().getHeight(Heightmap.Types.WORLD_SURFACE, (int) tracker.x, (int) tracker.z), z);
            spawnRing.add(tracker);

        }

    }

    public static Vec3 getValidSpawnPos(Vec3 ringPos) {

        Vec3 vec = ringPos;

        for (int i = 0; i < 10; i++) {
            if (heart.level().canSeeSky(new BlockPos(BlockPos.containing(vec)))) {
                return vec;
            } else {
                vec = vec.add(0, 1, 0);
            }
        }
        MOTMUtils.printToChat("Couldn't find pos");
        return Vec3.ZERO;
    }


    public static boolean isHordeSpawned() {
        if (heart.hordeSpawned == 0) return false;
        else return true;
    }

}
