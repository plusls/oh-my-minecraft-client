package com.plusls.ommc.mixin.feature.highlightLavaSource.sodium;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceResourceLoader;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuffers;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FluidRenderer.class, remap = false)
public class MixinFluidRenderer {
    @Shadow
    @Final
    private Sprite[] lavaSprites;

    // @Coerce Object
    // stable -> ModelQuadSinkDelegate consumer
    // next -> ChunkModelBuffers buffers

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void modifyLavaSprites(BlockRenderView view, FluidState state, BlockPos pos, ChunkModelBuffers buffers, CallbackInfoReturnable<Boolean> info) {
        if (Configs.FeatureToggle.HIGHLIGHT_LAVA_SOURCE.getBooleanValue() && state.isIn(FluidTags.LAVA) &&
                view.getBlockState(pos).get(FluidBlock.LEVEL) == 0) {
            lavaSprites[0] = LavaSourceResourceLoader.lavaSourceStillSprite;
            lavaSprites[1] = LavaSourceResourceLoader.lavaSourceFlowSprite;
        }
    }

    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    public void restoreLavaSprites(BlockRenderView view, FluidState state, BlockPos pos, ChunkModelBuffers buffers, CallbackInfoReturnable<Boolean> info) {
        lavaSprites[0] = LavaSourceResourceLoader.defaultLavaSourceStillSprite;
        lavaSprites[1] = LavaSourceResourceLoader.defaultLavaSourceFlowSprite;
    }

}
