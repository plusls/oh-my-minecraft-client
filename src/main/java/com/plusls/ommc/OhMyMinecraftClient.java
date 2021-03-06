package com.plusls.ommc;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.event.InputHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.fabricmc.api.ClientModInitializer;
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
import org.apache.logging.log4j.core.config.Configurator;

import java.util.function.Function;

public class OhMyMinecraftClient implements ClientModInitializer {
    public static Sprite lavaSourceFlowSprite;
    public static Sprite lavaSourceStillSprite;


    @Override
    public void onInitializeClient() {
        Configurator.setLevel(ModInfo.LOGGER.getName(), Level.toLevel("DEBUG"));
        initLavaSourceFlowSprite();
        ConfigManager.getInstance().registerConfigHandler(ModInfo.MOD_ID, new Configs());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
    }

    private static void initLavaSourceFlowSprite() {
        final Identifier flowingSpriteId = new Identifier(ModInfo.MOD_ID, "block/lava_flow");
        final Identifier stillSpriteId = new Identifier(ModInfo.MOD_ID, "block/lava_still");

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

