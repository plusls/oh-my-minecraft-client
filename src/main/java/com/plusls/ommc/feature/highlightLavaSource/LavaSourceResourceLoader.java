package com.plusls.ommc.feature.highlightLavaSource;

import com.plusls.ommc.ModInfo;
import com.plusls.ommc.config.Configs;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class LavaSourceResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final Sprite[] lavaSourceSpites = new Sprite[2];
    public static final Sprite[] defaultLavaSourceSpites = new Sprite[2];
    private static final Identifier listenerId = ModInfo.id("lava_reload_listener");
    private static final Identifier flowingSpriteId = ModInfo.id("block/lava_flow");
    private static final Identifier stillSpriteId = ModInfo.id("block/lava_still");
    public static Sprite lavaSourceFlowSprite;
    public static Sprite lavaSourceStillSprite;
    public static Sprite defaultLavaSourceFlowSprite;
    public static Sprite defaultLavaSourceStillSprite;

    public static void init() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) ->
        {
            registry.register(flowingSpriteId);
            registry.register(stillSpriteId);
        });
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new LavaSourceResourceLoader());
    }

    @Override
    public Identifier getFabricId() {
        return listenerId;
    }

    @Override
    public void reload(ResourceManager manager) {
        final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        lavaSourceStillSprite = atlas.apply(stillSpriteId);
        lavaSourceFlowSprite = atlas.apply(flowingSpriteId);
        lavaSourceSpites[0] = lavaSourceStillSprite;
        lavaSourceSpites[1] = lavaSourceFlowSprite;
        defaultLavaSourceStillSprite = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getSprite();
        defaultLavaSourceFlowSprite = ModelLoader.LAVA_FLOW.getSprite();
        defaultLavaSourceSpites[0] = defaultLavaSourceStillSprite;
        defaultLavaSourceSpites[1] = defaultLavaSourceFlowSprite;
        FluidRenderHandler lavaSourceRenderHandler = new FluidRenderHandler() {
            @Override
            public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {

                if (view != null && pos != null && Configs.FeatureToggle.HIGHLIGHT_LAVA_SOURCE.getBooleanValue()) {
                    BlockState blockState = view.getBlockState(pos);
                    if (blockState.contains(FluidBlock.LEVEL) && blockState.get(FluidBlock.LEVEL) == 0) {
                        return lavaSourceSpites;
                    }
                }
                return defaultLavaSourceSpites;
            }
        };
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.LAVA, lavaSourceRenderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.FLOWING_LAVA, lavaSourceRenderHandler);
    }
}
