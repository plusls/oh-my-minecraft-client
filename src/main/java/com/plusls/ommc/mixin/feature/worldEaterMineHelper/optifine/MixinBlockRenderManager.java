package com.plusls.ommc.mixin.feature.worldEaterMineHelper.optifine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.plusls.ommc.feature.worldEaterMineHelper.BlockModelRendererContext;
import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import com.plusls.ommc.mixin.accessor.AccessorBlockStateBase;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

import java.util.Random;

//#if MC > 11802
//$$ import net.minecraft.util.RandomSource;
//#endif

// 兼容 opt
@Dependencies(and = @Dependency("optifabric"))
@Mixin(BlockRenderDispatcher.class)
public class MixinBlockRenderManager {
    private final ThreadLocal<BlockModelRendererContext> ommcRenderContext = ThreadLocal.withInitial(BlockModelRendererContext::new);
    private final ThreadLocal<Integer> ommcOriginalLuminance = ThreadLocal.withInitial(() -> -1);

    @Inject(method = "renderBreakingTexture", at = @At(value = "HEAD"))
    private void initRenderContext0(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack matrix,
                                    VertexConsumer vertexConsumer, CallbackInfo ci) {
        BlockModelRendererContext context = ommcRenderContext.get();
        context.pos = pos;
        context.state = state;
    }

    @Inject(method = "renderBreakingTexture", at = @At(value = "RETURN"))
    private void clearRenderContext0(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack matrix,
                                     VertexConsumer vertexConsumer, CallbackInfo ci) {
        ommcRenderContext.get().clear();
        int originalLuminance = ommcOriginalLuminance.get();
        if (originalLuminance != -1) {
            ((AccessorBlockStateBase) state).setLightEmission(originalLuminance);
            ommcOriginalLuminance.set(-1);
        }
    }

    @Inject(method = "renderBatched", at = @At(value = "HEAD"))
    private void initRenderContext1(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack matrix,
                                    VertexConsumer vertexConsumer, boolean cull,
                                    //#if MC > 11802
                                    //$$ RandomSource random,
                                    //#else
                                    Random random,
                                    //#endif
                                    CallbackInfoReturnable<Boolean> cir) {
        BlockModelRendererContext context = ommcRenderContext.get();
        context.pos = pos;
        context.state = state;
    }

    @Inject(method = "renderBatched", at = @At(value = "RETURN"))
    private void clearRenderContext1(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack matrix,
                                     VertexConsumer vertexConsumer, boolean cull,
                                     //#if MC > 11802
                                     //$$ RandomSource random,
                                     //#else
                                     Random random,
                                     //#endif
                                     CallbackInfoReturnable<Boolean> cir) {
        ommcRenderContext.get().clear();
        int originalLuminance = ommcOriginalLuminance.get();
        if (originalLuminance != -1) {
            ((AccessorBlockStateBase) state).setLightEmission(originalLuminance);
            ommcOriginalLuminance.set(-1);
        }
    }

    @Inject(method = "getBlockModel", at = @At(value = "RETURN"), cancellable = true)
    private void useCustomModel(BlockState state, CallbackInfoReturnable<BakedModel> cir) {
        BlockModelRendererContext context = ommcRenderContext.get();
        if (context.pos == null) {
            return;
        }
        Block block = context.state.getBlock();
        if (WorldEaterMineHelperUtil.shouldUseCustomModel(state, context.pos)) {
            BakedModel model = WorldEaterMineHelperUtil.customFullModels.get(block);
            if (model != null) {
                ommcOriginalLuminance.set(((AccessorBlockStateBase) state).getLightEmission());
                ((AccessorBlockStateBase) state).setLightEmission(15);
                cir.setReturnValue(model);
            }
        }
    }
}