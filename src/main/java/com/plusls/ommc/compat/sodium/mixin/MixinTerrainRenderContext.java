package com.plusls.ommc.compat.sodium.mixin;

import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.Dependency;
import com.plusls.ommc.feature.blockModelNoOffset.BlockModelNoOffsetUtil;
import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import java.util.function.Supplier;

@Dependencies(dependencyList = @Dependency(modId = "sodium", version = ">=0.3.2"))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.render.renderer.TerrainRenderContext", remap = false)
public class MixinTerrainRenderContext {
    @Dynamic
    @Redirect(method = "renderBlock", at = @At(value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads", ordinal = 0))
    private void emitCustomBlockQuads(FabricBakedModel model, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        WorldEaterMineHelperUtil.emitCustomBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    @Dynamic
    @Redirect(method = "renderBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getModelOffset(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Vec3d;", ordinal = 0, remap = true))
    private Vec3d blockModelNoOffset(BlockState blockState, BlockView world, BlockPos pos) {
        return BlockModelNoOffsetUtil.blockModelNoOffset(blockState, world, pos);
    }

}
