package com.plusls.ommc;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;
import net.fabricmc.loader.impl.util.version.VersionPredicateParser;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OmmcMixinPlugin implements IMixinConfigPlugin {

    static private Method oldMatchesMethod;
    static private Method oldDisplayCriticalErrorMethod;

    static {
        try {
            oldMatchesMethod = Class.forName("net.fabricmc.loader.util.version.VersionPredicateParser").getMethod("matches", Version.class, String.class);
            oldDisplayCriticalErrorMethod = Class.forName("net.fabricmc.loader.gui.FabricGuiEntry").getMethod("displayCriticalError", Throwable.class, boolean.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }
    }

    private static boolean myMatches(Version version, String s) {
        try {
            if (oldMatchesMethod != null) {
                return (boolean) oldMatchesMethod.invoke(null, version, s);
            }
            return VersionPredicateParser.parse(s).test(version);
        } catch (VersionParsingException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean checkDependency(String modId, String version) {
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(modId);
        if (modContainerOptional.isPresent()) {
            ModContainer modContainer = modContainerOptional.get();
            return myMatches(modContainer.getMetadata().getVersion(), version);
        }
        return false;
    }

    private static void myDisplayCriticalError(Throwable exception) {
        if (oldDisplayCriticalErrorMethod != null) {
            try {
                oldDisplayCriticalErrorMethod.invoke(null, exception, true);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            FabricGuiEntry.displayCriticalError(exception, true);
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        FabricLoader.getInstance().getModContainer(ModInfo.MOD_ID).ifPresent(
                container -> container.getMetadata().getCustomValue("compat").getAsObject().forEach(
                        customValue -> {
                            if (ModInfo.isModLoaded(customValue.getKey()) &&
                                    !checkDependency(customValue.getKey(), customValue.getValue().getAsString())) {
                                myDisplayCriticalError(new IllegalStateException(String.format("Mod %s requires: %s",
                                        customValue.getKey(), customValue.getValue().getAsString())));
                            }
                        }
                )
        );
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
