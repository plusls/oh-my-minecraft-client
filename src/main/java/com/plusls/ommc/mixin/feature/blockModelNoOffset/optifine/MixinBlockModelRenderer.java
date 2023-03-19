package com.plusls.ommc.mixin.feature.blockModelNoOffset.optifine;


import com.plusls.ommc.feature.blockModelNoOffset.BlockModelNoOffsetUtil;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

//#if MC <= 11605
//$$ import org.spongepowered.asm.mixin.Dynamic;
//#endif

@Dependencies(and = @Dependency("optifabric"))
@Mixin(ModelBlockRenderer.class)
public class MixinBlockModelRenderer {

    //#if MC > 11605
    @Redirect(method = "tesselateBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;",
                    remap = true, ordinal = 0),
            remap = false)
    private Vec3 blockModelNoOffset(BlockState blockState, BlockGetter world, BlockPos pos) {
        return BlockModelNoOffsetUtil.blockModelNoOffset(blockState, world, pos);
    }
    //#else
    //$$ @Dynamic
    //$$ @Redirect(method = "renderModel", at = @At(value = "INVOKE",
    //$$         target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;",
    //$$         remap = true),
    //$$         remap = false)
    //$$ private Vec3 blockModelNoOffset(BlockState blockState, BlockGetter world, BlockPos pos) {
    //$$     return BlockModelNoOffsetUtil.blockModelNoOffset(blockState, world, pos);
    //$$ }
    //#endif
}
