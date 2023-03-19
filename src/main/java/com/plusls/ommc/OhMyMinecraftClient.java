package com.plusls.ommc;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceResourceLoader;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointResourceLoader;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import com.plusls.ommc.feature.preventWastageOfWater.PreventWastageOfWaterHandler;
import com.plusls.ommc.feature.realSneaking.RealSneakingEventHandler;
import net.fabricmc.api.ClientModInitializer;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;
import top.hendrixshen.magiclib.malilib.impl.ConfigHandler;
import top.hendrixshen.magiclib.malilib.impl.ConfigManager;

public class OhMyMinecraftClient implements ClientModInitializer {
    private static final int CONFIG_VERSION = 1;

    @Dependencies(and = {
            //#if MC > 11701
            @Dependency(value = "canvas", versionPredicate = ">=1.0.2308", optional = true),
            @Dependency(value = "fabric", versionPredicate = ">=0.75.0", optional = true),
            @Dependency(value = "frex", versionPredicate = ">=6.0.242", optional = true),
            @Dependency(value = "sodium", versionPredicate = ">=0.4.1", optional = true),
            //#elseif MC > 11605
            @Dependency(value = "sodium", versionPredicate = ">=0.3.4", optional = true),
            //#elseif MC > 11502
            @Dependency(value = "sodium", versionPredicate = ">=0.2.0", optional = true),
            //#endif
    })
    @Override
    public void onInitializeClient() {
        LavaSourceResourceLoader.init();
        HighlightWaypointResourceLoader.init();
        ConfigManager cm = ConfigManager.get(OhMyMinecraftClientReference.getModIdentifier());
        cm.parseConfigClass(Configs.class);
        OhMyMinecraftClientReference.configHandler = new ConfigHandler(OhMyMinecraftClientReference.getModIdentifier(), cm, CONFIG_VERSION);
        OhMyMinecraftClientReference.configHandler.postDeserializeCallback = Configs::postDeserialize;
        ConfigHandler.register(OhMyMinecraftClientReference.configHandler);
        Configs.init(cm);
        RealSneakingEventHandler.init();
        HighlightWaypointUtil.init();
        PreventWastageOfWaterHandler.init();
    }
}

