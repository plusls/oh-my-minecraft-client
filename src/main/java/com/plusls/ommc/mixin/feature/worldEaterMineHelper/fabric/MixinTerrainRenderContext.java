package com.plusls.ommc.mixin.feature.worldEaterMineHelper.fabric;

import com.plusls.ommc.feature.worldEaterMineHelper.CustomBakedModels;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainBlockRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TerrainRenderContext.class, remap = false)
public class MixinTerrainRenderContext {
    @Final
    @Shadow
    private TerrainBlockRenderInfo blockInfo;

    @Inject(method = "tesselateBlock", at = @At(value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads", ordinal = 0))
    private void emitCustomBlockQuads(BlockState state, BlockPos pos, BakedModel model, MatrixStack matrixStack, CallbackInfoReturnable<Boolean> cir) {
        Block block = blockInfo.blockState.getBlock();
        if (CustomBakedModels.shouldUseCustomModel(block, blockInfo.blockPos)) {
            ((FabricBakedModel) CustomBakedModels.models.get(block)).emitBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, (TerrainRenderContext) (Object) this);
        }
    }
}
