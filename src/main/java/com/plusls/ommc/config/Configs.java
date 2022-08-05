package com.plusls.ommc.config;

import com.google.common.collect.Lists;
import com.plusls.ommc.ModInfo;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import com.plusls.ommc.feature.sortInventory.SortInventoryUtil;
import com.plusls.ommc.gui.GuiConfigs;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
//#if MC > 11802
import net.minecraft.Util;
import net.minecraft.client.gui.chat.ClientChatPreview;
import net.minecraft.network.chat.Component;
//#endif
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import top.hendrixshen.magiclib.config.ConfigHandler;
import top.hendrixshen.magiclib.config.ConfigManager;
import top.hendrixshen.magiclib.config.Option;
import top.hendrixshen.magiclib.config.annotation.Config;
import top.hendrixshen.magiclib.config.annotation.Hotkey;
import top.hendrixshen.magiclib.config.annotation.Numeric;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.dependency.annotation.OptionDependencyPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Configs {
    private static final List<String> OLD_WORLD_EATER_MINE_HELPER_WHITELIST = new ArrayList<>();
    private static final List<String> OLD_BLOCK_MODEL_NO_OFFSET_BLACKLIST = new ArrayList<>();
    private static final List<String> OLD_BLOCK_MODEL_NO_OFFSET_WHITELIST = new ArrayList<>();

    public static void updateOldStringList() {
        OLD_BLOCK_MODEL_NO_OFFSET_BLACKLIST.clear();
        OLD_BLOCK_MODEL_NO_OFFSET_BLACKLIST.addAll(blockModelNoOffsetBlacklist);
        OLD_BLOCK_MODEL_NO_OFFSET_WHITELIST.clear();
        OLD_BLOCK_MODEL_NO_OFFSET_WHITELIST.addAll(blockModelNoOffsetWhitelist);
        OLD_WORLD_EATER_MINE_HELPER_WHITELIST.clear();
        OLD_WORLD_EATER_MINE_HELPER_WHITELIST.addAll(worldEaterMineHelperWhitelist);
    }

    public static void checkIsStringListChanged() {
        boolean dirty = false;
        if (!OLD_WORLD_EATER_MINE_HELPER_WHITELIST.equals(worldEaterMineHelperWhitelist) ||
                !OLD_BLOCK_MODEL_NO_OFFSET_BLACKLIST.equals(blockModelNoOffsetBlacklist) ||
                !OLD_BLOCK_MODEL_NO_OFFSET_WHITELIST.equals(blockModelNoOffsetWhitelist)) {
            Minecraft.getInstance().levelRenderer.allChanged();
            dirty = true;
        }


        if (dirty) {
            updateOldStringList();
        }
    }

    // GENERIC
    @Hotkey(hotkey = "C")
    @Config(category = ConfigCategory.GENERIC)
    public static ConfigHotkey clearWaypoint;

    @Config(category = ConfigCategory.GENERIC)
    public static boolean debug = false;

    @Config(category = ConfigCategory.GENERIC)
    public static boolean dontClearChatHistory = false;

    @Hotkey
    @Config(category = ConfigCategory.GENERIC)
    public static boolean forceParseWaypointFromChat = false;

    @Hotkey(hotkey = "O,C")
    @Config(category = ConfigCategory.GENERIC)
    public static ConfigHotkey openConfigGui;

    @Hotkey
    @Config(category = ConfigCategory.GENERIC)
    public static boolean parseWaypointFromChat = true;

    @Hotkey(hotkey = "O,P")
    @Config(category = ConfigCategory.GENERIC)
    public static ConfigHotkey sendLookingAtBlockPos;

    @Hotkey
    @Config(category = ConfigCategory.GENERIC)
    public static boolean sortInventorySupportEmptyShulkerBoxStack = false;

    @Hotkey(hotkey = "R")
    @Config(category = ConfigCategory.GENERIC)
    public static ConfigHotkey sortInventory;

    @Config(category = ConfigCategory.GENERIC)
    public static IConfigOptionListEntry sortInventoryShulkerBoxLast = SortInventoryShulkerBoxLastType.AUTO;

    // FEATURE_TOGGLE

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean autoSwitchElytra = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean betterSneaking = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE, dependencies = @Dependencies(and = @Dependency(value = "minecraft", versionPredicate = ">1.15.2")))
    public static boolean disableBlocklistCheck = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean disableBreakBlock = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean disableBreakScaffolding = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean disableMoveDownInScaffolding = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean disablePistonPushEntity = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean flatDigger = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean forceBreakingCooldown = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean highlightLavaSource = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean highlightPersistentMob = false;

    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean highlightPersistentMobClientMode = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean preventWastageOfWater = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean preventIntentionalGameDesign = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean realSneaking = false;

    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean removeBreakingCooldown = false;


    @Hotkey
    @Config(category = ConfigCategory.FEATURE_TOGGLE)
    public static boolean worldEaterMineHelper = false;


    // LISTS

    @Config(category = ConfigCategory.LISTS)
    public static ArrayList<String> blockModelNoOffsetBlacklist = new ArrayList<>();

    @Config(category = ConfigCategory.LISTS)
    public static IConfigOptionListEntry blockModelNoOffsetListType = UsageRestriction.ListType.WHITELIST;

    @Config(category = ConfigCategory.LISTS)
    public static ArrayList<String> blockModelNoOffsetWhitelist = Lists.newArrayList("minecraft:wither_rose",
            "minecraft:poppy", "minecraft:dandelion");

    @Config(category = ConfigCategory.LISTS)
    public static ArrayList<String> breakBlockBlackList = Lists.newArrayList("minecraft:budding_amethyst", "_bud");

    @Config(category = ConfigCategory.LISTS)
    public static ArrayList<String> breakScaffoldingWhiteList = Lists.newArrayList("minecraft:air", "minecraft:scaffolding");

    @Config(category = ConfigCategory.LISTS)
    public static ArrayList<String> highlightEntityBlackList = new ArrayList<>();

    @Config(category = ConfigCategory.LISTS)
    public static IConfigOptionListEntry highlightEntityListType = UsageRestriction.ListType.WHITELIST;

    @Config(category = ConfigCategory.LISTS)
    public static ArrayList<String> highlightEntityWhiteList = Lists.newArrayList("minecraft:wandering_trader");

    @Config(category = ConfigCategory.LISTS)
    public static ArrayList<String> moveDownInScaffoldingWhiteList = Lists.newArrayList("minecraft:air", "minecraft:scaffolding");

    @Config(category = ConfigCategory.LISTS)
    public static ArrayList<String> worldEaterMineHelperWhitelist = Lists.newArrayList("_ore", "minecraft:ancient_debris", "minecraft:obsidian");

    // ADVANCED_INTEGRATED_SERVER

    @Hotkey
    @Config(category = ConfigCategory.ADVANCED_INTEGRATED_SERVER, dependencies = @Dependencies(predicate = SinglePlayerServerOptionPredicate.class))
    public static boolean onlineMode = true;

    @Config(category = ConfigCategory.ADVANCED_INTEGRATED_SERVER, dependencies = @Dependencies(predicate = SinglePlayerServerOptionPredicate.class))
    public static boolean pvp = true;

    @Config(category = ConfigCategory.ADVANCED_INTEGRATED_SERVER, dependencies = @Dependencies(predicate = SinglePlayerServerOptionPredicate.class))
    public static boolean flight = true;

    @Numeric(minValue = 0, maxValue = 65535)
    @Config(category = ConfigCategory.ADVANCED_INTEGRATED_SERVER, dependencies = @Dependencies(predicate = SinglePlayerServerOptionPredicate.class))
    public static int port = 0;

    private static boolean first = true;

    public static void postDeserialize(ConfigHandler configHandler) {
        if (first) {
            if (debug) {
                Configurator.setLevel(ModInfo.MOD_ID, Level.toLevel("DEBUG"));
            }
            updateOldStringList();
            first = false;
        }
        checkIsStringListChanged();
    }


    public static void init(ConfigManager cm) {

        // GENERIC
        cm.setValueChangeCallback("debug", option -> {
            if (debug) {
                Configurator.setLevel(ModInfo.MOD_ID, Level.toLevel("DEBUG"));
            } else {
                Configurator.setLevel(ModInfo.MOD_ID, Level.toLevel("INFO"));
            }
            GuiConfigs.getInstance().reDraw();
        });

        clearWaypoint.getKeybind().setCallback((keyAction, iKeybind) -> {
            HighlightWaypointUtil.highlightPos = null;
            HighlightWaypointUtil.lastBeamTime = 0;
            return false;
        });

        openConfigGui.getKeybind().setCallback((keyAction, iKeybind) -> {
            GuiConfigs screen = GuiConfigs.getInstance();
            screen.setParentGui(Minecraft.getInstance().screen);
            Minecraft.getInstance().setScreen(screen);
            return true;
        });

        sendLookingAtBlockPos.getKeybind().setCallback((keyAction, iKeybind) -> {
            Minecraft client = Minecraft.getInstance();
            Entity cameraEntity = client.getCameraEntity();
            MultiPlayerGameMode clientPlayerInteractionManager = client.gameMode;
            if (cameraEntity != null && clientPlayerInteractionManager != null) {
                HitResult hitresult = cameraEntity.pick(clientPlayerInteractionManager.getPickRange(), client.getFrameTime(), false);
                if (hitresult.getType() == HitResult.Type.BLOCK) {
                    BlockPos lookPos = ((BlockHitResult) hitresult).getBlockPos();
                    if (client.player != null) {
                        String message = String.format("[%d, %d, %d]", lookPos.getX(), lookPos.getY(), lookPos.getZ());
                        //#if MC > 11802
                        ClientChatPreview chatPreview = new ClientChatPreview(Minecraft.getInstance());
                        Component component = Util.mapNullable(chatPreview.pull(message), ClientChatPreview.Preview::response);
                        client.player.chatSigned(message, component);
                        //#else
                        //$$ client.player.chat(message);
                        //#endif
                    }
                }
            }
            return false;
        });

        sortInventory.getKeybind().setSettings(KeybindSettings.GUI);

        sortInventory.getKeybind().setCallback((keyAction, iKeybind) -> {
            if (SortInventoryUtil.sort()) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            } else {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.DISPENSER_FAIL, 1.0F));
            }
            return false;
        });

        // FEATURE_TOGGLE
        cm.setValueChangeCallback("highlightLavaSource", option -> {
            ModInfo.LOGGER.debug("set highlightLavaSource {}", ((ConfigBoolean) option.getConfig()).getBooleanValue());
            Minecraft.getInstance().levelRenderer.allChanged();
        });
        cm.setValueChangeCallback("worldEaterMineHelper", option -> {
            ModInfo.LOGGER.debug("set worldEaterMineHelper {}", ((ConfigBoolean) option.getConfig()).getBooleanValue());
            Minecraft.getInstance().levelRenderer.allChanged();
        });

        // LISTS
        cm.setValueChangeCallback("blockModelNoOffsetListType",
                option -> Minecraft.getInstance().levelRenderer.allChanged());

        // ADVANCED_INTEGRATED_SERVER
        cm.setValueChangeCallback("onlineMode", option -> {
            ModInfo.LOGGER.debug("set onlineMode {}", ((ConfigBoolean) option.getConfig()).getBooleanValue());
            if (Minecraft.getInstance().hasSingleplayerServer()) {
                Objects.requireNonNull(Minecraft.getInstance().getSingleplayerServer()).setUsesAuthentication(onlineMode);
            }
        });

        cm.setValueChangeCallback("pvp", option -> {
            ModInfo.LOGGER.debug("set pvp {}", ((ConfigBoolean) option.getConfig()).getBooleanValue());
            if (Minecraft.getInstance().hasSingleplayerServer()) {
                Objects.requireNonNull(Minecraft.getInstance().getSingleplayerServer()).setPvpAllowed(pvp);
            }
        });
        cm.setValueChangeCallback("flight", option -> {
            ModInfo.LOGGER.debug("set flight {}", ((ConfigBoolean) option.getConfig()).getBooleanValue());
            if (Minecraft.getInstance().hasSingleplayerServer()) {
                Objects.requireNonNull(Minecraft.getInstance().getSingleplayerServer()).setFlightAllowed(flight);
            }
        });
    }

    public enum SortInventoryShulkerBoxLastType implements IConfigOptionListEntry {
        FALSE("false", ModInfo.MOD_ID + ".gui.label.sort_inventory_shulker_box_last_type.false"),
        TRUE("true", ModInfo.MOD_ID + ".gui.label.sort_inventory_shulker_box_last_type.true"),
        AUTO("auto", ModInfo.MOD_ID + ".gui.label.sort_inventory_shulker_box_last_type.auto");
        private final String configString;
        private final String translationKey;

        SortInventoryShulkerBoxLastType(String configString, String translationKey) {
            this.configString = configString;
            this.translationKey = translationKey;
        }

        @Override
        public String getStringValue() {
            return this.configString;
        }

        @Override
        public String getDisplayName() {
            return I18n.get(this.translationKey);
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward) {
            int id = this.ordinal();
            if (forward) {
                ++id;
                if (id >= values().length) {
                    id = 0;
                }
            } else {
                --id;
                if (id < 0) {
                    id = values().length - 1;
                }
            }

            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String name) {
            SortInventoryShulkerBoxLastType[] values = values();
            for (SortInventoryShulkerBoxLastType mode : values) {
                if (mode.configString.equalsIgnoreCase(name)) {
                    return mode;
                }
            }
            return AUTO;
        }
    }

    public static class ConfigCategory {
        public static final String GENERIC = "generic";
        public static final String FEATURE_TOGGLE = "feature_toggle";
        public static final String LISTS = "lists";
        public static final String ADVANCED_INTEGRATED_SERVER = "advanced_integrated_server";
    }

    public static class SinglePlayerServerOptionPredicate implements OptionDependencyPredicate {
        @Override
        public boolean test(Option option) {
            return Minecraft.getInstance().hasSingleplayerServer();
        }
    }
}