package com.plusls.ommc;

import com.plusls.ommc.command.SwitchHighlightLavaCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.function.Function;

public class OhMyMinecraftClient implements ClientModInitializer {
    public static String MOD_ID = "ommc";
    public static Sprite lavaSourceFlowSprite;
    public static Sprite lavaSourceStillSprite;
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static String level = "INFO";

    @Override
    public void onInitializeClient() {
        Configurator.setLevel(LOGGER.getName(), Level.toLevel(OhMyMinecraftClient.level));
        initLavaSourceFlowSprite();
        ClientTickEvents.END_CLIENT_TICK.register(SwitchHighlightLavaCommand::updateHighlightLava);
        SwitchHighlightLavaCommand.register();
    }

    private static void initLavaSourceFlowSprite() {
        final Identifier flowingSpriteId = new Identifier(MOD_ID, "block/lava_flow");
        final Identifier stillSpriteId = new Identifier(MOD_ID, "block/lava_still");

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) ->
        {
            registry.register(flowingSpriteId);
            registry.register(stillSpriteId);
        });

        final Identifier listenerId = new Identifier("lava_reload_listener");


        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return listenerId;
            }

            @Override
            public void apply(ResourceManager resourceManager) {
                final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                lavaSourceFlowSprite = atlas.apply(flowingSpriteId);
                lavaSourceStillSprite = atlas.apply(stillSpriteId);
            }
        });
    }
}

