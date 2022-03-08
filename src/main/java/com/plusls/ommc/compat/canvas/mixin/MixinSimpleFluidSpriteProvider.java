package com.plusls.ommc.compat.canvas.mixin;

import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.Dependency;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceResourceLoader;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Dependencies(dependencyList = @Dependency(modId = "frex", version = "*"))
@Pseudo
@Mixin(targets = "io.vram.frex.impl.model.SimpleFluidSpriteProvider", remap = false)
public abstract class MixinSimpleFluidSpriteProvider {
    private boolean isLava;
    private final Sprite[] lavaSourceSpites = new Sprite[3];

    @Dynamic
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void preInit(Identifier stillSpriteName, Identifier flowingSpriteName, Identifier overlaySpriteName, CallbackInfo ci) {
        this.isLava = stillSpriteName.toString().equals("minecraft:block/lava_still");
    }

    @Dynamic
    @Inject(method = "getFluidSprites", at = @At(value = "RETURN"), cancellable = true)
    private void setLavaSprite(BlockRenderView view, BlockPos pos, FluidState state, CallbackInfoReturnable<Sprite[]> cir) {
        if (this.isLava) {
            if (lavaSourceSpites[0] != LavaSourceResourceLoader.lavaSourceSpites[0]) {
                lavaSourceSpites[0] = LavaSourceResourceLoader.lavaSourceSpites[0];
                lavaSourceSpites[1] = LavaSourceResourceLoader.lavaSourceSpites[1];
            }
            if (Configs.FeatureToggle.HIGHLIGHT_LAVA_SOURCE.getBooleanValue() && state.isIn(FluidTags.LAVA) &&
                    view.getBlockState(pos).get(FluidBlock.LEVEL) == 0) {
                cir.setReturnValue(lavaSourceSpites);
            }
        }
    }

}
