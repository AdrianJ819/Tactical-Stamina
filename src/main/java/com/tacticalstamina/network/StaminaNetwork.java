package com.tacticalstamina.network;

import com.tacticalstamina.TacticalStaminaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class StaminaNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(TacticalStaminaMod.MODID, "stamina_net"),
        () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, PacketSyncStamina.class, PacketSyncStamina::encode, PacketSyncStamina::decode, PacketSyncStamina::handle);
    }
}