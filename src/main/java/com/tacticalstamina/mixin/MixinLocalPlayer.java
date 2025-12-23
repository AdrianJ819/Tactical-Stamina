package com.tacticalstamina.mixin;

import com.tacticalstamina.capabilities.StaminaCapability;
import com.tacticalstamina.config.StaminaConfig;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {

    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void preventSprintingStart(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        
        if (StaminaConfig.COMMON.enableStamina.get()) {
            player.getCapability(StaminaCapability.INSTANCE).ifPresent(cap -> {
                if (cap.stamina <= 0) {
                    cir.setReturnValue(false);
                }
            });
        }
    }
}