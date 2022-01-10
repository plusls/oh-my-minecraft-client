package com.plusls.ommc.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.plusls.ommc.ModInfo;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import com.plusls.ommc.feature.sortInventory.SortInventoryUtil;
import com.plusls.ommc.gui.GuiConfigs;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = ModInfo.MOD_ID + ".json";
    private static final int CONFIG_VERSION = 1;
    private static final List<String> OLD_WORLD_EATER_MINE_HELPER_WHITELIST = new ArrayList<>();
    private static final List<String> OLD_FALLBACK_LANGUAGE_LIST = new ArrayList<>();
    private static final List<String> OLD_BLOCK_MODEL_NO_OFFSET_BLACKLIST = new ArrayList<>();
    private static final List<String> OLD_BLOCK_MODEL_NO_OFFSET_WHITELIST = new ArrayList<>();

    private static boolean firstLoadConfig = true;

    public static void updateOldStringList() {
        OLD_WORLD_EATER_MINE_HELPER_WHITELIST.clear();
        OLD_WORLD_EATER_MINE_HELPER_WHITELIST.addAll(Lists.WORLD_EATER_MINE_HELPER_WHITELIST.getStrings());
        OLD_FALLBACK_LANGUAGE_LIST.clear();
        OLD_FALLBACK_LANGUAGE_LIST.addAll(Lists.FALLBACK_LANGUAGE_LIST.getStrings());
        OLD_BLOCK_MODEL_NO_OFFSET_BLACKLIST.clear();
        OLD_BLOCK_MODEL_NO_OFFSET_BLACKLIST.addAll(Lists.BLOCK_MODEL_NO_OFFSET_BLACKLIST.getStrings());
        OLD_BLOCK_MODEL_NO_OFFSET_WHITELIST.clear();
        OLD_BLOCK_MODEL_NO_OFFSET_WHITELIST.addAll(Lists.BLOCK_MODEL_NO_OFFSET_WHITELIST.getStrings());

    }

    public static void checkIsStringListChanged() {
        boolean dirty = false;
        if (!OLD_WORLD_EATER_MINE_HELPER_WHITELIST.equals(Lists.WORLD_EATER_MINE_HELPER_WHITELIST.getStrings()) ||
                !OLD_BLOCK_MODEL_NO_OFFSET_BLACKLIST.equals(Lists.BLOCK_MODEL_NO_OFFSET_BLACKLIST.getStrings()) ||
                !OLD_BLOCK_MODEL_NO_OFFSET_WHITELIST.equals(Lists.BLOCK_MODEL_NO_OFFSET_WHITELIST.getStrings())) {
            MinecraftClient.getInstance().worldRenderer.reload();
            dirty = true;
        }

        if (!OLD_FALLBACK_LANGUAGE_LIST.equals(Lists.FALLBACK_LANGUAGE_LIST.getStrings())) {
            MinecraftClient.getInstance().reloadResources();
            dirty = true;
        }

        if (dirty) {
            updateOldStringList();
        }
    }

    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
                KeybindSettings keybindSettings = Generic.SORT_INVENTORY.getKeybind().getSettings();
                if (keybindSettings.getContext() != KeybindSettings.Context.GUI) {
                    Generic.SORT_INVENTORY.getKeybind().setSettings(KeybindSettings.create(KeybindSettings.Context.GUI,
                            keybindSettings.getActivateOn(), keybindSettings.getAllowExtraKeys(),
                            keybindSettings.isOrderSensitive(), keybindSettings.isExclusive(),
                            keybindSettings.shouldCancel(), keybindSettings.getAllowEmpty()));
                }
                ConfigUtils.readHotkeyToggleOptions(root, "FeatureHotkey", "FeatureToggle", FeatureToggle.OPTIONS);
                ConfigUtils.readConfigBase(root, "Lists", Lists.OPTIONS);
                ConfigUtils.readConfigBase(root, "AdvancedIntegratedServer", AdvancedIntegratedServer.OPTIONS);
                int version = JsonUtils.getIntegerOrDefault(root, "config_version", 0);
            }
        }
        if (Generic.DEBUG.getBooleanValue()) {
            Configurator.setLevel(ModInfo.LOGGER.getName(), Level.toLevel("DEBUG"));
        }
        if (firstLoadConfig) {
            updateOldStringList();
        }
        firstLoadConfig = false;
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();
            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
            ConfigUtils.writeHotkeyToggleOptions(root, "FeatureHotkey", "FeatureToggle", FeatureToggle.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Lists", Lists.OPTIONS);
            ConfigUtils.writeConfigBase(root, "AdvancedIntegratedServer", AdvancedIntegratedServer.OPTIONS);
            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));
            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }

    public static class Generic {
        private static final String PREFIX = String.format("%s.config.generic", ModInfo.MOD_ID);
        public static final ConfigHotkey OPEN_CONFIG_GUI = new TranslatableConfigHotkey(PREFIX, "openConfigGui", "O,C");
        public static final ConfigBoolean DEBUG = new TranslatableConfigBoolean(PREFIX, "debug", false);
        public static final ConfigBoolean DONT_CLEAR_CHAT_HISTORY = new TranslatableConfigBoolean(PREFIX, "dontClearChatHistory", false);
        public static final ConfigHotkey CLEAR_WAYPOINT = new TranslatableConfigHotkey(PREFIX, "clearWaypoint", "C");
        public static final ConfigBoolean PARSE_WAYPOINT_FROM_CHAT = new TranslatableConfigBoolean(PREFIX, "parseWaypointFromChat", true);
        public static final ConfigHotkey SEND_LOOKING_AT_BLOCK_POS = new TranslatableConfigHotkey(PREFIX, "sendLookingAtBlockPos", "O,P");
        public static final ConfigHotkey SORT_INVENTORY = new TranslatableConfigHotkey(PREFIX, "sortInventory", "R", KeybindSettings.GUI);
        public static final ImmutableList<ConfigHotkey> HOTKEYS = ImmutableList.of(
                OPEN_CONFIG_GUI,
                CLEAR_WAYPOINT,
                SEND_LOOKING_AT_BLOCK_POS,
                SORT_INVENTORY
        );
        public static final ConfigBoolean SORT_INVENTORY_SHULKER_BOX_LAST = new TranslatableConfigBoolean(PREFIX, "sortInventoryShulkerBoxLast", false);
        public static final ConfigBoolean SORT_INVENTORY_SUPPORT_EMPTY_SHULKER_BOX_STACK = new TranslatableConfigBoolean(PREFIX, "sortInventorySupportEmptyShulkerBoxStack", false);
        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                OPEN_CONFIG_GUI,
                DEBUG,
                DONT_CLEAR_CHAT_HISTORY,
                CLEAR_WAYPOINT,
                PARSE_WAYPOINT_FROM_CHAT,
                SEND_LOOKING_AT_BLOCK_POS,
                SORT_INVENTORY,
                SORT_INVENTORY_SHULKER_BOX_LAST,
                SORT_INVENTORY_SUPPORT_EMPTY_SHULKER_BOX_STACK
        );

        static {
            OPEN_CONFIG_GUI.getKeybind().setCallback((keyAction, iKeybind) -> {
                GuiBase.openGui(new GuiConfigs());
                return true;
            });
            SEND_LOOKING_AT_BLOCK_POS.getKeybind().setCallback((keyAction, iKeybind) -> {
                MinecraftClient client = MinecraftClient.getInstance();
                Entity cameraEntity = client.getCameraEntity();
                ClientPlayerInteractionManager clientPlayerInteractionManager = client.interactionManager;
                if (cameraEntity != null && clientPlayerInteractionManager != null) {
                    HitResult hitresult = cameraEntity.raycast(clientPlayerInteractionManager.getReachDistance(), client.getTickDelta(), false);
                    if (hitresult.getType() == HitResult.Type.BLOCK) {
                        BlockPos lookPos = ((BlockHitResult) hitresult).getBlockPos();
                        if (client.player != null) {
                            client.player.sendChatMessage(String.format("[%d, %d, %d]", lookPos.getX(), lookPos.getY(), lookPos.getZ()));
                        }
                    }
                }
                return false;
            });

            CLEAR_WAYPOINT.getKeybind().setCallback((keyAction, iKeybind) -> {
                HighlightWaypointUtil.highlightPos = null;
                HighlightWaypointUtil.lastBeamTime = 0;
                return false;
            });
            SORT_INVENTORY.getKeybind().setCallback((keyAction, iKeybind) -> {
                SortInventoryUtil.sort();
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return false;
            });
            DEBUG.setValueChangeCallback(config -> {
                if (config.getBooleanValue()) {
                    Configurator.setLevel(ModInfo.LOGGER.getName(), Level.toLevel("DEBUG"));
                } else {
                    Configurator.setLevel(ModInfo.LOGGER.getName(), Level.toLevel("INFO"));
                }
            });
        }
    }

    public static class FeatureToggle {
        private static final String PREFIX = String.format("%s.config.feature_toggle", ModInfo.MOD_ID);
        public static final ConfigBooleanHotkeyed AUTO_SWITCH_ELYTRA = new TranslatableConfigBooleanHotkeyed(PREFIX, "autoSwitchElytra", false, "");
        public static final ConfigBooleanHotkeyed BETTER_SNEAKING = new TranslatableConfigBooleanHotkeyed(PREFIX, "betterSneaking", false, "");
        public static final ConfigBooleanHotkeyed BLOCK_MODEL_NO_OFFSET = new TranslatableConfigBooleanHotkeyed(PREFIX, "blockModelNoOffset", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_BLOCKLIST_CHECK = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableBlocklistCheck", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_BREAK_BLOCK = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableBreakBlock", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_BREAK_SCAFFOLDING = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableBreakScaffolding", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_MOVE_DOWN_IN_SCAFFOLDING = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableMoveDownInScaffolding", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_PISTON_PUSH_ENTITY = new TranslatableConfigBooleanHotkeyed(PREFIX, "disablePistonPushEntity", false, "");
        public static final ConfigBooleanHotkeyed FLAT_DIGGER = new TranslatableConfigBooleanHotkeyed(PREFIX, "flatDigger", false, "");
        public static final ConfigBooleanHotkeyed FORCE_BREAKING_COOLDOWN = new TranslatableConfigBooleanHotkeyed(PREFIX, "forceBreakingCooldown", false, "");
        public static final ConfigBooleanHotkeyed HIGHLIGHT_LAVA_SOURCE = new TranslatableConfigBooleanHotkeyed(PREFIX, "highlightLavaSource", false, "");
        public static final ConfigBooleanHotkeyed HIGHLIGHT_WANDERING_TRADER = new TranslatableConfigBooleanHotkeyed(PREFIX, "highlightWanderingTrader", false, "");
        public static final ConfigBooleanHotkeyed HIGHLIGHT_PERSISTENT_MOB = new TranslatableConfigBooleanHotkeyed(PREFIX, "highlightPersistentMob", false, "");
        public static final ConfigBooleanHotkeyed PREVENT_INTENTIONAL_GAME_DESIGN = new TranslatableConfigBooleanHotkeyed(PREFIX, "preventIntentionalGameDesign", false, "");
        public static final ConfigBooleanHotkeyed PREVENT_WASTAGE_OF_WATER = new TranslatableConfigBooleanHotkeyed(PREFIX, "preventWastageOfWater", false, "");
        public static final ConfigBooleanHotkeyed REAL_SNEAKING = new TranslatableConfigBooleanHotkeyed(PREFIX, "realSneaking", false, "");
        public static final ConfigBooleanHotkeyed REMOVE_BREAKING_COOLDOWN = new TranslatableConfigBooleanHotkeyed(PREFIX, "removeBreakingCooldown", false, "");
        public static final ConfigBooleanHotkeyed WORLD_EATER_MINE_HELPER = new TranslatableConfigBooleanHotkeyed(PREFIX, "worldEaterMineHelper", false, "");

        public static final ImmutableList<ConfigBooleanHotkeyed> OPTIONS = ImmutableList.of(
                AUTO_SWITCH_ELYTRA,
                BETTER_SNEAKING,
                BLOCK_MODEL_NO_OFFSET,
                DISABLE_BLOCKLIST_CHECK,
                DISABLE_BREAK_BLOCK,
                DISABLE_BREAK_SCAFFOLDING,
                DISABLE_MOVE_DOWN_IN_SCAFFOLDING,
                DISABLE_PISTON_PUSH_ENTITY,
                FLAT_DIGGER,
                FORCE_BREAKING_COOLDOWN,
                HIGHLIGHT_LAVA_SOURCE,
                HIGHLIGHT_WANDERING_TRADER,
                HIGHLIGHT_PERSISTENT_MOB,
                PREVENT_INTENTIONAL_GAME_DESIGN,
                PREVENT_WASTAGE_OF_WATER,
                REAL_SNEAKING,
                REMOVE_BREAKING_COOLDOWN,
                WORLD_EATER_MINE_HELPER
        );

        static {
            BLOCK_MODEL_NO_OFFSET.setValueChangeCallback(config -> MinecraftClient.getInstance().worldRenderer.reload());
            HIGHLIGHT_LAVA_SOURCE.setValueChangeCallback(config -> {
                ModInfo.LOGGER.debug("set HIGHLIGHT_LAVA_SOURCE {}", config.getBooleanValue());
                MinecraftClient.getInstance().worldRenderer.reload();
            });
            WORLD_EATER_MINE_HELPER.setValueChangeCallback(config -> {
                ModInfo.LOGGER.debug("set WORLD_EATER_MINE_HELPER {}", config.getBooleanValue());
                MinecraftClient.getInstance().worldRenderer.reload();
            });
        }
    }

    public static class Lists {
        private static final String PREFIX = String.format("%s.config.lists", ModInfo.MOD_ID);
        public static final ConfigOptionList BLOCK_MODEL_NO_OFFSET_LIST_TYPE = new TranslatableConfigOptionList(PREFIX, "blockModelNoOffsetListType", UsageRestriction.ListType.WHITELIST);
        public static final ConfigStringList BLOCK_MODEL_NO_OFFSET_BLACKLIST = new TranslatableConfigStringList(PREFIX, "blockModelNoOffsetBlacklist", ImmutableList.of());
        public static final ConfigStringList BLOCK_MODEL_NO_OFFSET_WHITELIST = new TranslatableConfigStringList(PREFIX, "blockModelNoOffsetWhitelist", ImmutableList.of("minecraft:wither_rose", "minecraft:poppy", "minecraft:dandelion"));
        public static final ConfigStringList BREAK_BLOCK_BLACKLIST = new TranslatableConfigStringList(PREFIX,
                "breakBlockBlackList", ImmutableList.of("minecraft:budding_amethyst", "_bud"));
        public static final ConfigStringList BREAK_SCAFFOLDING_WHITELIST = new TranslatableConfigStringList(PREFIX,
                "breakScaffoldingWhiteList", ImmutableList.of("minecraft:air", "minecraft:scaffolding"));
        public static final ConfigStringList FALLBACK_LANGUAGE_LIST = new TranslatableConfigStringList(PREFIX,
                "fallbackLanguageList", ImmutableList.of("zh_cn"));
        public static final ConfigStringList MOVE_DOWN_IN_SCAFFOLDING_WHITELIST = new TranslatableConfigStringList(PREFIX,
                "moveDownInScaffoldingWhiteList", ImmutableList.of("minecraft:air", "minecraft:scaffolding"));
        public static final ConfigStringList WORLD_EATER_MINE_HELPER_WHITELIST = new TranslatableConfigStringList(PREFIX,
                "worldEaterMineHelperWhitelist", ImmutableList.of("_ore", "minecraft:ancient_debris", "minecraft:obsidian"));
        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                BLOCK_MODEL_NO_OFFSET_LIST_TYPE,
                BLOCK_MODEL_NO_OFFSET_BLACKLIST,
                BLOCK_MODEL_NO_OFFSET_WHITELIST,
                BREAK_BLOCK_BLACKLIST,
                BREAK_SCAFFOLDING_WHITELIST,
                FALLBACK_LANGUAGE_LIST,
                MOVE_DOWN_IN_SCAFFOLDING_WHITELIST,
                WORLD_EATER_MINE_HELPER_WHITELIST
        );

        static {
            BLOCK_MODEL_NO_OFFSET_LIST_TYPE.setValueChangeCallback(config -> MinecraftClient.getInstance().worldRenderer.reload());
        }

    }

    public static class AdvancedIntegratedServer {
        private static final String PREFIX = String.format("%s.config.advanced_integrated_server", ModInfo.MOD_ID);
        public static final ConfigBoolean ONLINE_MODE = new TranslatableConfigBoolean(PREFIX, "onlineMode", true);
        public static final ConfigBoolean PVP = new TranslatableConfigBoolean(PREFIX, "pvp", true);
        public static final ConfigBoolean FLIGHT = new TranslatableConfigBoolean(PREFIX, "flight", true);
        public static final ConfigInteger PORT = new TranslatableConfigInteger(PREFIX, "port", 0, 0, 65535);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ONLINE_MODE,
                PVP,
                FLIGHT,
                PORT
        );

        static {
            ONLINE_MODE.setValueChangeCallback(config -> {
                ModInfo.LOGGER.debug("set ONLINE_MODE {}", config.getBooleanValue());
                if (MinecraftClient.getInstance().isIntegratedServerRunning()) {
                    Objects.requireNonNull(MinecraftClient.getInstance().getServer()).setOnlineMode(ONLINE_MODE.getBooleanValue());
                }
            });
            PVP.setValueChangeCallback(config -> {
                ModInfo.LOGGER.debug("set PVP {}", config.getBooleanValue());
                if (MinecraftClient.getInstance().isIntegratedServerRunning()) {
                    Objects.requireNonNull(MinecraftClient.getInstance().getServer()).setPvpEnabled(PVP.getBooleanValue());
                }
            });
            FLIGHT.setValueChangeCallback(config -> {
                ModInfo.LOGGER.debug("set FLIGHT {}", config.getBooleanValue());
                if (MinecraftClient.getInstance().isIntegratedServerRunning()) {
                    Objects.requireNonNull(MinecraftClient.getInstance().getServer()).setFlightEnabled(PVP.getBooleanValue());
                }
            });
        }

    }
}