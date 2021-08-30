package com.plusls.ommc.compat.canvas.mixin;

import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.Dependency;
import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import java.util.function.Supplier;

@Dependencies(dependencyList = @Dependency(modId = "canvas", version = ">=1.0.1511"))
@Pseudo
@Mixin(targets = "grondag.canvas.apiimpl.rendercontext.TerrainRenderContext", remap = false)
public abstract class MixinTerrainRenderContext {
    @Redirect(method = "renderInner", at = @At(value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads", ordinal = 0))
    private void emitCustomBlockQuads(FabricBakedModel model, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        WorldEaterMineHelperUtil.emitCustomBlockQuads(blockView, state, pos, randomSupplier, context);
    }
}
