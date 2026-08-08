# Phase 7K — Copper Pistons

## Historical behavior

Historical Charm documents and the 1.21.1 source describe Copper Pistons as
vanilla piston equivalents that do **not** have quasi-connectivity. Charm
provided exactly two base variants: Copper Piston and Sticky Copper Piston.
There were no exposed, weathered, oxidized, or waxed variants. Push limit,
sticky pulling, slime/honey attachment, redstone timing, neighbor updates,
entity displacement, and vanilla immovable-block rules were otherwise
unchanged. Charm did not add movable block entities; the vanilla piston block
entity and its restrictions remained authoritative.

The normal recipe uses three planks, two cobblestone, one redstone, and one
copper ingot. Sticky Copper Piston is crafted from a slime ball and a Copper
Piston. Both blocks use the historical Charm piston textures and loot.

## 1.21.10 architecture

The feature is a native root-Charm `SidedFeature` with four registered blocks:

* `charm:copper_piston`
* `charm:sticky_copper_piston`
* `charm:copper_piston_head`
* `charm:moving_copper_piston`

Two block items are registered and inserted into the Redstone creative tab.
The base, head, and moving blocks subclass the corresponding vanilla piston
classes. The vanilla `BlockEntityType.PISTON` is extended only with the custom
moving block, so piston movement and serialization continue through vanilla
machinery.

Three narrow common mixins preserve the historical distinction:

1. `PistonBaseBlockMixin` substitutes Charm head/moving states and returns no
   neighbor signal for Copper Pistons, suppressing quasi-connectivity.
2. `PistonHeadBlockMixin` returns the correct Copper or Sticky Copper item when
   a Charm head is cloned.
3. `PistonMovingBlockEntityMixin` uses the Charm head for collision handling
   during movement.

The feature is configuration-aware. When disabled, the mixin hooks leave
vanilla piston behavior unchanged and the feature registrations are not run.

## Resources and migration

Historical Charm blockstates, piston models, textures, recipes, loot, piston
tags, and recipe advancements were recovered. 1.21.10 registration uses
`Block.Properties.setId` and current `BlockItem` properties. No new copper
oxidation family was introduced because it was not part of historical Charm.

The 1.21.10 client piston renderer now uses render-state extraction rather
than the historical `render(PistonMovingBlockEntity, ...)` method. The common
implementation compiles and loads the custom moving block; renderer-specific
texture verification remains an explicit runtime test before claiming full
parity.

## Validation

* `./gradlew.bat :compileJava` — PASS
* `./gradlew.bat build` — PASS
* JSON/resource references — present for both recipes, blockstates, models,
  loot, tags, and translations
* Dev-client startup/resource initialization — PASS; the client reached normal
  resource-atlas initialization with no Copper Piston registry, model, or
  blockstate errors. Known authentication/Realms TLS warnings are unrelated
  development-environment failures.
* Interactive piston movement/redstone tests — UNTESTED

The following remain in `RELEASE_VALIDATION_BACKLOG.md`: all six-direction
movement, quasi-connectivity absence, 12-block and immovable-block limits,
slime/honey chains, rapid redstone pulses, save/reload while extended, and
client/server synchronization. The Phase 7C P0 blocker
`Suspicious Block Falling Item Persistence` remains OPEN.
