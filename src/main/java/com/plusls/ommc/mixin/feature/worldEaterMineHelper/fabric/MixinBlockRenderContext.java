package com.plusls.ommc.mixin.feature.worldEaterMineHelper.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Supplier;

@Mixin(value = BlockRenderContext.class, remap = false)
public abstract class MixinBlockRenderContext implements RenderContext {
    @Final
    @Shadow
    private Supplier<Random> randomSupplier;

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V",
            shift = At.Shift.AFTER, ordinal = 0, remap = true))
    private void emitCustomBlockQuads(BlockAndTintGetter blockView, BakedModel model, BlockState state, BlockPos pos, PoseStack matrixStack, VertexConsumer buffer, Random random, long seed, int overlay, CallbackInfoReturnable<Boolean> cir) {
        WorldEaterMineHelperUtil.emitCustomBlockQuads(blockView, state, pos, randomSupplier, this);
    }
}
