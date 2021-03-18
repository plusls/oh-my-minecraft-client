package com.plusls.ommc.feature.realSneaking;

import com.plusls.ommc.config.Configs;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class RealSneakingEventHandler {
    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(RealSneakingEventHandler::preClientTick);
    }

    private static void preClientTick(MinecraftClient minecraftClient) {
        if (minecraftClient.player != null) {
            if (Configs.FeatureToggle.REAL_SNEAKING.getBooleanValue() && minecraftClient.player.isSneaking()) {
                minecraftClient.player.stepHeight = 0.001f;
            } else {
                minecraftClient.player.stepHeight = 0.6f;
            }
        }
    }
}
