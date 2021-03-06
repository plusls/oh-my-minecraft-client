package com.plusls.ommc;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModInfo {
    public static String MOD_ID = "ommc";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
