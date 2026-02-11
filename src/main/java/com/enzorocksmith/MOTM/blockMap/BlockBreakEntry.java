package com.enzorocksmith.MOTM.blockMap;

public class BlockBreakEntry {
    public float maxDura;
    public float dura;
    public float coolDown;

    public BlockBreakEntry(float hardness) {
        maxDura = hardness*100;
        dura = maxDura;
    }

}
