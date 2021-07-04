package com.plusls.ommc.compat.sodium.mixin;

import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.Dependency;
import com.plusls.ommc.compat.sodium.SodiumDependencyPredicate;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceResourceLoader;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Dependencies(dependencyList = @Dependency(modId = "sodium", version = ">=0.2", predicate = SodiumDependencyPredicate.FluidRendererOldPredicate.class))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer", remap = false)
public class MixinFluidRendererOld {
    @Shadow
    @Final
    private Sprite[] lavaSprites;

    @Inject(method = "render", at = @At("HEAD"))
    public void modifyLavaSprites(BlockRenderView world, FluidState fluidState, BlockPos pos, @Coerce Object buffers, CallbackInfoReturnable<Boolean> info) {
        if (Configs.FeatureToggle.HIGHLIGHT_LAVA_SOURCE.getBooleanValue() && fluidState.isIn(FluidTags.LAVA) &&
                world.getBlockState(pos).get(FluidBlock.LEVEL) == 0) {
            lavaSprites[0] = LavaSourceResourceLoader.lavaSourceStillSprite;
            lavaSprites[1] = LavaSourceResourceLoader.lavaSourceFlowSprite;
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void restoreLavaSprites(BlockRenderView world, FluidState fluidState, BlockPos pos, @Coerce Object buffers, CallbackInfoReturnable<Boolean> info) {
        lavaSprites[0] = LavaSourceResourceLoader.defaultLavaSourceStillSprite;
        lavaSprites[1] = LavaSourceResourceLoader.defaultLavaSourceFlowSprite;
    }

}
