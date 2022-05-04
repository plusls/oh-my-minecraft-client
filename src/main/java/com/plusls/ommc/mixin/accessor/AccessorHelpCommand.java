package com.plusls.ommc.mixin.accessor;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.commands.HelpCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HelpCommand.class)
public interface AccessorHelpCommand {
    @Accessor("ERROR_FAILED")
    static SimpleCommandExceptionType getFailedException() {
        throw new AssertionError("mixin");
    }
}
