package com.plusls.ommc.compat.canvas.mixin;

import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.Dependency;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceResourceLoader;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Dependencies(dependencyList = @Dependency(modId = "canvas", version = ">=1.0.1511"))
@Pseudo
@Mixin(targets = "grondag.canvas.apiimpl.fluid.LavaFluidModel", remap = false)
public class MixinLavaFluidModel {
    @Dynamic
    @Inject(method = "getFluidSprites", at = @At(value = "HEAD"), cancellable = true)
    private void modifyLavaSprites(BlockRenderView view, BlockPos pos, FluidState state, CallbackInfoReturnable<Sprite[]> cir) {
        if (Configs.FeatureToggle.HIGHLIGHT_LAVA_SOURCE.getBooleanValue() && state.isIn(FluidTags.LAVA) &&
                view.getBlockState(pos).get(FluidBlock.LEVEL) == 0) {
            cir.setReturnValue(LavaSourceResourceLoader.lavaSourceSpites);
        }
    }
}
