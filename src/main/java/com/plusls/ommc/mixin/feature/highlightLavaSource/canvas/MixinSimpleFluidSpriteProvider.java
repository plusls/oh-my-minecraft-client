package com.plusls.ommc.mixin.feature.highlightLavaSource.canvas;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceResourceLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;


@Dependencies(and = @Dependency("frex"))
@Pseudo
@Mixin(targets = "io.vram.frex.impl.model.SimpleFluidSpriteProvider", remap = false)
public abstract class MixinSimpleFluidSpriteProvider {
    private boolean isLava;
    private final TextureAtlasSprite[] lavaSourceSpites = new TextureAtlasSprite[3];

    @Dynamic
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void preInit(ResourceLocation stillSpriteName, ResourceLocation flowingSpriteName,
                         ResourceLocation overlaySpriteName, CallbackInfo ci) {
        this.isLava = stillSpriteName.toString().equals("minecraft:block/lava_still");
    }

    @Dynamic
    @Inject(method = "getFluidSprites", at = @At(value = "RETURN"), cancellable = true)
    private void setLavaSprite(BlockAndTintGetter view, BlockPos pos,
                               FluidState state, CallbackInfoReturnable<TextureAtlasSprite[]> cir) {
        if (this.isLava) {
            if (lavaSourceSpites[0] != LavaSourceResourceLoader.lavaSourceSpites[0]) {
                lavaSourceSpites[0] = LavaSourceResourceLoader.lavaSourceSpites[0];
                lavaSourceSpites[1] = LavaSourceResourceLoader.lavaSourceSpites[1];
            }
            if (Configs.highlightLavaSource && state.is(FluidTags.LAVA) &&
                    view.getBlockState(pos).getValue(LiquidBlock.LEVEL) == 0) {
                cir.setReturnValue(lavaSourceSpites);
            }
        }
    }

}
