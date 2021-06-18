package com.plusls.ommc.mixin.feature.worldEaterMineHelper.canvas;

import com.plusls.ommc.feature.worldEaterMineHelper.CustomBakedModels;
import grondag.canvas.apiimpl.rendercontext.AbstractBlockRenderContext;
import grondag.canvas.apiimpl.rendercontext.TerrainRenderContext;
import grondag.canvas.terrain.region.FastRenderRegion;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TerrainRenderContext.class, remap = false)
public abstract class MixinTerrainRenderContext extends AbstractBlockRenderContext<FastRenderRegion> {
    protected MixinTerrainRenderContext(String name) {
        super(name);
    }

    @Inject(method = "renderInner", at = @At(value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads", ordinal = 0))
    private void emitCustomBlockQuads(BlockState blockState, BlockPos blockPos, boolean defaultAo, FabricBakedModel model, MatrixStack matrixStack, CallbackInfo ci) {
        Block block = blockState.getBlock();
        if (CustomBakedModels.shouldUseCustomModel(block, blockPos)) {
            FabricBakedModel customModel = (FabricBakedModel) CustomBakedModels.models.get(block);
            if (customModel != null) {
                customModel.emitBlockQuads(region, blockState, blockPos, randomSupplier, this);
            }
        }
    }
}
