package com.plusls.ommc.compat.optifabric.mixin;


import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.Dependency;
import com.plusls.ommc.feature.blockModelNoOffset.BlockModelNoOffsetUtil;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Dependencies(dependencyList = @Dependency(modId = "optifabric", version = "*"))
@Mixin(ModelBlockRenderer.class)
public class MixinBlockModelRenderer {

    @Redirect(method = "tesselateBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;",
                    remap = true, ordinal = 0),
            remap = false)
    private Vec3 blockModelNoOffset(BlockState blockState, BlockGetter world, BlockPos pos) {
        return BlockModelNoOffsetUtil.blockModelNoOffset(blockState, world, pos);
    }
}
