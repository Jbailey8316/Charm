# Phase 6D — Suspicious Block Creating

## Decision

KEEP/FIX. Historical Charm implements an archaeology-style item cache: a
piston pushes an item entity into Sand or Gravel, the block becomes Suspicious
Sand or Suspicious Gravel, and brushing later returns the supplied item rather
than unrelated archaeology loot.

## Implementation and fix

The 1.21.10 port preserves the historical piston path. On the server, the
piston mixin examines the block two positions in its facing direction and item
entities in the intervening block space. One eligible entity is selected at
random. The selected `ItemStack` is copied into the resulting
`BrushableBlockEntity.item`, its loot table is cleared, and the source
`ItemEntity` is killed exactly once. A successful conversion triggers the
existing advancement and sound. Sand and Gravel are the only conversions.

The copied stack is the complete modern `ItemStack`, so count, damage,
enchantments, names, lore, container contents, and custom components use
vanilla BrushableBlockEntity serialization. The conversion now calls
`BrushableBlockEntity.setChanged()` after assigning the item and clearing the
loot table, ensuring normal save/load dirty-state handling.

The entire selected stack is stored. A stack of 32 therefore becomes a stored
stack of 32; it is not reduced to one item. Other item entities remain in the
world when multiple entities are present, matching the historical random-one
selection rule.

## Brushing, breaking, and falling

Vanilla brushing progression remains unchanged. The explicit item is returned
once and the cleared loot table prevents desert-well/trail-ruins archaeology
loot replacement. Breaking without brushing follows vanilla suspicious-block
behavior; this phase does not turn the feature into protected storage.

Minecraft 1.21.10's `BrushableBlock.tick` creates a `FallingBlockEntity` with
the block state and disables its normal drop, while vanilla falling-block data
transfer is conditional on `FallingBlockEntity.blockData`. The brushable tick
does not populate that field, so persistence of an explicitly stored item
through gravity has not been proven and is a release blocker pending an
interactive falling test. No speculative custom falling-entity workaround was
added.

## Configuration and authority

The existing `SuspiciousBlockCreating` feature toggle remains authoritative.
With the feature disabled, the mixin's feature path is not run and vanilla
piston/Sand/Gravel behavior remains. Conversion and item mutation occur on the
server; the client cannot create a cache or consume an item independently.

## Validation

- Affected `:charmony-tweaks:build`: PASS.
- Aggregate `./gradlew build`: PASS.
- Client/mixin/resource smoke: PASS; no suspicious-block, brushable block
  entity, or serialization errors observed.
- Static inspection: PASS for complete ItemStack copying, stack-count
  preservation, loot-table suppression, one source-entity removal, and the
  dirty-state fix.
- Interactive piston, distinctive component-rich item, stack-of-32, save/
  reload, brushing, break, redstone clock, multiple-player, explosion, and
  config-on/off tests: UNTESTED because gameplay automation is unavailable.
- Falling Suspicious Sand/Gravel item persistence: **RELEASE BLOCKER —
  Suspicious Block Falling Item Persistence** until manually verified.

Known log noise is limited to external Mojang Realms/TLS connectivity
warnings; no Charm-specific errors were observed.
