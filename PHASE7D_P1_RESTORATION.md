# Phase 7D — P1 Restoration Planning and First Restoration Batch

## Scope and baseline

This phase used `PHASE7B_MASTER_PARITY_AUDIT.md` as the authoritative roadmap.
The open P0 Suspicious Block Falling Item Persistence blocker was not changed
or closed. No P2 feature restoration was started.

## Section A — Complete P1 roadmap

The audit's named P1 list is the twelve-item list in its priority section (not
an inference from individual table-row inconsistencies):

| Order/group | Feature | Category / historical source | Current state/assets | Size | Dependencies | 1.21.10 risk | Recommended grouping |
|---:|---|---|---|---|---|---|---|
| 1 | Storage Blocks remaining scope | Storage; `feature/storage_blocks` | Wood storage is present; compact Ender Pearl, Gunpowder, Sugar blocks were missing; historical assets recoverable | MEDIUM | Charmony registration/resources | FallingBlock codecs, item/block tags, Silverfish AI access | **First batch; three compact blocks together** |
| 2 | Recipe Improvements | Crafting; `feature/recipe_improvements` | Partial conditional/compact recipe infrastructure; historical convenience recipes missing | LARGE | Conditional recipe manager; content features | Recipe schema/result components and reload synchronization | Dedicated recipe audit/restoration |
| 3 | Aerial Affinity | Enchanting; `feature/aerial_affinity` | No current feature or assets | SMALL/MEDIUM | Enchantment registry; break-speed hook | 1.21.10 mining-speed/event/mapping changes | Standalone enchantment phase |
| 4 | Animal Armor Enchanting | Enchanting; `feature/animal_armor_enchanting` | No current feature or tags/assets | MEDIUM | Enchantment tags and animal equipment APIs | Item-component/enchantment validation and animal armor slots | Pair with Aerial only after separate audits |
| 5 | Anvils Last Longer | Anvils; `feature/anvils_last_longer` | No current feature/assets | SMALL | Vanilla anvil behavior | Anvil durability fields and mixin target | Standalone anvil phase |
| 6 | Item Stacking | Inventory/QoL; `feature/item_stacking` | No current feature/assets | LARGE/HIGH RISK | Item stack limits/components | Duplication, container sync, component limits | Dedicated safety-first phase |
| 7 | Kilns | Blocks/food; `feature/kilns` | No current feature/assets; historical block, BE, menu, renderer, recipes recoverable | LARGE | Menus, recipes, client screen/rendering | 1.21.10 menu, recipe-book, block-entity APIs | Dedicated kiln phase |
| 8 | Copper Pistons | Redstone; `feature/copper_pistons` | No current registrations/assets | LARGE | Block properties, piston behavior, redstone | Piston movement API and redstone semantics | Dedicated redstone phase |
| 9 | Lumberjacks | Villagers; `feature/lumberjacks` | No current villager/job/assets | MEDIUM/LARGE | Villager professions/trades | POI/profession and AI changes | Group with Woodcutters only after audit |
| 10 | Woodcutters | Villagers; `feature/woodcutters` | No current villager/job/assets | MEDIUM/LARGE | Wood materials and villager trades | POI/profession/trade mappings | Group with Lumberjacks |
| 11 | Woodcutting | Utility/recipes; `feature/woodcutting` | No current implementation/assets | MEDIUM | Wood families and recipe system | Recipe/tag/axe APIs | Pair with villager wood jobs only if source confirms shared content |
| 12 | Arcane Purpur | Blocks/teleport; `feature/arcane_purpur` | No current blocks/assets; historical six-block family and teleport logic recoverable | LARGE | Endermite Powder, teleport, chorus handling | Block renderers, teleport and component APIs | Dedicated Arcane Purpur phase after dependencies |

Recommended sequence is foundational registration/content first, then recipe
infrastructure, isolated enchant/anvil systems, high-risk inventory behavior,
large menu/block systems, redstone, and finally villager/wood and Arcane Purpur
systems. The order intentionally avoids grouping unrelated large features.

## Section B — First restoration batch: Storage Blocks remaining scope

### Historical behavior

Historical Charm `feature/storage_blocks` contains three compact blocks:

* **Ender Pearl Block:** nine ender pearls craft the block and the block breaks
  back into nine pearls (Silk Touch returns the block). It emits portal
  particles. When enabled, a Silverfish can burrow into an adjacent block and
  convert to an Endermite.
* **Gunpowder Block:** nine gunpowder craft the falling block; it breaks back
  into gunpowder and dissolves when touching lava. A configurable TNT recipe
  uses the block and sand.
* **Sugar Block:** nine sugar craft the falling block; it breaks back into sugar
  and dissolves when touching water.

Historical source locations:

* `feature/storage_blocks/StorageBlocks.java`
* `feature/storage_blocks/ender_pearl_block/common/*`
* `feature/storage_blocks/gunpowder_block/common/*`
* `feature/storage_blocks/sugar_block/common/*`

### Previous current-port state

The current port already restored 14-family barrels, normal chests, and trapped
chests, but searches found no compact storage-block registrations, classes,
recipes, loot tables, models, textures, or translations. This made the
historical Storage Blocks row partial despite the wood-storage work.

### Implementation architecture

The batch adds one common Charmony `StorageBlocks` feature with shared
registration and three native block implementations:

* `EnderPearlStorageBlock` extends `Block` and emits the historical portal
  particles.
* `GunpowderStorageBlock` and `SugarStorageBlock` share a falling/dissolving
  base, use 1.21.10 `MapCodec`s, retain falling behavior, and react to lava or
  water respectively.
* A feature-scoped Silverfish goal is installed through the existing
  `EntityTickCallback`; it uses an access-widened `Mob.goalSelector` and the
  1.21.10 `EntitySpawnReason.CONVERSION` path for Endermite conversion.
* The client sided feature inserts all three items into the Functional Blocks
  creative tab using Charmony's `ClientRegistry`.

This reuses the existing Charmony registry/config architecture and does not
introduce a new module or runtime dependency.

### Configuration

The feature is enabled by default and can be disabled through the Charmony
feature config. The historical toggles are retained as config fields:

* Ender Pearl Block converts Silverfish (default true).
* TNT from Gunpowder Block and Sand (default true).

The registered content is feature-scoped; the config architecture remains the
source of truth for enabling/disabling behavior. Runtime toggle validation is
still pending.

### Resources

Recovered historical Charm resources were reused under the current `charmony`
namespace:

* three blockstates;
* three block models and three item models;
* three historical block textures;
* block loot tables with Silk Touch/explosion behavior;
* compact-block recipes and inverse recipes;
* TNT recipe;
* English names and dissolve subtitles.

The historical recipes already use the 1.21.10 `result.id` form. JSON namespace
references were changed only from `charm:` to `charmony:`.

### 1.21.10 migrations

* Blocks use keyed `BlockBehaviour.Properties` and `MapCodec` constructors.
* Falling blocks implement the required `getDustColor` method.
* Neighbor hooks use the 1.21.10 `Orientation` signature.
* Silverfish conversion uses `EntitySpawnReason.CONVERSION` and modern entity
  positioning methods.
* Creative entries use Fabric's current `ItemGroupEvents` path.
* `Mob.goalSelector` is widened narrowly for the historical goal insertion;
  no unrelated entity fields were changed.

### Validation

| Check | Result |
|---|---|
| Charmony compile | PASS — `./gradlew :charmony:compileJava` |
| Charmony build | PASS — `./gradlew :charmony:build` |
| Resource processing/access-widener validation | PASS as part of Charmony build |
| Aggregate build | PASS — `./gradlew build` |
| JSON/resource presence | PASS — all 3 registrations, models, recipes, loot, textures, and translations present |
| Dev-client startup | UNTESTED in this batch |
| Placement/crafting/dissolve/Silverfish conversion | UNTESTED; add to release backlog |
| Config disable/TNT recipe toggle | UNTESTED; add to release backlog |

The P0 Suspicious Block Falling Item Persistence blocker remains OPEN and is
unchanged by this batch.

### Files changed

Root documentation:

* `PHASE7B_MASTER_PARITY_AUDIT.md` — Storage Blocks row updated from PARTIAL to
  PRESENT / NEEDS VALIDATION.
* `PHASE7D_P1_RESTORATION.md`
* `RELEASE_VALIDATION_BACKLOG.md`

Charmony nested module:

* common/client initializer registration;
* `common/features/storage_blocks/StorageBlocks.java`;
* client `features/storage_blocks/StorageBlocks.java`;
* `charmony.accesswidener` goal-selector entry;
* blockstates, models, item models, textures, recipes, loot tables, and language
  entries for the three compact blocks.

No Tweaks, Mooblooms, chest/barrel, Totem, Coral Squid, or P2 feature source was
changed.

## Remaining manual tests

Add/retain the following in `RELEASE_VALIDATION_BACKLOG.md`:

* craft/place/break all three compact blocks and verify exact counts;
* Ender Pearl portal particles and Silverfish-to-Endermite conversion;
* Gunpowder gravity, lava dissolution, TNT recipe, and config-off behavior;
* Sugar gravity, water dissolution, and config-off behavior;
* Silk Touch, explosion decay, save/reload, hoppers/redstone adjacency where
  applicable, dedicated-server synchronization, and resource/model checks;
* the existing P0 suspicious-block falling persistence test.

