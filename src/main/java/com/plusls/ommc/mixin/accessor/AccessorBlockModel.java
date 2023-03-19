package com.plusls.ommc.mixin.accessor;

import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockModel.class)
public interface AccessorBlockModel {
    @Accessor
    BlockModel getParent();

    @Accessor()
    //#if MC > 11903
    Boolean getHasAmbientOcclusion();
    //#else
    //$$ boolean getHasAmbientOcclusion();
    //#endif

    @Mutable
    @Accessor
    //#if MC > 11903
    void setHasAmbientOcclusion(Boolean hasAmbientOcclusion);
    //#else
    //$$ void setHasAmbientOcclusion(boolean hasAmbientOcclusion);
    //#endif
}
