package com.tacticalstamina.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class StaminaConfig {
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static final Client CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();

        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue enableStamina;

        public final ForgeConfigSpec.DoubleValue depletionSprint;
        public final ForgeConfigSpec.DoubleValue depletionJump;
        public final ForgeConfigSpec.DoubleValue depletionAttack;
        public final ForgeConfigSpec.DoubleValue depletionBlockBreak;
        public final ForgeConfigSpec.DoubleValue depletionSwim;
        public final ForgeConfigSpec.DoubleValue depletionClimb;

        public final ForgeConfigSpec.DoubleValue recoveryPerTick;
        public final ForgeConfigSpec.DoubleValue recoveryRestMult;
        public final ForgeConfigSpec.DoubleValue recoveryClimbMult;
        public final ForgeConfigSpec.IntValue recoveryDelay;

        public final ForgeConfigSpec.DoubleValue minMaxStamina;
        public final ForgeConfigSpec.DoubleValue fatigueThreshold; 

        public final ForgeConfigSpec.IntValue penaltyRecoveryDelay;
        public final ForgeConfigSpec.DoubleValue penaltyBaseRate; 
        public final ForgeConfigSpec.DoubleValue penaltyCurveFactor;
        
        public final ForgeConfigSpec.DoubleValue maxExertionPenalty;
        public final ForgeConfigSpec.DoubleValue maxHungerPenalty;
        public final ForgeConfigSpec.IntValue hungerPenaltyThreshold;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            enableStamina = builder.comment("Set to false to completely disable the stamina system").define("enableStamina", true);
            builder.pop();

            builder.push("Depletion Rates");
            depletionSprint = builder.comment("Stamina drained per tick while sprinting").defineInRange("depletionSprint", 0.15, 0.0, 100.0);
            depletionJump = builder.comment("Stamina drained per jump").defineInRange("depletionJump", 0.85, 0.0, 100.0);
            depletionAttack = builder.comment("Stamina drained per attack").defineInRange("depletionAttack", 3.45, 0.0, 100.0);
            depletionBlockBreak = builder.comment("Stamina drained per block broken").defineInRange("depletionBlockBreak", 1.1, 0.0, 100.0);
            depletionSwim = builder.comment("Stamina drained per tick while swimming").defineInRange("depletionSwim", 0.05, 0.0, 100.0);
            depletionClimb = builder.comment("Stamina drained per tick while climbing").defineInRange("depletionClimb", 0.7, 0.0, 100.0);
            builder.pop();

            builder.push("Recovery Settings");
            recoveryPerTick = builder.comment("Stamina recovered per tick").defineInRange("recoveryPerTick", 0.36, 0.0, 100.0);
            recoveryRestMult = builder.comment("Multiplier for recovery when standing completely still").defineInRange("recoveryRestMult", 1.45, 1.0, 10.0);
            recoveryClimbMult = builder.comment("Multiplier for recovery when hanging on a ladder/vine (not moving)").defineInRange("recoveryClimbMult", 0.2, 0.0, 10.0);
            recoveryDelay = builder.comment("Ticks before stamina starts regenerating after action (20 ticks = 1 sec)").defineInRange("recoveryDelay", 50, 0, 2000);
            builder.pop();

            builder.push("Fatigue & Limits");
            minMaxStamina = builder.comment("The absolute floor for Max Stamina (Stamina bar cannot shrink smaller than this)").defineInRange("minMaxStamina", 10.0, 1.0, 100.0);
            fatigueThreshold = builder.comment("Percentage of Max Stamina where fatigue penalty starts (0.25 = 25%)").defineInRange("fatigueThreshold", 0.25, 0.0, 1.0);
            penaltyRecoveryDelay = builder.comment("Ticks to wait after leaving red zone before penalty recovers").defineInRange("penaltyRecoveryDelay", 100, 0, 2000);
            penaltyBaseRate = builder.comment("Base rate for exponential penalty increase").defineInRange("penaltyBaseRate", 0.02, 0.0, 10.0);
            penaltyCurveFactor = builder.comment("Divisor for exponential curve (Lower = Steeper curve)").defineInRange("penaltyCurveFactor", 150.0, 1.0, 1000.0);
            
            maxExertionPenalty = builder.comment("Maximum reduction to Max Stamina caused by physical exhaustion").defineInRange("maxExertionPenalty", 30.0, 0.0, 100.0);
            maxHungerPenalty = builder.comment("Maximum reduction to Max Stamina caused by starvation").defineInRange("maxHungerPenalty", 30.0, 0.0, 100.0);
            hungerPenaltyThreshold = builder.comment("Food level at which stamina penalty begins (6 = 3 shanks)").defineInRange("hungerPenaltyThreshold", 6, 0, 20);
            builder.pop();
        }
    }

    public static class Client {
        public final ForgeConfigSpec.IntValue barXOffset;
        public final ForgeConfigSpec.IntValue barYOffset;
        public final ForgeConfigSpec.IntValue barWidth;
        public final ForgeConfigSpec.IntValue barHeight;
        public final ForgeConfigSpec.IntValue colorBackground;
        public final ForgeConfigSpec.IntValue colorSafe;
        public final ForgeConfigSpec.IntValue colorCritical;
        public final ForgeConfigSpec.IntValue colorTireless;
        public final ForgeConfigSpec.IntValue colorStripes;
        public final ForgeConfigSpec.IntValue colorPenaltyHunger;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("HUD Layout");
            barXOffset = builder.comment("Horizontal offset from center").defineInRange("barXOffset", 0, -1000, 1000);
            barYOffset = builder.comment("Vertical offset from bottom").defineInRange("barYOffset", 24, 0, 1000);
            barWidth = builder.comment("Width of the bar in pixels").defineInRange("barWidth", 180, 1, 1000);
            barHeight = builder.comment("Height of the bar in pixels").defineInRange("barHeight", 2, 1, 100);
            builder.pop();

            builder.push("Colors");
            colorBackground = builder.defineInRange("colorBackground", 2236962, 0, 16777215);
            colorSafe = builder.defineInRange("colorSafe", 65280, 0, 16777215);
            colorCritical = builder.defineInRange("colorCritical", 16711680, 0, 16777215);
            colorTireless = builder.defineInRange("colorTireless", 65450, 0, 16777215);
            colorStripes = builder.defineInRange("colorStripes", 16711680, 0, 16777215);
            
            colorPenaltyHunger = builder.comment("Color for Hunger penalty stripes").defineInRange("colorPenaltyHunger", 16763904, 0, 16777215);
            builder.pop();
        }
    }
}