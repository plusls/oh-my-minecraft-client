package com.plusls.ommc.mixin.feature.worldEaterMineHelper.fabric;

import com.plusls.ommc.feature.worldEaterMineHelper.CustomBakedModels;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import java.util.function.Supplier;

@Mixin(TerrainRenderContext.class)
public class MixinTerrainRenderContext {
    @Redirect(method = "tesselateBlock", at = @At(value = "INVOKE",
            //target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V", ordinal = 0,remap = true), remap = false)
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/class_1920;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V", ordinal = 0), remap = false)
    private void redirectEmitBlockQuads(FabricBakedModel fabricBakedModel, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        Block block = state.getBlock();
        if (CustomBakedModels.shouldUseCustomModel(block, pos)) {
            ((FabricBakedModel) CustomBakedModels.models.get(block)).emitBlockQuads(blockView, state, pos, randomSupplier, context);
        } else {
            fabricBakedModel.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        }
    }
}
