package com.plusls.ommc.feature.highlithtWaypoint;

import com.plusls.ommc.ModInfo;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import java.util.function.Function;

public class HighlightWaypointResourceLoader implements SimpleSynchronousResourceReloadListener {
    private static final ResourceLocation listenerId = ModInfo.id("target_reload_listener");
    private static final ResourceLocation targetId = ModInfo.id("images/target");
    public static TextureAtlasSprite targetIdSprite;

    public static void init() {
        ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register(
                (atlasTexture, registry) -> registry.register(targetId)
        );
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new HighlightWaypointResourceLoader());
    }

    @Override
    public ResourceLocation getFabricId() {
        return listenerId;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        //#if MC > 11404
        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        targetIdSprite = atlas.apply(targetId);
        //#else
        //$$ targetIdSprite = Minecraft.getInstance().getTextureAtlas().getSprite(targetId);
        //#endif
    }
}
