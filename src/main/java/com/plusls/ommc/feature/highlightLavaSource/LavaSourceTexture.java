package com.plusls.ommc.feature.highlightLavaSource;

import com.plusls.ommc.ModInfo;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class LavaSourceTexture {
    public static Sprite lavaSourceFlowSprite;
    public static Sprite lavaSourceStillSprite;

    public static void initLavaSourceFlowSprite() {
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
