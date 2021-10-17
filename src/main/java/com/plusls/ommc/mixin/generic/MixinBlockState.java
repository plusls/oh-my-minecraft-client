package com.plusls.ommc.mixin.generic;

import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockState.class)
public interface MixinBlockState {
    @Accessor
    int getLuminance();

    @Accessor
    void setLuminance(int luminance);
}
