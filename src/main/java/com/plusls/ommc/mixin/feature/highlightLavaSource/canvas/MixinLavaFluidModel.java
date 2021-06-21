package com.plusls.ommc.mixin.feature.highlightLavaSource.canvas;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceResourceLoader;
import grondag.canvas.apiimpl.fluid.LavaFluidModel;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LavaFluidModel.class, remap = false)
public class MixinLavaFluidModel {
    @Inject(method = "getFluidSprites", at = @At(value = "HEAD"), cancellable = true)
    private void modifyLavaSprites(BlockRenderView view, BlockPos pos, FluidState state, CallbackInfoReturnable<Sprite[]> cir) {
        if (Configs.FeatureToggle.HIGHLIGHT_LAVA_SOURCE.getBooleanValue() && state.isIn(FluidTags.LAVA) &&
                view.getBlockState(pos).get(FluidBlock.LEVEL) == 0) {
            cir.setReturnValue(LavaSourceResourceLoader.lavaSourceSpites);
        }
    }
}
