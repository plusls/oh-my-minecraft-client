package com.plusls.ommc.mixin.feature.highlightLavaSource;

import com.plusls.ommc.OhMyMinecraftClient;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceTexture;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidRenderer.class)
public class MixinFluidRenderer {

    @Shadow
    @Final
    private Sprite[] lavaSprites;
    final private Sprite[] ommc_backupLavaSprites = new Sprite[2];

    @Inject(method = "onResourceReload", at = @At(value = "RETURN"))
    private void backupSprite(CallbackInfo ci) {
        ommc_backupLavaSprites[0] = lavaSprites[0];
        ommc_backupLavaSprites[1] = lavaSprites[1];
    }


    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void modifyLavaSprites(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.FeatureToggle.HIGHLIGHT_LAVA_SOURCE.getBooleanValue() && state.isIn(FluidTags.LAVA) &&
                world.getBlockState(pos).get(FluidBlock.LEVEL) == 0) {
            lavaSprites[0] = LavaSourceTexture.lavaSourceStillSprite;
            lavaSprites[1] = LavaSourceTexture.lavaSourceFlowSprite;
        }
    }

    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    public void restoreLavaSprites(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> cir) {
        lavaSprites[0] = ommc_backupLavaSprites[0];
        lavaSprites[1] = ommc_backupLavaSprites[1];
    }

}
