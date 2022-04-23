package com.plusls.ommc.mixin.feature.worldEaterMineHelper.fabric;

import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainBlockRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
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
    }, at = @At(value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V", shift = At.Shift.AFTER, ordinal = 0, remap = true), require = 0)
    private void emitCustomBlockQuads(BlockState blockState, BlockPos blockPos, BakedModel model, MatrixStack matrixStack, CallbackInfoReturnable<Boolean> cir) {
        WorldEaterMineHelperUtil.emitCustomBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, this);
    }
}