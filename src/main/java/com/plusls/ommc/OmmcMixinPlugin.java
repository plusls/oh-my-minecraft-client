package com.plusls.ommc;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.discovery.ModResolutionException;
import net.fabricmc.loader.gui.FabricGuiEntry;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OmmcMixinPlugin implements IMixinConfigPlugin {
    public final static String SODIUM_MOD_ID = "sodium";
    private static final String MIXIN_SODIUM = ".sodium.";
    public final static String CANVAS_MOD_ID = "canvas";
    private static final String MIXIN_CANVAS = ".canvas.";
    public static boolean isSodiumLoaded;
    public static boolean isCanvasLoaded;

    public static String getComparableVersion(String version) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < version.length(); ++i) {
            char ch = version.charAt(i);
            if ((ch >= '0' && ch <= '9') || ch == '.') {
                ret.append(ch);
            } else {
                break;
            }
        }
        return ret.toString();
    }

    public static int versionCompare(String version1, String version2) {
        if (version1 == null || version2 == null) {
            throw new IllegalArgumentException("Version can not be null");
        }
        version1 = getComparableVersion(version1);
        version2 = getComparableVersion(version2);
        String[] version1Parts = version1.split("\\.");
        String[] version2Parts = version2.split("\\.");
        int length = Math.max(version1Parts.length, version2Parts.length);
        for (int i = 0; i < length; i++) {
            int version1Part = i < version1Parts.length ?
                    Integer.parseInt(version1Parts[i]) : 0;
            int version2Part = i < version2Parts.length ?
                    Integer.parseInt(version2Parts[i]) : 0;
            if (version1Part < version2Part)
                return -1;
            if (version1Part > version2Part)
                return 1;
        }
        return 0;
    }

    public static void versionCheck(String modid, String version) {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modid);
        if (modContainer.isPresent()) {
            if (versionCompare(modContainer.get().getMetadata().getVersion().getFriendlyString(), version) == -1) {
                ModResolutionException exception =
                        new ModResolutionException(String.format("The version of %s must be greater than %s.", modid, version));
                FabricGuiEntry.displayCriticalError(exception, true);
            }
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        if (FabricLoader.getInstance().isModLoaded(SODIUM_MOD_ID)) {
            isSodiumLoaded = true;
            versionCheck(SODIUM_MOD_ID, "0.2");
        }
        if (FabricLoader.getInstance().isModLoaded(CANVAS_MOD_ID)) {
            isCanvasLoaded = true;
            versionCheck(CANVAS_MOD_ID, "1.0.1511");
        }
    }


    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!isSodiumLoaded && mixinClassName.contains(MIXIN_SODIUM)) {
            return false;
        } else if (!isCanvasLoaded && mixinClassName.contains(MIXIN_CANVAS)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
