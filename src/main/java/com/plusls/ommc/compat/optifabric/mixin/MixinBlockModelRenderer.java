package com.plusls.ommc.compat.optifabric.mixin;


import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.Dependency;
import com.plusls.ommc.feature.blockModelNoOffset.BlockModelNoOffsetUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Dependencies(dependencyList = @Dependency(modId = "optifabric", version = "*"))
@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {

    @Dynamic
    @Redirect(method = "tesselateBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getModelOffset(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Vec3d;", remap = true), remap = false)
    private Vec3d blockModelNoOffset(BlockState blockState, BlockView world, BlockPos pos) {
        return BlockModelNoOffsetUtil.blockModelNoOffset(blockState, world, pos);
    }
}
