package com.plusls.ommc.mixin.feature.autoSwitchElytra;

import com.mojang.authlib.GameProfile;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.autoSwitchElytra.AutoSwitchElytraUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    protected MinecraftClient client;

    boolean prevFallFlying = false;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private void autoSwitchElytra(CallbackInfo ci) {
        if (!Configs.FeatureToggle.AUTO_SWITCH_ELYTRA.getBooleanValue()) {
            return;
        }
        ItemStack chestItemStack = this.getEquippedStack(EquipmentSlot.CHEST);
        if (chestItemStack.isOf(Items.ELYTRA) || !AutoSwitchElytraUtil.myCheckFallFlying(this)) {
            return;
        }
        AutoSwitchElytraUtil.autoSwitch(AutoSwitchElytraUtil.CHEST_SLOT_IDX, this.client, (ClientPlayerEntity) (Object) this, itemStack -> itemStack.isOf(Items.ELYTRA));
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isFallFlying()Z", ordinal = 0))
    private void autoSwitchChest(CallbackInfo ci) {
        if (!Configs.FeatureToggle.AUTO_SWITCH_ELYTRA.getBooleanValue()) {
            return;
        }
        ItemStack chestItemStack = this.getEquippedStack(EquipmentSlot.CHEST);
        if (!chestItemStack.isOf(Items.ELYTRA) || !prevFallFlying || this.isFallFlying()) {
            prevFallFlying = this.isFallFlying();
            return;
        }
        prevFallFlying = this.isFallFlying();
        AutoSwitchElytraUtil.autoSwitch(AutoSwitchElytraUtil.CHEST_SLOT_IDX, this.client, (ClientPlayerEntity) (Object) this, itemStack -> Registry.ITEM.getId(itemStack.getItem()).toString().contains("_chestplate"));

    }

}
