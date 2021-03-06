package com.plusls.ommc.mixin.sodium;

import com.plusls.ommc.OhMyMinecraftClient;
import com.plusls.ommc.config.Configs;
import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.model.quad.blender.BiomeColorBlender;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FluidRenderer.class, remap = false)
public class MixinFluidRenderer {
    @Shadow
    @Final
    private Sprite[] lavaSprites;
    final private Sprite[] ommc_backupLavaSprites = new Sprite[2];

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void backupSprite(MinecraftClient client, LightPipelineProvider lighters, BiomeColorBlender biomeColorBlender, CallbackInfo ci) {
        ommc_backupLavaSprites[0] = lavaSprites[0];
        ommc_backupLavaSprites[1] = lavaSprites[1];
    }

    // @Coerce Object
    // stable -> ModelQuadSinkDelegate consumer
    // next -> ChunkModelBuffers buffers

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void modifyLavaSprites(BlockRenderView view, FluidState state, BlockPos pos, @Coerce Object object, CallbackInfoReturnable<Boolean> info) {
        if (Configs.FeatureToggle.HIGHLIGHT_LAVA_SOURCE.getBooleanValue() && state.isIn(FluidTags.LAVA) &&
                view.getBlockState(pos).get(FluidBlock.LEVEL) == 0) {
            lavaSprites[0] = OhMyMinecraftClient.lavaSourceStillSprite;
            lavaSprites[1] = OhMyMinecraftClient.lavaSourceFlowSprite;
        }
    }

    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    public void restoreLavaSprites(BlockRenderView view, FluidState state, BlockPos pos, @Coerce Object object, CallbackInfoReturnable<Boolean> info) {
        lavaSprites[0] = ommc_backupLavaSprites[0];
        lavaSprites[1] = ommc_backupLavaSprites[1];
    }

//    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
//    public void modifyLavaSprites(BlockRenderView view, FluidState state, BlockPos pos, ModelQuadSinkDelegate consumer, CallbackInfoReturnable<Boolean> info) {
//        if (OmmcConfig.highlightLava && state.isIn(FluidTags.LAVA) &&
//                view.getBlockState(pos).get(FluidBlock.LEVEL) == 0) {
//            lavaSprites[0] = OhMyMinecraftClient.lavaSourceStillSprite;
//            lavaSprites[1] = OhMyMinecraftClient.lavaSourceFlowSprite;
//        }
//    }
//
//    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
//    public void restoreLavaSprites(BlockRenderView view, FluidState state, BlockPos pos, ModelQuadSinkDelegate consumer, CallbackInfoReturnable<Boolean> info) {
//        lavaSprites[0] = ommc_backupLavaSprites[0];
//        lavaSprites[1] = ommc_backupLavaSprites[1];
//    }
}
