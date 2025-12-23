package com.tacticalstamina.handlers;

import com.tacticalstamina.TacticalStaminaMod;
import com.tacticalstamina.capabilities.StaminaCapability;
import com.tacticalstamina.config.StaminaConfig;
import com.tacticalstamina.network.PacketSyncStamina;
import com.tacticalstamina.network.StaminaNetwork;
import com.tacticalstamina.registry.StaminaAttributes;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = TacticalStaminaMod.MODID)
public class ServerStaminaHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!StaminaConfig.COMMON.enableStamina.get()) return;

        Player player = event.player;

        // 1. MOVEMENT LOGIC (Phase.END)
        if (event.phase == TickEvent.Phase.END) {
            if (player.onClimbable() && player.isShiftKeyDown()) {
                double speedMult = 0.4; 
                AttributeInstance climbAttr = player.getAttribute(StaminaAttributes.SLOW_CLIMB_SPEED.get());
                if (climbAttr != null) {
                    speedMult = climbAttr.getValue();
                }

                if (speedMult < 0.99) {
                    player.setDeltaMovement(player.getDeltaMovement().scale(speedMult));
                }
            }
            return;
        }

        // 2. STAMINA LOGIC (Phase.START)
        if (event.phase != TickEvent.Phase.START || player.level().isClientSide) return;

        player.getCapability(StaminaCapability.INSTANCE).ifPresent(cap -> {

            double baseAttr = 100.0;
            AttributeInstance attr = player.getAttribute(StaminaAttributes.MAX_STAMINA.get());
            if (attr != null) baseAttr = attr.getValue();

            float hungerPenalty = 0.0f;
            float maxHungerPen = StaminaConfig.COMMON.maxHungerPenalty.get().floatValue();
            
            // VANILLA HUNGER LOGIC
            int foodLevel = player.getFoodData().getFoodLevel();
            int hungerThreshold = StaminaConfig.COMMON.hungerPenaltyThreshold.get();

            if (foodLevel <= hungerThreshold && hungerThreshold > 0) {
                hungerPenalty = ((float)(hungerThreshold - foodLevel) / (float)hungerThreshold) * maxHungerPen;
            }

            float minMax = StaminaConfig.COMMON.minMaxStamina.get().floatValue();
            float effectiveMax = (float) baseAttr - cap.fatiguePenalty - hungerPenalty;
            
            if (effectiveMax < minMax) effectiveMax = minMax;
            cap.maxStamina = effectiveMax;

            boolean isConsuming = false;
            
            if (player.isSwimming()) {
                cap.stamina -= StaminaConfig.COMMON.depletionSwim.get().floatValue();
                isConsuming = true;
            } else if (player.onClimbable() && Math.abs(player.getDeltaMovement().y) > 0.1) {
                cap.stamina -= StaminaConfig.COMMON.depletionClimb.get().floatValue();
                isConsuming = true;
            } else if (player.isSprinting()) {
                cap.stamina -= StaminaConfig.COMMON.depletionSprint.get().floatValue();
                isConsuming = true;
            }

            if (isConsuming) {
                cap.staminaRegenDelay = StaminaConfig.COMMON.recoveryDelay.get();
            } else {
                if (cap.staminaRegenDelay > 0) {
                    cap.staminaRegenDelay--;
                } else if (cap.stamina < cap.maxStamina) {
                    float recovery = StaminaConfig.COMMON.recoveryPerTick.get().floatValue();
                    
                    if (player.onClimbable()) {
                        recovery *= StaminaConfig.COMMON.recoveryClimbMult.get().floatValue();
                    } else if (player.getDeltaMovement().lengthSqr() < 0.005) {
                        recovery *= StaminaConfig.COMMON.recoveryRestMult.get().floatValue();
                    }
                    
                    cap.stamina += recovery;
                }
            }

            if (cap.stamina < 0) cap.stamina = 0;
            if (cap.stamina > cap.maxStamina) cap.stamina = cap.maxStamina;
            
            if (cap.stamina <= 0 && player.isSprinting()) {
                player.setSprinting(false);
            }
            
            float fatigueThresh = StaminaConfig.COMMON.fatigueThreshold.get().floatValue();
            if (cap.stamina <= (baseAttr * fatigueThresh)) {
                cap.fatigueTimer++;
                cap.penaltyRegenDelay = StaminaConfig.COMMON.penaltyRecoveryDelay.get(); 
            } else {
                cap.fatigueTimer = 0;
            }

            boolean isRecovering = cap.stamina > cap.lastTickStamina + 0.001f;
            if (cap.fatigueTimer > 240) {
                if (!isRecovering) {
                    float maxExertion = StaminaConfig.COMMON.maxExertionPenalty.get().floatValue();
                    if (cap.fatiguePenalty < maxExertion) {
                        float baseRate = StaminaConfig.COMMON.penaltyBaseRate.get().floatValue();
                        float curve = StaminaConfig.COMMON.penaltyCurveFactor.get().floatValue();
                        
                        float ticksExceeded = (float)(cap.fatigueTimer - 240);
                        float exponentialRate = baseRate * (float) Math.exp(ticksExceeded / curve);
                        cap.fatiguePenalty += exponentialRate;
                        
                        if (cap.fatiguePenalty > maxExertion) cap.fatiguePenalty = maxExertion;
                    }
                }
            } 
            else if (cap.fatigueTimer == 0 && cap.fatiguePenalty > 0) {
                if (cap.penaltyRegenDelay > 0) {
                    cap.penaltyRegenDelay--;
                } else {
                    cap.fatiguePenalty = Math.max(0.0f, cap.fatiguePenalty - (50.0f / 1200.0f));
                }
            }

            cap.lastTickStamina = cap.stamina;
            if (player.tickCount % 5 == 0 || isConsuming) { 
                StaminaNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (net.minecraft.server.level.ServerPlayer) player), 
                    new PacketSyncStamina(cap.stamina, cap.maxStamina, cap.fatiguePenalty));
            }
        });
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!StaminaConfig.COMMON.enableStamina.get()) return;
        if (event.getEntity() instanceof Player p && !p.level().isClientSide) {
            p.getCapability(StaminaCapability.INSTANCE).ifPresent(c -> { 
                c.stamina -= StaminaConfig.COMMON.depletionJump.get().floatValue(); 
                c.staminaRegenDelay = StaminaConfig.COMMON.recoveryDelay.get(); 
            });
        }
    }
    
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (!StaminaConfig.COMMON.enableStamina.get()) return;
        if (!event.getEntity().level().isClientSide) {
            event.getEntity().getCapability(StaminaCapability.INSTANCE).ifPresent(c -> { 
                c.stamina -= StaminaConfig.COMMON.depletionAttack.get().floatValue(); 
                c.staminaRegenDelay = StaminaConfig.COMMON.recoveryDelay.get(); 
            });
        }
    }
    
    @SubscribeEvent
    public static void onBlockBroken(net.minecraftforge.event.level.BlockEvent.BreakEvent event) {
        if (!StaminaConfig.COMMON.enableStamina.get()) return;
        if (!event.getPlayer().level().isClientSide) {
            event.getPlayer().getCapability(StaminaCapability.INSTANCE).ifPresent(c -> { 
                c.stamina -= StaminaConfig.COMMON.depletionBlockBreak.get().floatValue(); 
                c.staminaRegenDelay = StaminaConfig.COMMON.recoveryDelay.get(); 
            });
        }
    }
}