package com.plusls.ommc.mixin.feature.emptyHandMoveDownInScaffolding;

import com.plusls.ommc.config.Configs;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScaffoldingBlock.class)
public class MixinScaffoldingBlock {
    @Shadow
    @Final
    private static VoxelShape NORMAL_OUTLINE_SHAPE;

    @Inject(method = "getCollisionShape", at=@At(value = "RETURN"), cancellable = true)
    private void setNormalOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (cir.getReturnValue() != NORMAL_OUTLINE_SHAPE) {
            if (Configs.FeatureToggle.EMPTY_HAND_MOVE_DOWN_IN_SCAFFOLDING.getBooleanValue() &&
                    context.isDescending() && !context.isHolding(Items.AIR) &&
                    context.isAbove(VoxelShapes.fullCube(), pos, true)) {
                cir.setReturnValue(NORMAL_OUTLINE_SHAPE);
            }
        }
    }
}
