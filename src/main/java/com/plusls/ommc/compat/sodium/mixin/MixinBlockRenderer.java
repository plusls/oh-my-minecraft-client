package com.plusls.ommc.compat.sodium.mixin;

import com.plusls.ommc.feature.blockModelNoOffset.BlockModelNoOffsetUtil;
import com.plusls.ommc.feature.worldEaterMineHelper.BlockModelRendererContext;
import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import com.plusls.ommc.mixin.accessor.AccessorBlockStateBase;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

// TODO
@Dependencies(and = @Dependency("sodium"))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer", remap = false)
public class MixinBlockRenderer {
    private final ThreadLocal<BlockModelRendererContext> ommcRenderContext = ThreadLocal.withInitial(BlockModelRendererContext::new);
    private final ThreadLocal<Integer> ommcOriginalLuminance = ThreadLocal.withInitial(() -> -1);

    @Dynamic
    @Inject(method = "renderModel", at = @At(value = "HEAD"))
    private void initRenderContext(BlockAndTintGetter world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, @Coerce Object buffers, boolean cull, long seed, CallbackInfoReturnable<Boolean> cir) {
        BlockModelRendererContext context = ommcRenderContext.get();
        context.pos = pos;
        context.state = state;
    }

    @Dynamic
    @Redirect(method = "renderModel",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0, remap = true))
    private Vec3 blockModelNoOffset(BlockState blockState, BlockGetter world, BlockPos pos) {
        return BlockModelNoOffsetUtil.blockModelNoOffset(blockState, world, pos);
    }

    @Dynamic
    @ModifyVariable(method = "renderModel", at = @At(value = "HEAD"), ordinal = 0)
    private BakedModel modifyBakedModel(BakedModel bakedModel) {
        BlockModelRendererContext context = ommcRenderContext.get();
        if (WorldEaterMineHelperUtil.shouldUseCustomModel(context.state, context.pos)) {
            BakedModel customModel = WorldEaterMineHelperUtil.customFullModels.get(context.state.getBlock());
            if (customModel != null) {
                ommcOriginalLuminance.set(((AccessorBlockStateBase) context.state).getLightEmission());
                ((AccessorBlockStateBase) context.state).setLightEmission(15);
                return customModel;
            }
        }
        return bakedModel;
    }

    @Dynamic
    @Inject(method = "renderModel", at = @At(value = "RETURN"))
    private void postRenderModel(BlockAndTintGetter world, BlockState state, BlockPos pos, BlockPos origin,
                                 BakedModel model, @Coerce Object buffers, boolean cull,
                                 long seed, CallbackInfoReturnable<Boolean> cir) {
        int originalLuminance = ommcOriginalLuminance.get();
        if (originalLuminance != -1) {
            ((AccessorBlockStateBase) state).setLightEmission(originalLuminance);
            ommcOriginalLuminance.set(-1);
        }
    }
}
