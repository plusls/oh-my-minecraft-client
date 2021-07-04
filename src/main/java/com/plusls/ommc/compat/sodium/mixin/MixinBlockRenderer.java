package com.plusls.ommc.compat.sodium.mixin;

import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.Dependency;
import com.plusls.ommc.compat.sodium.SodiumDependencyPredicate;
import com.plusls.ommc.feature.worldEaterMineHelper.BlockModelRendererContext;
import com.plusls.ommc.feature.worldEaterMineHelper.CustomBakedModels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Dependencies(dependencyList = @Dependency(modId = "sodium", version = ">=0.2", predicate = SodiumDependencyPredicate.BlockRendererPredicate.class))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer", remap = false)
public class MixinBlockRenderer {
    private final ThreadLocal<BlockModelRendererContext> ommcRenderContext = ThreadLocal.withInitial(BlockModelRendererContext::new);

    @Inject(method = "renderModel", at = @At(value = "HEAD"))
    private void initRenderContext(BlockRenderView world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, @Coerce Object buffers, boolean cull, long seed, CallbackInfoReturnable<Boolean> cir) {
        BlockModelRendererContext context = ommcRenderContext.get();
        context.pos = pos;
        context.state = state;
    }

    @ModifyVariable(method = "renderModel", at = @At(value = "HEAD"), ordinal = 0)
    private BakedModel modifyBakedModel(BakedModel bakedModel) {
        BlockModelRendererContext context = ommcRenderContext.get();
        Block block = context.state.getBlock();
        if (CustomBakedModels.shouldUseCustomModel(block, context.pos)) {
            BakedModel customModel = CustomBakedModels.models.get(block);
            if (customModel != null) {
                return CustomBakedModels.models.get(block);
            } else {
                return bakedModel;
            }
        } else {
            return bakedModel;
        }
    }
}
