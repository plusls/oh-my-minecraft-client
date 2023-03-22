package com.plusls.ommc;

import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.compat.minecraft.api.network.chat.ComponentCompatApi;
import top.hendrixshen.magiclib.language.api.I18n;
import top.hendrixshen.magiclib.malilib.impl.ConfigHandler;

//#if MC > 11502
import net.minecraft.network.chat.MutableComponent;
//#else
//$$ import net.minecraft.network.chat.BaseComponent;
//#endif

public class OhMyMinecraftClientReference {
    @Getter
    private static final String currentModIdentifier = "@MOD_IDENTIFIER@-@MINECRAFT_VERSION_IDENTIFY@";
    @Getter
    private static final String modIdentifier = "@MOD_IDENTIFIER@";
    @Getter
    private static final String currentModName = FabricLoader.getInstance().getModContainer(currentModIdentifier).orElseThrow(RuntimeException::new).getMetadata().getName();
    @Getter
    private static final String modName = "@MOD_NAME@";
    @Getter
    private static final String modVersion = FabricLoader.getInstance().getModContainer(currentModIdentifier).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
    @Getter
    private static final Logger logger = LogManager.getLogger(modIdentifier);
    public static ConfigHandler configHandler;

    public static String translate(String key, Object... objects) {
        return I18n.get(OhMyMinecraftClientReference.modIdentifier + "." + key, objects);
    }

    public static @NotNull
    //#if MC > 11502
    MutableComponent
    //#else
    //$$ BaseComponent
    //#endif
    translatable(String key, Object... objects) {
        return ComponentCompatApi.translatable(OhMyMinecraftClientReference.modIdentifier + "." + key, objects);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ResourceLocation identifier(String path) {
        return new ResourceLocation(OhMyMinecraftClientReference.modIdentifier, path);
    }
}
