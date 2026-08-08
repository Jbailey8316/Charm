# Phase 5A: Moobloom Head Drops

## Scope and baseline finding

This phase adds Moobloom heads without changing Moobloom spawning, AI, breeding, pollination, shearing, flower planting, rendering, or normal entity loot.

The recovered Charm/Charmony baseline does not contain Cow or Mooshroom head providers, head loot tables, or head-drop percentages. The Tweaks `MobDrops` feature is the existing Vanilla Tweaks-derived extra-drop system: it receives `EntityKilledDropCallback`, iterates registered `DropProvider` implementations, and spawns any returned extra stack independently of the entity's normal loot. Phase 5A extends that system rather than adding death logic to the Moobloom entity.

## Implementation

`MoobloomHeadDrops` is registered in the existing `MobDrops.Registers.drops` list. Consequently, it is active only when the existing common `MobDrops` feature is enabled; no standalone configuration option was added.

The provider:

- recognizes the registered `charmony:moobloom` entity type without introducing a build dependency from Tweaks to the Mooblooms module;
- requires `DamageSource.getEntity()` to be a `ServerPlayer`, matching the player-kill eligibility of Vanilla Tweaks More Mob Heads;
- calculates the drop probability as `0.01 + EnchantmentsHelper.lootingLevel(source) * 0.001`;
- adds a vanilla `minecraft:player_head` as an extra drop, leaving the Moobloom's normal drops untouched;
- sets a stable resolved `minecraft:profile` component with a Moobloom texture hosted by Mojang's texture service;
- sets the display name to `Moobloom Head`.

The probability is therefore:

| Looting level | Drop chance |
| ---: | ---: |
| 0 | 1.0% |
| 1 | 1.1% |
| 2 | 1.2% |
| 3 | 1.3% |

This matches the requested Vanilla Tweaks Cow/normal Mooshroom rate of one percent plus 0.1 percentage point per Looting level.

## Files changed

Tweaks:

- `modules/charmony-tweaks/src/main/java/svenhjol/charmony/tweaks/common/features/mob_drops/Registers.java`
- `modules/charmony-tweaks/src/main/java/svenhjol/charmony/tweaks/common/features/mob_drops/mobs/MoobloomHeadDrops.java`

Documentation:

- `PHASE5A_MOOBLOOM_HEADS.md`

## Validation

`./gradlew :charmony-tweaks:compileJava`

- Result: **BUILD SUCCESSFUL**.

For deterministic runtime validation without hundreds of kills, set a debugger breakpoint immediately before the random comparison in `MoobloomHeadDrops.dropWhenKilled`, evaluate the calculated chance for Looting levels 0 through 3, and force the sampled random value once below and once equal to the boundary. Verify that only the below-boundary case produces one named, textured player head, while the normal Moobloom loot is still present. Repeat once with a non-player damage source to verify no head is produced. This exercises the production code without adding a test-only gameplay path or configuration.

Runtime validation remains required for the displayed head texture, placed-head appearance, player-kill restriction, Looting behavior, and coexistence with every Moobloom variant's normal drops.

## Build results

`./gradlew :charmony-tweaks:build`

- Result: **BUILD SUCCESSFUL** (18 tasks; compilation, resources, remapping, access-widener validation, checks, and assembly completed).

`./gradlew build`

- Result: **BUILD SUCCESSFUL** (92 tasks).
