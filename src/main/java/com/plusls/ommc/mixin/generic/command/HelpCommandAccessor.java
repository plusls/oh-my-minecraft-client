package com.plusls.ommc.mixin.generic.command;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.HelpCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/HelpCommandAccessor.java
@Mixin(HelpCommand.class)
public interface HelpCommandAccessor {
    @Accessor("FAILED_EXCEPTION")
    static SimpleCommandExceptionType getFailedException() {
        throw new AssertionError("mixin");
    }
}
