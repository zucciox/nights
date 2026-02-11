package com.enzorocksmith.MOTM.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.lang.Class;
import java.util.ArrayList;
import java.util.List;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;


public class MOTMUtils {


    public static void printToChat (String msg) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(Component.literal(msg), false);
        }
    }

    public static boolean isNight (Level level) {
        int days = Mth.floor((float) level.getDayTime() / 24000);

        int adjustedTime = (int) level.getDayTime() - (days * 24000);

        if (adjustedTime >= 13000 && adjustedTime <= 24000) {
            printToChat("is night");
            return true;
        }
        printToChat("is day");
        return false;
    }

    public static double vecDist(Vec3 v1, Vec3 v2) {
        return Math.sqrt(Math.pow(v2.x - v1.x, 2) + Math.pow(v2.y - v1.y, 2) + Math.pow(v2.z - v1.z, 2));
    }


    public static <T extends LivingEntity> T findNearestEntityOfClass(LivingEntity self, Class<T> targetType) {
        List<T> list = self.level().getEntitiesOfClass(targetType, new AABB(self.position(), self.position()).inflate(100d), (T e) -> e != self);
        T nearestEntity = null;
        double nearestDistance = Double.MAX_VALUE;

        for (T entity : list) {
            double distance = self.distanceToSqr(entity);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestEntity = entity;
            }
        }

        return nearestEntity;
    }

    public static float getBlastRes(Level level, BlockPos block, Entity dummyEntity) {
        BlockState blockState = level.getBlockState(block);
        List<BlockPos> dummyList = new ArrayList<>();
        return blockState.getExplosionResistance(level, block, new Explosion(level, dummyEntity, 0d, 0d, 0d, 0, dummyList));
    }
}
