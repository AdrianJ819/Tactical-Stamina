package com.tacticalstamina.registry;

import com.tacticalstamina.TacticalStaminaMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StaminaAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = 
        DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TacticalStaminaMod.MODID);

    public static final RegistryObject<Attribute> MAX_STAMINA = ATTRIBUTES.register("generic.max_stamina",
            () -> new RangedAttribute("attribute.name.tactical_stamina.max_stamina", 100.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> SLOW_CLIMB_SPEED = ATTRIBUTES.register("generic.slow_climb_speed",
            () -> new RangedAttribute("attribute.name.tactical_stamina.slow_climb_speed", 0.4D, 0.0D, 1.0D).setSyncable(true));
}