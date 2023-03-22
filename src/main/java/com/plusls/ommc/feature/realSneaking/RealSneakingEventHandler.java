package com.plusls.ommc.feature.realSneaking;

import com.plusls.ommc.config.Configs;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class RealSneakingEventHandler {
    final private static float MIN_STEP_HEIGHT = 0.001f;
    private static float prevStepHeight;

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(RealSneakingEventHandler::preClientTick);
    }

    private static void preClientTick(Minecraft minecraftClient) {
        if (minecraftClient.player != null) {
            if (minecraftClient.player.maxUpStepCompat() - MIN_STEP_HEIGHT >= 0.00001) {
                prevStepHeight = minecraftClient.player.maxUpStepCompat();
            }
            if (Configs.realSneaking && minecraftClient.player.isShiftKeyDown()) {
                minecraftClient.player.setMaxUpStepCompat(MIN_STEP_HEIGHT);
            } else {
                minecraftClient.player.setMaxUpStepCompat(prevStepHeight);
            }
        }
    }
}
