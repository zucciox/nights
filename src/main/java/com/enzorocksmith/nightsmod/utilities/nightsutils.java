package com.enzorocksmith.nightsmod.utilities;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class nightsutils {

    public static void printToChat (Player DM, String msg) {
        DM.displayClientMessage(Component.literal(msg), false);
    }
}
