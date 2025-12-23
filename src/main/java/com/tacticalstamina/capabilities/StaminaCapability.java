package com.tacticalstamina.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StaminaCapability implements INBTSerializable<CompoundTag> {
    public static final Capability<StaminaCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public float stamina = 100.0f;
    public float maxStamina = 100.0f;
    public int staminaRegenDelay = 0; 
    
    public int fatigueTimer = 0;
    public float fatiguePenalty = 0.0f;
    public int penaltyRegenDelay = 0; 
    public float lastTickStamina = 100.0f;

    public void copyFrom(StaminaCapability other) {
        this.stamina = other.stamina;
        this.maxStamina = other.maxStamina;
        this.staminaRegenDelay = other.staminaRegenDelay;
        this.fatigueTimer = other.fatigueTimer;
        this.fatiguePenalty = other.fatiguePenalty;
        this.penaltyRegenDelay = other.penaltyRegenDelay;
        this.lastTickStamina = other.lastTickStamina;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Stamina", stamina);
        tag.putFloat("MaxStamina", maxStamina);
        tag.putInt("RegenDelay", staminaRegenDelay);
        tag.putInt("FatigueTimer", fatigueTimer);
        tag.putFloat("FatiguePenalty", fatiguePenalty);
        tag.putInt("PenaltyDelay", penaltyRegenDelay);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        stamina = nbt.getFloat("Stamina");
        maxStamina = nbt.getFloat("MaxStamina");
        staminaRegenDelay = nbt.getInt("RegenDelay");
        fatigueTimer = nbt.getInt("FatigueTimer");
        fatiguePenalty = nbt.getFloat("FatiguePenalty");
        penaltyRegenDelay = nbt.getInt("PenaltyDelay");
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<StaminaCapability> instance = LazyOptional.of(StaminaCapability::new);

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == INSTANCE ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() { return instance.orElseThrow(IllegalStateException::new).serializeNBT(); }

        @Override
        public void deserializeNBT(CompoundTag nbt) { instance.orElseThrow(IllegalStateException::new).deserializeNBT(nbt); }
    }
}