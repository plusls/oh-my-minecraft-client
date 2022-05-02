package com.plusls.ommc.mixin.accessor;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TranslatableComponent.class)
public interface AccessorTranslatableComponent {
    @Accessor
    void setDecomposedWith(Language decomposedWith);
}
