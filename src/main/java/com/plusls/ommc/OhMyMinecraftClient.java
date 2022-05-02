package com.plusls.ommc;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceResourceLoader;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointResourceLoader;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import com.plusls.ommc.feature.preventWastageOfWater.PreventWastageOfWaterHandler;
import com.plusls.ommc.feature.realSneaking.RealSneakingEventHandler;
import net.fabricmc.api.ClientModInitializer;
import top.hendrixshen.magiclib.config.ConfigHandler;
import top.hendrixshen.magiclib.config.ConfigManager;

public class OhMyMinecraftClient implements ClientModInitializer {
    private static final int CONFIG_VERSION = 1;

    //      "custom": {
//        "compat": {
//            "sodium": ">=0.4.0-alpha5+build.9",
//                    "canvas": ">=1.0.2282"
//        }
//    },
    @Override
    public void onInitializeClient() {
        LavaSourceResourceLoader.init();
        HighlightWaypointResourceLoader.init();
        top.hendrixshen.magiclib.config.ConfigManager cm = ConfigManager.get(ModInfo.MOD_ID);
        cm.parseConfigClass(Configs.class);
        ModInfo.configHandler = new ConfigHandler(ModInfo.MOD_ID, cm, CONFIG_VERSION);
        ModInfo.configHandler.postDeserializeCallback = Configs::postDeserialize;
        ConfigHandler.register(ModInfo.configHandler);
        Configs.init(cm);
        RealSneakingEventHandler.init();
        HighlightWaypointUtil.init();
        PreventWastageOfWaterHandler.init();
    }
}

