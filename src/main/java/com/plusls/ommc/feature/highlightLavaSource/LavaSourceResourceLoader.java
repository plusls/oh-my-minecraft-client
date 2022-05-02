package com.plusls.ommc.feature.highlightLavaSource;

import com.plusls.ommc.ModInfo;
import com.plusls.ommc.config.Configs;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import java.util.function.Function;

public class LavaSourceResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final TextureAtlasSprite[] lavaSourceSpites = new TextureAtlasSprite[2];
    public static final TextureAtlasSprite[] defaultLavaSourceSpites = new TextureAtlasSprite[2];
    private static final ResourceLocation listenerId = ModInfo.id("lava_reload_listener");
    private static final ResourceLocation flowingSpriteId = ModInfo.id("block/lava_flow");
    private static final ResourceLocation stillSpriteId = ModInfo.id("block/lava_still");
    public static TextureAtlasSprite lavaSourceFlowSprite;
    public static TextureAtlasSprite lavaSourceStillSprite;
    public static TextureAtlasSprite defaultLavaSourceFlowSprite;
    public static TextureAtlasSprite defaultLavaSourceStillSprite;

    public static void init() {
        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS).register((atlasTexture, registry) ->
        {
            registry.register(flowingSpriteId);
            registry.register(stillSpriteId);
        });
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new LavaSourceResourceLoader());
    }

    @Override
    public ResourceLocation getFabricId() {
        return listenerId;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        lavaSourceStillSprite = atlas.apply(stillSpriteId);
        lavaSourceFlowSprite = atlas.apply(flowingSpriteId);
        lavaSourceSpites[0] = lavaSourceStillSprite;
        lavaSourceSpites[1] = lavaSourceFlowSprite;
        defaultLavaSourceStillSprite = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
        defaultLavaSourceFlowSprite = ModelBakery.LAVA_FLOW.sprite();
        defaultLavaSourceSpites[0] = defaultLavaSourceStillSprite;
        defaultLavaSourceSpites[1] = defaultLavaSourceFlowSprite;
        FluidRenderHandler lavaSourceRenderHandler = (view, pos, state) -> {

            if (view != null && pos != null && Configs.FeatureToggle.HIGHLIGHT_LAVA_SOURCE.getBooleanValue()) {
                BlockState blockState = view.getBlockState(pos);
                if (blockState.hasProperty(LiquidBlock.LEVEL) && blockState.getValue(LiquidBlock.LEVEL) == 0) {
                    return lavaSourceSpites;
                }
            }
            return defaultLavaSourceSpites;
        };
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.LAVA, lavaSourceRenderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.FLOWING_LAVA, lavaSourceRenderHandler);
    }
}
