package com.enzorocksmith.MOTM.blockMap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class BlockBreakMap {
    public static ConcurrentHashMap<BlockPos, BlockBreakEntry> map = new ConcurrentHashMap<>();

    public static void tickTickers() {
        for (BlockBreakEntry i : map.values()) {
            i.coolDown--;
        }
    }

}
