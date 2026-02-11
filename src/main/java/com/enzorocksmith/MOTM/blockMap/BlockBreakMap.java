package com.enzorocksmith.MOTM.blockMap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class BlockBreakMap {
    public static HashMap<BlockPos, BlockBreakEntry> map = new HashMap<>();

    public static void tickTickers() {
        for (BlockBreakEntry i : map.values()) {
            i.coolDown--;
        }
    }

}
