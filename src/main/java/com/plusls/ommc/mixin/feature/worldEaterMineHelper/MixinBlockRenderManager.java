package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import com.plusls.ommc.feature.worldEaterMineHelper.BlockModelRendererContext;
import com.plusls.ommc.feature.worldEaterMineHelper.CustomBakedModels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

// 兼容 opt
@Mixin(BlockRenderManager.class)
public class MixinBlockRenderManager {
    private final ThreadLocal<BlockModelRendererContext> ommcRenderContext = ThreadLocal.withInitial(BlockModelRendererContext::new);

    @Inject(method = "renderDamage", at = @At(value = "HEAD"))
    private void initRenderContext0(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix,
                                    VertexConsumer vertexConsumer, CallbackInfo ci) {
        BlockModelRendererContext context = ommcRenderContext.get();
        context.pos = pos;
        context.state = state;
    }

    @Inject(method = "renderDamage", at = @At(value = "RETURN"))
    private void clearRenderContext0(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix,
                                     VertexConsumer vertexConsumer, CallbackInfo ci) {
        ommcRenderContext.get().clear();
    }

    @Inject(method = "renderBlock", at = @At(value = "HEAD"))
    private void initRenderContext1(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix,
                                    VertexConsumer vertexConsumer, boolean cull, Random random,
                                    CallbackInfoReturnable<Boolean> cir) {
        BlockModelRendererContext context = ommcRenderContext.get();
        context.pos = pos;
        context.state = state;
    }

    @Inject(method = "renderBlock", at = @At(value = "RETURN"))
    private void clearRenderContext1(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix,
                                     VertexConsumer vertexConsumer, boolean cull, Random random,
                                     CallbackInfoReturnable<Boolean> cir) {
        ommcRenderContext.get().clear();
    }

    @Inject(method = "getModel", at = @At(value = "RETURN"), cancellable = true)
    private void useCustomModel(BlockState state, CallbackInfoReturnable<BakedModel> cir) {
        BlockModelRendererContext context = ommcRenderContext.get();
        if (context.pos == null) {
            return;
        }
        Block block = context.state.getBlock();
        if (CustomBakedModels.shouldUseCustomModel(block, context.pos)) {
            cir.setReturnValue(CustomBakedModels.models.get(block));
        }
    }
}
