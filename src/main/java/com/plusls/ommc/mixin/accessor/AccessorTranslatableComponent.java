package com.plusls.ommc.mixin.accessor;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TranslatableContents.class)
public interface AccessorTranslatableComponent {

    //#if MC > 11502
    @Accessor
    void setDecomposedWith(Language decomposedWith);
    //#else
    //$$ @Accessor
    //$$ void setDecomposedLanguageTime(long decomposedLanguageTime);
    //#endif
}
