package com.plusls.ommc.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC > 11502
import net.minecraft.world.level.block.state.BlockBehaviour;
//#else
//$$ import net.minecraft.world.level.block.state.BlockState;
//#endif

//#if MC > 11502
@Mixin(BlockBehaviour.BlockStateBase.class)
//#else
//$$ @Mixin(BlockState.class)
//#endif
public interface AccessorBlockStateBase {
    @Accessor
    int getLightEmission();


    @Mutable
    @Accessor
    void setLightEmission(int lightEmission);
}
