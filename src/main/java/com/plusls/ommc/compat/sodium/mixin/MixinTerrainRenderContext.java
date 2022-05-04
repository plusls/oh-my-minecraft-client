package com.plusls.ommc.compat.sodium.mixin;

import com.plusls.ommc.feature.blockModelNoOffset.BlockModelNoOffsetUtil;
import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.util.MiscUtil;

import java.util.Random;
import java.util.function.Supplier;

//#if MC > 11802
//$$ import net.minecraft.util.RandomSource;
//#endif

// TODO
@Dependencies(and = @Dependency(value = "sodium", versionPredicate = ">=0.5"))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.render.renderer.TerrainRenderContext", remap = false)
public class MixinTerrainRenderContext {
    @Dynamic
    @Redirect(method = "renderBlock", at = @At(value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V",
            ordinal = 0,
            remap = true))
    private void emitCustomBlockQuads(FabricBakedModel model, BlockAndTintGetter blockView, BlockState state, BlockPos pos,
                                      //#if MC > 11802
                                      //$$ Supplier<RandomSource> randomSupplier,
                                      //#else
                                      Supplier<Random> randomSupplier,
                                      //#endif
                                      RenderContext context) {
        model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        WorldEaterMineHelperUtil.emitCustomBlockQuads(blockView, state, pos, MiscUtil.cast(randomSupplier), context);
    }

    @Dynamic
    @Redirect(method = "renderBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0,
            remap = true))
    private Vec3 blockModelNoOffset(BlockState blockState, BlockGetter world, BlockPos pos) {
        return BlockModelNoOffsetUtil.blockModelNoOffset(blockState, world, pos);
    }

}
