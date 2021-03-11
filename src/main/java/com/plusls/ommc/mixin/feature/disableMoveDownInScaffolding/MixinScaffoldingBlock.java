package com.plusls.ommc.mixin.feature.disableMoveDownInScaffolding;

import com.plusls.ommc.config.Configs;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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

    @Inject(method = "getCollisionShape", at = @At(value = "RETURN"), cancellable = true)
    private void setNormalOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (cir.getReturnValue() != NORMAL_OUTLINE_SHAPE) {
            if (Configs.FeatureToggle.DISABLE_MOVE_DOWN_IN_SCAFFOLDING.getBooleanValue() &&
                    context.isDescending() && context.isAbove(VoxelShapes.fullCube(), pos, true)) {
                for (String whitelistId : Configs.Lists.MOVE_DOWN_IN_SCAFFOLDING_WHITELIST.getStrings()) {
                    Item item = Registry.ITEM.get(new Identifier(whitelistId));
                    if (Registry.ITEM.getId(item).toString().equals(whitelistId) && context.isHolding(item)) {
                        return;
                    }
                }
                cir.setReturnValue(NORMAL_OUTLINE_SHAPE);
            }
        }
    }
}
