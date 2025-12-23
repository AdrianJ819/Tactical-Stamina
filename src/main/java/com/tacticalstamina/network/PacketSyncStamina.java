package com.tacticalstamina.network;

import com.tacticalstamina.capabilities.StaminaCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketSyncStamina {
    private final float stamina, maxStamina, fatiguePenalty;

    public PacketSyncStamina(float stamina, float maxStamina, float fatiguePenalty) {
        this.stamina = stamina;
        this.maxStamina = maxStamina;
        this.fatiguePenalty = fatiguePenalty;
    }

    public static void encode(PacketSyncStamina msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.stamina);
        buf.writeFloat(msg.maxStamina);
        buf.writeFloat(msg.fatiguePenalty);
    }

    public static PacketSyncStamina decode(FriendlyByteBuf buf) {
        return new PacketSyncStamina(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(PacketSyncStamina msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(StaminaCapability.INSTANCE).ifPresent(cap -> {
                    cap.stamina = msg.stamina;
                    cap.maxStamina = msg.maxStamina;
                    cap.fatiguePenalty = msg.fatiguePenalty;
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}