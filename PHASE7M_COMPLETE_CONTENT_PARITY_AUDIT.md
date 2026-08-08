# Phase 7M — Complete Charm Content Parity Audit

## Scope and method

This audit supersedes Phase 7B for detailed content parity while retaining
Phase 7B as the historical top-level feature context. It compares the local
`charm-1.21.1.zip` source/resources, current registrations/resources, all phase
documents, and the current 1.21.10 startup/build evidence. No production code
or resource was changed.

The historical archive contains 78 feature-package directories: 77 user-facing
or supporting feature packages plus the shared `core` package. The Phase 7B
table has 75 top-level user-facing rows after folding renamed/support packages
into their parent systems. The authoritative top-level count for this audit is
therefore the 75-row table, not the stale “71” summary.

## Top-level status

| Status | Count |
|---|---:|
| PRESENT | 54 |
| PARTIAL | 1 (`Wood`) |
| MISSING | 19 |
| SUPERSEDED BY VANILLA | 0 |
| INTENTIONALLY OMITTED | 1 (`Item Stacking`) |
| MYTHAS DIVERGENCE | 1 (`Totem of Preserving` Grave Mode) |
| OBSOLETE / N/A | 0 |
| UNKNOWN | 0 |
| **Audited rows** | **75** |

The 19 missing rows are: Atlases, Bat Buckets, Beacons Heal Mobs, Beekeepers,
Colored Sea Lanterns, Doors Open Together, Echolocation,
Item Hover Sorting, Note Block Lower Pitch, Noteblocks,
Player Pressure Plates, Potion of Radiance, Raid Horns, Redstone Sand, Silence,
Smooth Glowstone, Suspicious Effect Improvements, Tooltip Improvements, and
Waypoints. Firing was reclassified PRESENT because its current recipe type and
serializer are registered and used by Kilns.

## Hierarchical content inventory

### Wood and workstation family

Wood is PARTIAL at the top level but its audited restored content includes 14
Barrels, 14 normal Chests, 14 Trapped Chests, 14 Bookshelves, 14 Chiseled
Bookshelves, 14 Ladders, Woodcutting (277 concrete recipes), Woodcutter
(block/menu/payloads), and Lumberjack (POI/profession/trades). The broader row
remains PARTIAL because the historical wood package contains additional
supporting registrations and the 14 Chiseled Bookshelf base models are currently
misnamed relative to their blockstates.

### Entities and heads

Current registered custom entities are 14 Moobloom variants and 5 Coral Squid
variants. Native Charm heads exist for all 14 Mooblooms and all 5 Coral Squids.
Renderers, spawn eggs, bucket/persistence paths, and head resources are present;
natural spawning and gameplay behavior remain runtime-untested. No additional
historical Charm entity package beyond these entities was found.

### Kiln/Firing

Kilns use the native Charm `charm:firing` recipe type and serializer. Firing is
not a separate missing production feature. Kiln processing resources are
present; hopper, comparator, XP, and save/reload behavior remain untested.

### Other restored systems

The current tree contains the restored Aerial Affinity, Animal Armor Enchanting,
Anvils Last Longer, Copper Pistons, Recipe Improvements, Storage Blocks,
Suspicious Block Creating, Totem of Preserving, and Wood family systems. Their
phase documents and the release backlog remain authoritative for runtime status.

## Variant audit

| Family | Historical | Current | Status |
|---|---:|---:|---|
| Mooblooms | 14 | 14 | PRESENT / runtime untested |
| Moobloom heads | 14 | 14 | PRESENT / runtime untested |
| Coral Squids | 5 | 5 | PRESENT / runtime untested |
| Coral Squid heads | 5 | 5 | PRESENT / runtime untested |
| Barrels | 13 historical + Pale Oak | 14 | PRESENT; categorical extension |
| Chests | historical wood families + Pale Oak | 14 | PRESENT; runtime untested |
| Trapped Chests | historical wood families + Pale Oak | 14 | PRESENT; runtime untested |
| Bookshelves | 13 + Pale Oak | 14 | PRESENT; runtime untested |
| Chiseled Bookshelves | 13 + Pale Oak | 14 | PARTIAL: 14 base model references unresolved |
| Ladders | 13 + Pale Oak | 14 | PRESENT; runtime untested |

No other unimplemented variant family was found in the current restored
registrations. Colored Sea Lanterns is a missing feature, not a partial variant
family.

## Resource and orphan findings

Current resource inventory contains 708 Java classes, 521 recipe JSON files,
144 loot JSON files, 129 tag JSON files, 814 model/blockstate/item JSON files,
and 418 image/audio/metadata assets across the root and nested modules.

The dedicated orphan scan found these release-relevant issues:

1. Fourteen Chiseled Bookshelf blockstates reference
   `charmony:block/chiseled_<family>_bookshelf`, while the corresponding base
   model files are named `<family>_chiseled_bookshelf.json`. Startup reports all
   14 missing base models. This is PARTIAL resource parity and is intentionally
   not repaired in this audit.
2. No missing registered Lumberjack tags or trade IDs were observed after the
   Lumberjack phase namespace correction.
3. No missing `charm:firing` recipe type/serializer references were observed.
4. Existing unrelated Moobloom texture warnings and historical resource
   warnings remain in startup logs; they are listed separately in the risk
   report.

No production recipe, loot, tag, config, or registry mutation was made.

## Client/network/config status

Custom payloads were found for the Woodcutter menu and are isolated from the
vanilla Stonecutter path. No additional historical payload family was found
outside the already audited Item Frame Hiding and Woodcutter systems.

Historical configuration options were cross-checked in the companion config
report. Item Stacking remains intentionally omitted; Totem of Preserving remains
the approved Mythas Grave Mode divergence; Pale Oak is only a categorical
semantic extension.

## Endermite Powder implementation status

The independent Endermite Powder gameplay system is now registered and built:
the item, exact Endermite-kill drop callback, End-only 1,500-block End City
locator use, server-owned locator entity, portal particles, launch sound, and
rare Wandering Trader offer are present. The two historical advancements remain
deferred because their parent `charm:block_of_ender_pearls/convert_silverfish`
is not present in the current content set. Interactive drops, locator lifecycle,
and trader tests remain in the release backlog.

## Endermite Powder deep audit

Historical Endermite Powder owns an item, a custom locator entity, a launch
sound, an Endermite-kill drop handler, a rare Wandering Trader offer, two
advancements, and a structure tag `charm:endermite_powder_located`. Killing an
Endermite creates an item stack of `random.nextInt(2 + LootingLevel)`; this is
server-side, permits zero, and is not explicitly player-kill gated in the
historical event handler. Looting increases the upper bound. The item is usable
only in the End, has a 40-tick cooldown, consumes one item outside creative,
and locates the nearest tagged End City structure within 1500 blocks. It
spawns a short-lived server entity that travels toward the structure while
emitting portal particles; the entity persists target X/Z and expires after
1000 ticks. The item is sold by a rare Wandering Trader for 20 Emeralds in
stacks of 3. Its non-Arcane use is End City location and the trader offer.

## Arcane Purpur deep audit

Historical Arcane Purpur owns six blocks/items: Arcane Purpur block, slab,
stairs, glyph block, Chiseled Arcane Purpur block, and Chiseled Arcane Purpur
glyph block, plus loot tables, crafting/stonecutting resources, a
`charm:chorus_teleports` block tag, an advancement, client item-tab placement,
and a Chorus Fruit mixin. Chiseled Arcane Purpur is the teleport trigger.

When Chorus Fruit finishes using, the server scans a cube centered on the
entity's block position using configurable range clamped to 0--64 (historical
default 12). It considers tagged blocks, rejects the current position and any
candidate whose two blocks above are not air, chooses the minimum horizontal
distance candidate, and calls `randomTeleport` at the centered position above
the block. A successful teleport uses vanilla chorus particles/sounds, applies
a 20-tick player cooldown, consumes one Chorus Fruit outside creative, and
triggers the advancement. It is same-dimension only; failed lookup,
obstruction, zero range, or failed teleport leaves vanilla use intact. Multiple
blocks compete by nearest horizontal distance; ties are map-order dependent.
The mixin is server-authoritative for the decision but hooks the common
`finishUsingItem` path, so client prediction and multiplayer behavior require
runtime validation.

Both systems are MISSING and must not be implemented during Phase 7M.

## Validation state

Aggregate build is required to remain passing. Runtime remains UNTESTED for
most restored gameplay systems. Duplication/item-loss-sensitive systems receive
release-critical status in `PHASE7M_RELEASE_RISK_AND_VALIDATION.md`.
