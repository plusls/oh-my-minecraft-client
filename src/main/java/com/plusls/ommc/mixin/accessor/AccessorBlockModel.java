package com.plusls.ommc.mixin.accessor;

import net.minecraft.client.renderer.block.model.BlockModel;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockModel.class)
public interface AccessorBlockModel {
    @Accessor
    BlockModel getParent();

    @Accessor
    boolean getHasAmbientOcclusion();

    @Mutable
    @Accessor
    void setHasAmbientOcclusion(boolean hasAmbientOcclusion);
}
