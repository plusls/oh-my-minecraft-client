package com.plusls.ommc.mixin.accessor;

import net.minecraft.network.chat.contents.LiteralContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LiteralContents.class)
public interface AccessorTextComponent {
    @Accessor
    String getText();

    @Mutable
    @Accessor
    void setText(String text);
}
