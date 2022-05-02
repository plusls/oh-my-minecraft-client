package com.plusls.ommc.mixin.feature.disableMoveDownInScaffolding;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
    private static VoxelShape STABLE_SHAPE;

    @Inject(method = "getCollisionShape", at = @At(value = "RETURN"), cancellable = true)
    private void setNormalOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (cir.getReturnValue() != STABLE_SHAPE) {
            if (Configs.FeatureToggle.DISABLE_MOVE_DOWN_IN_SCAFFOLDING.getBooleanValue() &&
                    context.isDescending() && context.isAbove(Shapes.block(), pos, true)) {
                assert Minecraft.getInstance().player != null;
                Item item = Minecraft.getInstance().player.getMainHandItem().getItem();
                String itemId = Registry.ITEM.getKey(item).toString();
                String itemName = item.getDescription().getString();
                if (Configs.Lists.MOVE_DOWN_IN_SCAFFOLDING_WHITELIST.getStrings().stream().anyMatch(s -> itemId.contains(s) || itemName.contains(s))) {
                    return;
                }
                cir.setReturnValue(STABLE_SHAPE);
            }
        }
    }
}
