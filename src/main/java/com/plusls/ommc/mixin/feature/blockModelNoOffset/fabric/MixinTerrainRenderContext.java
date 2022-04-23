package com.plusls.ommc.mixin.feature.blockModelNoOffset.fabric;

import com.plusls.ommc.feature.blockModelNoOffset.BlockModelNoOffsetUtil;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainBlockRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TerrainRenderContext.class, remap = false)
public abstract class MixinTerrainRenderContext implements RenderContext {
    @Final
    @Shadow
    private TerrainBlockRenderInfo blockInfo;

    @Dynamic
    @Inject(method = {
            "tessellateBlock", // For fabric-renderer-indigo 0.5.0 and above
            "tesselateBlock" // For fabric-renderer-indigo 0.5.0 below
    }, at = @At(value = "HEAD"), require = 0)
    private void blockModelNoOffset(BlockState blockState, BlockPos blockPos, BakedModel model, MatrixStack matrixStack, CallbackInfoReturnable<Boolean> cir) {
        Vec3d offsetPos = blockState.getModelOffset(blockInfo.blockView, blockPos);
        if (BlockModelNoOffsetUtil.shouldNoOffset(blockState)) {
            matrixStack.translate(-offsetPos.x, -offsetPos.y, -offsetPos.z);
        }
    }
}