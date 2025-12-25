# Peak Stamina

Replaces vanilla sprinting mechanics with a resource management system based on fatigue and hunger.

## Features 
* **Action Costs:** Stamina is drained by sprinting, jumping, attacking, mining, swimming, and climbing.
* **Fatigue:** Dropping below 25% stamina accumulates "Fatigue," which temporarily reduces your maximum stamina cap.
* **Climbing:** Ladders consume stamina. Holding **Shift** allows for a "Slow Climb" (30% speed) that costs no energy.
* **Hunger Penalty:** Low food levels reduce your maximum stamina cap.
* **HUD:** A custom, low-profile stamina bar with visual indicators for fatigue and penalties.

![Preview of the stamina bar with hunger penalty and fatigue penalty applied](https://cdn.modrinth.com/data/cached_images/e42f52d0f09ffb6a271181fa78ff0bd48b192d7b.png)

## Configuration
All values are adjustable in `config/peak_stamina-common.toml`.

### General
* `enableStamina` (Default: `true`): Master switch for the mod.

### Action Depletion (Cost)
* `depletionSprint` (Default: `0.15`): Drain per tick while running.
* `depletionJump` (Default: `0.85`): Instant drain on jump.
* `depletionAttack` (Default: `3.45`): Instant drain on weapon swing.
* `depletionBlockBreak` (Default: `1.1`): Instant drain on block break.
* `depletionClimb` (Default: `0.7`): Drain per tick on ladders.
* `depletionSwim` (Default: `0.05`): Drain per tick in water.

### Recovery
* `recoveryPerTick` (Default: `0.36`): Base regeneration speed.
* `recoveryRestMult` (Default: `1.45`): Multiplier when standing still.
* `recoveryClimbMult` (Default: `0.2`): Multiplier when resting on a ladder.
* `recoveryDelay` (Default: `50`): Ticks before regen starts after an action.

### Penalties & Limits
* `fatigueThreshold` (Default: `0.25`): Stamina % where fatigue begins accumulating.
* `maxExertionPenalty` (Default: `30.0`): Max stamina lost due to fatigue.
* `maxHungerPenalty` (Default: `30.0`): Max stamina lost due to starvation.
* `hungerPenaltyThreshold` (Default: `6`): Food level (3 shanks) where penalties begin.
* `minMaxStamina` (Default: `10.0`): Hard floor for max stamina (bar cannot shrink below this).
* `penaltyRecoveryDelay` (Default: `100`): Ticks before fatigue heals after resting.

### Client Config (`peak_stamina-client.toml`)
* **Position:** `barXOffset`, `barYOffset`.
* **Size:** `barWidth`, `barHeight`.
* **Colors:** Full Hex code support for the bar, background, and penalty stripes.
