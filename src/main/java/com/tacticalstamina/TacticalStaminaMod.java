package com.tacticalstamina;

import com.tacticalstamina.capabilities.StaminaCapability;
import com.tacticalstamina.network.StaminaNetwork;
import com.tacticalstamina.registry.StaminaAttributes;
import net.minecraft.resources.ResourceLocation;
import com.tacticalstamina.config.StaminaConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;

@Mod(TacticalStaminaMod.MODID)
public class TacticalStaminaMod {
    public static final String MODID = "tactical_stamina";

    public TacticalStaminaMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StaminaConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, StaminaConfig.CLIENT_SPEC);

        StaminaAttributes.ATTRIBUTES.register(modEventBus);
        modEventBus.addListener(this::attachAttributes);
        modEventBus.addListener(this::registerCaps);

        StaminaNetwork.register();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addGenericListener(net.minecraft.world.entity.Entity.class, this::attachEntityCaps);
    }

    private void attachAttributes(EntityAttributeModificationEvent event) {
        if (!event.has(net.minecraft.world.entity.EntityType.PLAYER, StaminaAttributes.MAX_STAMINA.get())) {
            event.add(net.minecraft.world.entity.EntityType.PLAYER, StaminaAttributes.MAX_STAMINA.get());
        }
        if (!event.has(net.minecraft.world.entity.EntityType.PLAYER, StaminaAttributes.SLOW_CLIMB_SPEED.get())) {
            event.add(net.minecraft.world.entity.EntityType.PLAYER, StaminaAttributes.SLOW_CLIMB_SPEED.get());
        }
    }

    private void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(StaminaCapability.class);
    }

    public void attachEntityCaps(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
        if (event.getObject() instanceof net.minecraft.world.entity.player.Player) {
            if (!event.getObject().getCapability(StaminaCapability.INSTANCE).isPresent()) {
                event.addCapability(new ResourceLocation(MODID, "stamina"), new StaminaCapability.Provider());
            }
        }
    }
}