package com.plusls.ommc.feature.highlithtWaypoint;

import com.plusls.ommc.OhMyMinecraftClientReference;
//#if MC < 11903
//$$ import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
//$$ import net.minecraft.client.renderer.texture.TextureAtlas;
//#endif
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import java.util.function.Function;

public class HighlightWaypointResourceLoader implements SimpleSynchronousResourceReloadListener {
    private static final ResourceLocation listenerId = OhMyMinecraftClientReference.identifier("target_reload_listener");
    private static final ResourceLocation targetId = OhMyMinecraftClientReference.identifier("block/target");
    public static TextureAtlasSprite targetIdSprite;

    public static void init() {
        //#if MC < 11903
        //$$ ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register(
        //$$         (atlasTexture, registry) -> registry.register(targetId)
        //$$ );
        //#endif
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
