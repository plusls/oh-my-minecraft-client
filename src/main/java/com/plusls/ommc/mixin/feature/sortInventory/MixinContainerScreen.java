package com.plusls.ommc.mixin.feature.sortInventory;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ContainerScreen.class)
public interface MixinContainerScreen {
    @Invoker("getSlotAt")
    Slot getSlotAt(double xPosition, double yPosition);
}
