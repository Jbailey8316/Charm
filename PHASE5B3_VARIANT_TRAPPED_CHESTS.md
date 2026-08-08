# Phase 5B.3: Variant Trapped Chests

## Architecture

This phase extends the Phase 5B.2 Charmony chest foundation. `CustomTrappedChestBlock` and `CustomTrappedChestBlockEntity` add only trapped-specific behavior; the shared `CustomChestRenderer`, render state, material registry, wood registration, and vanilla chest inventory/lid implementation are reused. The trapped block entity preserves vanilla open-count neighbor updates, while the block exposes vanilla trapped-chest signal/stat behavior.

Fourteen trapped block/item pairs are registered: Acacia, Bamboo, Birch, Cherry, Crimson, Dark Oak, Jungle, Mangrove, Oak, Pale Oak, Spruce, Warped, Azalea, and Ebony.

Pairing is restricted in both directions. A trapped chest connects only to the same material's trapped block. Normal and trapped blocks are different classes, so they cannot combine.

## Redstone behavior

The block follows the 1.21.10 `TrappedChestBlock` rules: it is a signal source, reports the clamped open count (0–15), exposes direct signal only upward, and updates neighbors above and below whenever the open count changes. The trapped chest statistic is `Stats.TRIGGER_TRAPPED_CHEST`. Inventory/comparator behavior remains inherited from `ChestBlockEntity`.

## Assets and data

Historical Charm single/left/right trapped textures were recovered for the 13 pre-existing families. Pale Oak trapped textures were derived from the historical Birch trapped texture geometry and remapped to the Phase 5B.2 Pale Oak palette. Each family has blockstate, block/item model and item definition, shapeless matching-normal-chest plus tripwire-hook recipe, custom-name-preserving loot table, translation, creative-tab entry, and convention/mineability/guarded-container tags. Trapped item definitions use Minecraft's trapped special-chest texture variant. Christmas special-item handling remains vanilla's date-based behavior.

## Validation

- `:charmony:compileJava`, Azalea/Ebony compilation, aggregate compilation: PASS.
- Aggregate `./gradlew build`: PASS.
- Resource processing/remapping completed for all 14 trapped registrations and 649 JSON resources were enumerated.
- The 1.21.10 client launched with all Charm/Charmony modules. No trapped-chest model, texture, block-entity, renderer, or registry errors appeared in the startup/reload log.
- Remaining log entries are the known development-environment authentication/TLS failures and unrelated pre-existing Moobloom texture warnings.

## Runtime status

GUI automation was unavailable, so the following interactive checks remain manual: representative Oak, Pale Oak, Crimson/Warped, Azalea, and Ebony placement/opening; storage and custom-name persistence; hopper input/output; comparator output; matching double trapped chests; cross-variant and normal-vs-trapped non-pairing; and redstone open/close, neighbor update, and double-chest power behavior. These are recorded in the release validation backlog and are not claimed as passed here.

## Follow-up

The next release validation should exercise both normal and trapped chest container behavior in an actual world before declaring storage parity complete. No further trapped-chest implementation is required unless those tests expose a runtime defect.
