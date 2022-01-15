package com.plusls.ommc;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ModInfo {
    public static String MOD_ID = "ommc";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static String MOD_VERSION;

    static {
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(MOD_ID);
        modContainerOptional.ifPresent(modContainer -> MOD_VERSION = modContainer.getMetadata().getVersion().getFriendlyString());
    }

    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
