package com.plusls.ommc.feature.highlithtWaypoint;

import com.plusls.ommc.ModInfo;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.container.PlayerContainer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class HighlightWaypointResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static Sprite targetIdSprite;

    private static final Identifier listenerId = ModInfo.id("target_reload_listener");
    private static final Identifier targetId = ModInfo.id("images/target");

    public static void init() {
        ClientSpriteRegistryCallback.event(PlayerContainer.BLOCK_ATLAS_TEXTURE).register(
                (atlasTexture, registry) -> registry.register(targetId)
        );
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new HighlightWaypointResourceLoader());
    }

    @Override
    public Identifier getFabricId() {
        return listenerId;
    }

    @Override
    public void apply(ResourceManager manager) {
        final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerContainer.BLOCK_ATLAS_TEXTURE);
        targetIdSprite = atlas.apply(targetId);
    }
}
