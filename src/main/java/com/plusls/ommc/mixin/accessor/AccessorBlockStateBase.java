package com.plusls.ommc.mixin.accessor;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.BlockStateBase.class)
public interface AccessorBlockStateBase {
    @Accessor
    int getLightEmission();


    @Mutable
    @Accessor
    void setLightEmission(int lightEmission);
}
