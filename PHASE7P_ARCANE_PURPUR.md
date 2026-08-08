# Phase 7P — Arcane Purpur

## Historical scope

The recovered Charm implementation contains six blocks/items:

1. `arcane_purpur_block` — decorative base block and recipe intermediate.
2. `arcane_purpur_slab` — slab variant.
3. `arcane_purpur_stairs` — stair variant.
4. `arcane_purpur_glyph_block` — decorative glyph block.
5. `chiseled_arcane_purpur_block` — valid Chorus Fruit teleport target.
6. `chiseled_arcane_purpur_glyph_block` — valid Chorus Fruit teleport target.

All use full-copy Purpur block properties and have no block entities. Recipes
are the historical shaped and stonecutting graph. The base recipe is eight
Arcane Purpur Blocks from eight Purpur Blocks plus one Endermite Powder;
glyphs, slabs, stairs, and chiseled variants retain their historical counts.

## Teleport implementation

The current port uses the server-authoritative `Item.finishUsingItem` hook on
generic `Item`, filtered immediately to `Items.CHORUS_FRUIT`. This is required
because 1.21.10 has no dedicated `ChorusFruitItem` class. When Arcane Purpur
is enabled, the handler scans a cube from `-range` through `+range` in all
three axes around the entity, with range clamped to 0–64 and defaulting to 12.
Only blocks in `charmony:chorus_teleports` are candidates. The current player
position is rejected; both blocks above a candidate must be air. Candidates
are ranked by horizontal squared distance, with historical map-order tie
behavior. The target is centered at X/Z +0.5 and Y at the block above the
target. Vanilla `randomTeleport(..., true)` performs the teleport and emits
the vanilla chorus particles. Vanilla chorus teleport sounds play at the
destination and on the entity.

After a successful teleport only, a ServerPlayer receives a 20-tick Chorus
Fruit cooldown and one Chorus Fruit is consumed outside creative. Failed scans,
zero range, obstruction, or failed random teleport return false from the hook,
so vanilla Chorus Fruit behavior continues unchanged. No cross-dimension
teleportation or client-selected target exists.

## Resources and registration

The six blocks/items, blockstates, block/item models, textures and texture
metadata, loot tables, recipes, translations, and the block tag are restored
under the `charmony` namespace. The tag contains only the two chiseled blocks.
No custom sound or persistent block entity is required.

The historical advancement is deferred because its parent depends on the
separately missing Endermite Powder advancement chain. No dangling advancement
JSON was added. The Block of Ender Pearls dependency remains outside this
phase.

## Static safety and performance

The scan runs only from the Chorus Fruit completion path, is bounded to the
configured cube (at most `(2 × range + 1)^3` block positions), and performs no
per-tick or global scan. Target selection, obstruction checks, teleport,
cooldown, and consumption are server-owned. A failed target cannot consume or
teleport; a successful path performs one teleport and one consumption at most.
Feature disable bypasses the hook and preserves vanilla behavior.

## Validation

- Java compile: PASS.
- Aggregate build: PASS.
- JSON/resource processing: PASS.
- Dev-client initialization: PASS; no Arcane Purpur registry, model, recipe,
  tag, mixin, or sound errors observed.
- Interactive block placement, crafting, Chorus Fruit fallback, target choice,
  obstruction, boundary, cooldown, consumption, save/reload, and multiplayer
  tests: UNTESTED; added to the release backlog.

Known unrelated startup output consists of authentication/Realms TLS errors
and pre-existing Moobloom resource warnings.
