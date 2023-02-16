package com.plusls.ommc;

import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.ComponentCompatApi;
import top.hendrixshen.magiclib.config.ConfigHandler;
import top.hendrixshen.magiclib.language.I18n;

//#if MC > 11502
import net.minecraft.network.chat.MutableComponent;
import top.hendrixshen.magiclib.util.VersionParser;
//#else
//$$ import net.minecraft.network.chat.BaseComponent;
//#endif

public class OhMyMinecraftClientReference {
    @Getter
    private static final String currentModIdentifier = "${mod_id}-${minecraft_version_id}";
    @Getter
    private static final String modIdentifier = "${mod_id}";
    @Getter
    private static final String currentModName = FabricLoader.getInstance().getModContainer(currentModIdentifier).orElseThrow(RuntimeException::new).getMetadata().getName();
    @Getter
    private static final String modName = "${mod_name}";
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
