# Phase 7Q — Runtime Validation Matrix

## Test artifact and environment

Source commit: `6dcdc38 Restore Arcane Purpur`  
Artifact: `build/libs/charm-8.8.23.jar`  
SHA-256: `7B97F3789149DCD3D22772ABED2E2A9D62B440E936BDD5D76FD55957BF65A143`  
Minecraft: 1.21.10  
Fabric Loader: 0.19.3  
Fabric API: 0.138.4+1.21.10  
Java: 21  
Charmony: 1.44.4  
Charmony API: 1.26.15  

Use a fresh Prism Launcher instance with only Fabric API, Charmony API,
Charmony, and this Charm JAR. Install the JARs in the instance `mods` folder;
do not initially include the full Mythas pack. Create a fresh creative test
world with cheats enabled. Disable `keepInventory` for death tests. Every row
below starts as `NOT TESTED`; compilation and startup never count as gameplay
PASS. Record screenshots for failures involving inventories, rendering,
teleportation, or persistence and attach the relevant `latest.log` excerpt.

Result fields for execution: **Actual**, **Result** (`PASS`, `FAIL`,
`BLOCKED`, `NOT TESTED`), **Log**, and **Release-blocking**.

## Runtime blocker resolution — Aerial Affinity registry key

During the first world-load attempt, registry decoding failed because the
enchantment definition referenced `charm:player.aerial_mining_speed`, while
Phase 7F had registered the attribute through `CommonRegistry`. That helper
uses Charmony's `charmony` namespace for registry IDs, so no attribute existed
under the `charm` key required by the Charm data resource.

The fix registers the Aerial Affinity attribute with the existing
constructor-time `Registerable` lifecycle under `charm:player.aerial_mining_speed`
and creates the matching `charm:aerial_affinity` enchantment resource key. No
mining-speed hook, feature toggle, or unrelated feature was changed.

Validation evidence:

* `compileJava`: PASS.
* Dev client title-screen initialization: PASS.
* Dev client quick-play world load reached server spawn preparation without the
  previous unknown-attribute error: PASS for the registry-decoding blocker.
* The same world load still reports pre-existing unrelated data errors (legacy
  woodcutting/Chiseled Bookshelf resource issues) and later crashes in an
  unrelated Villager Attracting path; those are not attributed to Aerial
  Affinity and were not changed in this blocker fix.
* Aerial Affinity gameplay row Q2-06 remains `NOT TESTED`; startup/world-load
  success is not gameplay validation.

## Runtime blocker resolution — Animal Armor player-tick crash

The first world-load attempt after the Aerial fix crashed while ticking an
ordinary `ServerPlayer`. The Phase 7G `LivingEntity#getItemBySlot` mixin cast
every living entity to `Mob` before checking its type. Minecraft 1.21.10
enchantment effect iteration queries `EquipmentSlot.BODY` during each living
entity tick, so the unconditional cast raised a `ClassCastException` before
the Horse/Wolf guard could run.

The hook now pattern-matches the `LivingEntity` to Horse and Wolf first and
returns only their native BODY equipment for `BODY`. Players,
generic living entities, and all non-Horse/Wolf entities retain vanilla slot
lookup. The feature toggle still gates the hook; no supported enchantment set
or equipment tags changed.

Follow-up review found that `Mob#getBodyArmorItem()` itself delegates back to
`LivingEntity#getItemBySlot(BODY)`. The final repair therefore reads the native
`LivingEntity.equipment` container directly after the Horse/Wolf type check;
the mixin no longer calls `getBodyArmorItem`, `getSlot`, or `getItemBySlot`.
Horse and Wolf use the same native BODY equipment storage, independently
verified by the type branches.

Validation evidence:

* `compileJava`: PASS.
* Integrated-server quick-play world load: PASS; player joined and remained in
  the world for the observation window without the Animal Armor exception.
* Player-tick crash: RESOLVED.
* Save/load and extended ticking after the direct equipment access repair:
  PASS; no recursive equipment call, null equipment field, or player-tick
  exception appeared.
* Full Animal Armor enchanting/effect, config-disabled, and multiplayer rows
  remain `NOT TESTED` and are not promoted by this blocker repair.
* Existing unrelated data/resource parse errors remain in the dev log and are
  tracked outside this isolated fix.

## Phase 7Q.3 — known resource-loading cleanup

Baseline: `d0b4dcc Fix Animal Armor equipment recursion`.

### Kiln Firing recipes

All 33 `charm:firing` recipe resources were audited with strict JSON parsing.
27 were invalid and 6 were initially valid. The shared defect was a literal
trailing `+` token after the closing JSON object (the runtime reported the
failure near line 8). Removing that token from the 27 affected resources was
the complete repair; recipe type, ingredients, results, counts, experience,
and cooking times were not changed. Final result: 33/33 strict parses PASS.

The custom Firing serializer/schema was unchanged. Kiln processing, GUI,
automation, fuel, comparator, persistence, and transaction behavior remain
gameplay `NOT TESTED`.

### Copper Piston advancement

`charm:copper_pistons/obtained_copper_piston` was valid JSON but referenced the
missing historical parent `charm:core/automation`. The narrow namespace
migration changes only its parent to the existing `charmony:root`; criteria,
icon, and item IDs are unchanged. The advancement now has a resolvable parent.
Copper Piston gameplay remains `NOT TESTED`.

### Runtime gate

After the resource repairs, a fresh client resource reload/world-load must be
checked for the two targeted failures (`charm:kilns/firing/*` parse errors and
the Copper Piston advancement load error). Other pre-existing Charm resource
warnings are outside this focused phase and remain backlog items; they do not
promote Kiln or Copper Piston gameplay to PASS.

Static validation completed: 33/33 Firing files parse strictly and the
advancement parent resolves. The attempted compile/build/client gates were
blocked by a transient Loom dependency-resolution failure (`fabric-loom:1.11.8`
could not be downloaded after retries); the prior client log therefore remains
historical evidence only and is not counted as a post-repair runtime PASS.

## Recipe migration audit — validation blocked

Runtime validation exposed a repository-wide 1.21.10 recipe migration issue.
An audit of root and bundled Charmony module resources found 482 recipe JSON
files (434 root resources and 48 nested-module resources). All are strict JSON,
but codec compatibility is not yet complete:

* 308 `charm:woodcutting` recipes and 5 Charmony stonecutting recipes use
  legacy object-form single ingredients (`{"item": ...}` / `{"tag": ...}`).
* 70 shaped recipes use legacy object-form entries in `key`; 17 shapeless
  recipes use legacy object-form entries in `ingredients`. These include
  Charmony chest/storage, Arcane Purpur, Ender Pearl Block, Gunpowder Block,
  Sugar Block, Azalea/Ebony content, Copper Pistons, Kiln, and Woodcutter
  recipes.
* 63 Woodcutting files contain semantically invalid overworld substitutions:
  seven bad files each for Acacia, Birch, Cherry, Dark Oak, Jungle, Mangrove,
  Oak, Pale Oak, and Spruce. Overworld families must use log/wood forms;
  stem/hyphae terminology is limited to Crimson/Warped-style families.
* `charm:kilns/firing/cracked_mud_bricks.json` references the nonexistent
  `minecraft:cracked_mud_bricks`; its historical intended output requires
  source verification before changing or removing the recipe.
* 34 Woodcutting files use `charm:` Azalea/Ebony IDs even though the current
  registrations are `charmony:`; Pale Oak references to `charmony:pale_oak_*`
  likewise require migration to the actual vanilla IDs.

No repairs were applied in this audit. Kiln, Woodcutting, Arcane Purpur,
storage-block, and related recipe gameplay validation is `BLOCKED BY AUDIT`.

## Phase 7Q.4 — recipe migration repair

The complete migration was applied after the audit. Legacy object-form
ingredients were converted to the 1.21.10 string/tag representation across
400 files. The 63 generated overworld stem/hyphae templates were obsolete
duplicates once corrected to valid log/wood semantics, so they were removed;
the valid log/wood and stripped-log/wood recipes remain. Azalea and Ebony
Woodcutting IDs now use their verified `charmony:` registrations, and Pale Oak
uses vanilla `minecraft:pale_oak_*` IDs.

Historical source recovery found no current or repository registration for
`minecraft:cracked_mud_bricks` and no source-backed equivalent. Its Firing
recipe was therefore classified obsolete/superseded and removed rather than
inventing a Charm block or output.

Final static result: 418 remaining recipe files, all strict JSON-valid, with
zero legacy ingredient objects, zero invalid overworld stem/hyphae IDs, zero
stale Azalea/Ebony namespaces, zero incorrect Pale Oak plank/slab IDs, and no
cracked-mud-bricks references. This is a resource migration result only;
runtime recipe loading and gameplay remain pending external build/client
validation.

## P0 — data integrity and item persistence

| ID | Feature | Setup and exact steps | Expected | Actual / Result / Log | Blocking |
|---|---|---|---|---|---|
| Q0-01 | Suspicious Sand | Create item-filled Suspicious Sand with a distinctive component-rich stack; verify contents; make it fall, land, brush. Repeat after save/reload before falling and after landing. | Exact item, count, components, and name survive; no archaeology replacement, loss, or duplicate. | NOT TESTED | P0 |
| Q0-02 | Suspicious Gravel | Repeat Q0-01 with Suspicious Gravel, including stack count >1 and sequential falls. | Same persistence invariant. | NOT TESTED | P0 |
| Q0-03 | Totem grave | Disable keepInventory; die with armor, offhand, damaged/enchanted/component-rich items and full inventory; recover with another player. | One holder, exact contents, inventory-first recovery, safe overflow, no duplication/loss. | NOT TESTED | P0 |
| Q0-04 | Grave persistence | Save/reload before recovery; repeat independent deaths. | Holder and contents persist; each death creates one independent holder. | NOT TESTED | P0 |

## P1 — transactions, inventories, networking, and teleportation

| ID | Feature | Setup and exact steps | Expected | Actual / Result / Log | Blocking |
|---|---|---|---|---|---|
| Q1-01 | Woodcutter normal take | Insert one valid log, select recipe, take result. | Exact output; exactly one input consumed. | NOT TESTED | P1 |
| Q1-02 | Woodcutter quick-move | Shift-click one result, stacked input, partial/full inventory, repeat rapidly. | Atomic transfer; no input loss or duplicate output. | NOT TESTED | P1 |
| Q1-03 | Woodcutter invalidation | Change recipe, remove/replace input, break workstation, close/reopen. | Stale result unavailable; input safely returned; no ghost output. | NOT TESTED | P1 |
| Q1-04 | Woodcutter persistence | Save/reload and disconnect/reconnect with input/menu open. | Transient input has exactly one surviving copy. | NOT TESTED | P1 |
| Q1-05 | Lumberjack acquisition | Place Woodcutter beside unemployed adult villager; save/reload; break/reclaim site. | Claims/releases/reclaims POI normally; profession and offers persist. | NOT TESTED | P1 |
| Q1-06 | Lumberjack trades | Progress all five tiers; test saplings, bark/logs, ladders, barrels, chiseled bookshelves. | Historical offers, stable randomized values, normal restocking. | NOT TESTED | P1 |
| Q1-07 | Endermite Powder use | Test Overworld, Nether, End; use with and without nearby End City. | End-only behavior, historical consumption-before-failure, 40-tick cooldown, one locator on success. | NOT TESTED | P1 |
| Q1-08 | Locator lifecycle | Observe direction, particles, lifetime; save/reload and unload/reload chunk. | Server target X/Z persists and entity expires safely at 1,000 ticks. | NOT TESTED | P1 |
| Q1-09 | Arcane fallback | Feature disabled/no target/all targets obstructed; consume Chorus Fruit. | Vanilla random teleport unchanged. | NOT TESTED | P1 |
| Q1-10 | Arcane target | One/multiple valid targets, obstruction, 12-block boundary and beyond. | Nearest valid horizontal target; one teleport, one cooldown, one consumption. | NOT TESTED | P1 |
| Q1-11 | Generic-consumable regression | With Arcane enabled consume normal food, potion, milk, honey, suspicious stew. | Arcane hook has zero observable effect. | NOT TESTED | P1 |
| Q1-12 | Totem inventory | Activate from main hand, offhand, then inventory; test Emergency Swap. | Priority main hand → offhand → inventory; exactly one totem consumed. | NOT TESTED | P1 |
| Q1-13 | Kiln transaction | Process valid/invalid inputs, fuel, hoppers, comparator, shift-click, save/reload. | No loss/duplication; historical processing and automation. | NOT TESTED | P1 |
| Q1-14 | Storage persistence | Barrels, chests, trapped chests, chiseled bookshelves with hoppers/comparators and save/reload. | Exact contents and redstone behavior persist; variants do not cross-pair. | NOT TESTED | P1 |

## P2 — gameplay and parity content

| ID | Feature | Setup and exact steps | Expected | Actual / Result / Log | Blocking |
|---|---|---|---|---|---|
| Q2-01 | Copper Pistons | Normal/sticky, slime/honey, 12-block limit, quasi-connectivity, save/reload. | Historical restored piston behavior. | NOT TESTED | No |
| Q2-02 | Bookshelves | Oak, Pale Oak, Azalea, Ebony, Crimson/Warped: craft/place, enchanting, fuel/fire. | Correct behavior and resources. | NOT TESTED | No |
| Q2-03 | Ladders | Oak, Pale Oak, Azalea, Ebony, Crimson/Warped, Bamboo: orientation, support, climb, waterlog, fuel. | Vanilla-equivalent mechanics and material fire rules. | NOT TESTED | No |
| Q2-04 | Mooblooms | All 14 variants: spawn/render/persistence and representative heads/drop path. | Variant identity, texture, head matching, player-kill/Mob Drops rules. | NOT TESTED | No |
| Q2-05 | Coral Squids | All five variants: swim/flee, bucket, spawn egg, persistence, heads/drops. | Variant behavior and matching drops. | NOT TESTED | No |
| Q2-06 | Aerial Affinity | Boots only; airborne/grounded/submerged mining; enchant/book/anvil; disabled feature. | Level I and historical 5× airborne speed behavior. | NOT TESTED | No |
| Q2-07 | Animal Armor Enchanting | Horse, wolf, copper horse armor; representative supported enchantments and exclusions. | Correct applicability/effects; excluded enchantments remain excluded. | NOT TESTED | No |
| Q2-08 | Anvils Last Longer | Repeated enabled/disabled anvil uses and damage transitions. | Configured Charm probability versus vanilla 12% disabled behavior. | NOT TESTED | No |
| Q2-09 | Crafting From Inventory | Table in hand/offhand/inventory; press V; remove possession; disabled feature. | Correct 3×3 menu, invalidation, no duplication/loss. | NOT TESTED | P1 |
| Q2-10 | Suspicious Creating | Sand/gravel with multiple nearby item entities; brush; disabled feature. | One source selected/killed; stored stack preserved; archaeology suppressed. | NOT TESTED | P1 |
| Q2-11 | Villager Attracting | Emerald Block main hand/offhand/inventory-only; range and AI resumption. | Historical eligibility and behavior. | NOT TESTED | No |
| Q2-12 | Recipe Improvements | Craft representative restored recipes and test every relevant toggle. | Correct ingredients/output and no collisions. | NOT TESTED | No |
| Q2-13 | Glint Colors | Smithing workflow, output glint, persistence. | Recipe and expected color work. | NOT TESTED | No |
| Q2-14 | Item Frame Hiding | Hide/unhide server/client, particles, reconnect, save/reload. | State synchronizes; no unknown payload warnings. | NOT TESTED | P1 |

## P3 — coverage and polish

| ID | Feature | Exact test | Expected | Actual / Result / Log | Blocking |
|---|---|---|---|---|---|
| Q3-01 | Wood family coverage | Representative Oak, Pale Oak, Azalea, Ebony, Nether, Bamboo recipes across Woodcutting and storage. | Correct family outputs/resources. | NOT TESTED | No |
| Q3-02 | Client resources | Inspect all restored models, textures, translations, profession/entity renderers. | No Charm-owned missing-resource warnings. | NOT TESTED | No |
| Q3-03 | Vanilla regression | Vanilla Stonecutter, Kiln, professions, Chorus Fruit, ladders, bookshelves, chiseled bookshelves. | Vanilla behavior remains unchanged outside Charm conditions. | NOT TESTED | P1 |

## Execution policy

Run Q0 first. A duplication, destructive loss, save corruption, or crash
stops that feature’s testing and is recorded as a release blocker; do not
repair during the first pass. After each group, archive the world and inspect
`latest.log`. Rows remain NOT TESTED until manually executed in the isolated
Prism instance. This artifact is a **Phase 7Q Test Build**, not production
ready.

## Phase 7Q.8 — final visual defect repair

High-confidence repairs:

* Pale Oak Ladder alpha was restored using the established ladder transparency
  mask; it now has 112 transparent pixels like the other ladder variants.
* Pale Oak Bookshelf now retains the Pale Oak base palette with visible book
  pixels derived from the established Charm bookshelf structure.
* Historical chest/trapped-chest item transforms and the original Woodcutter
  assets remain in place from 7Q.7.

Source comparison confirmed Cherry Ladder is byte-identical to the original
Charm ladder texture, so it was not replaced. Coral Squid head geometry and
Moobloom head presentation remain port/runtime items requiring visual proof;
no historical head model exists to substitute. The Coral Squid spawn egg
continues to use the historical vanilla special spawn-egg model.

Six-slot Chiseled Bookshelf behavior and all gameplay systems were untouched.
Runtime visual verification remains required.

## Phase 7Q.7 — original Charm asset recovery audit

The uploaded `charm-fabric-1.21-7.0.28.jar` was inspected as the historical
visual source.

Source-backed repairs:

* restored the historical Woodcutter block geometry and original
  `woodcutter_bottom`, `woodcutter_side`, and `woodcutter_top` textures;
* restored historical chest and trapped-chest item display transforms for the
  13 historical wood families;
* adapted the same transforms to the approved Pale Oak variants;
* restored the five historical Coral Squid entity textures at the current
  renderer path `assets/charmony/textures/entity/coral_squid/*`;
* reconciled all 14 historical Moobloom textures with the current
  `charmony` namespace.

The historical JAR contains Bookshelf and Chiseled Bookshelf assets matching
the current port's model families. It contains no Shelf family and no Mud or
Cracked Mud Bricks content. Coral Squid and Moobloom are historical Charm
content; their current port namespace/path adaptation is now source-backed.
The Coral Squid spawn egg uses the historical vanilla
`minecraft:item/template_spawn_egg` model and has no standalone texture.

No entity, gameplay, recipe, networking, trade, POI, or Suspicious Block
behavior was changed. Runtime visual verification remains required.

## Phase 7Q.7 — original Charm asset recovery audit

The uploaded `charm-fabric-1.21-7.0.28.jar` was inspected as the historical
visual source.

Source-backed repairs:

* restored the historical Woodcutter block model geometry and its original
  `woodcutter_bottom`, `woodcutter_side`, and `woodcutter_top` textures;
* restored the historical chest and trapped-chest item model display
  transforms for the 13 historical wood families;
* adapted those transforms to the approved Pale Oak variants using the
  existing Pale Oak plank texture.

The historical JAR contains Bookshelf and Chiseled Bookshelf assets matching
the current port's model families. It contains no Shelf family. It contains no
Mud or Cracked Mud Bricks content. Coral Squid and Moobloom visual assets are
not historical Charm assets in this artifact and remain Mythas/port content.

No entity, gameplay, recipe, networking, trade, POI, or Suspicious Block
behavior was changed. Runtime visual verification remains required.

## Phase 7Q.6 — visual parity and resource repair

Confirmed high-confidence repair:

* Moobloom head texture assets were present only in the bundled module
  resource tree. The 14 original `assets/charmony/textures/entity/moobloom/*`
  textures are now also available in the main resource tree, matching the
  existing head-model references without changing entity code or drop logic.

The audit also confirmed:

* Woodcutter currently uses Stonecutter textures and has no recoverable
  original artwork in the repository; artwork remains runtime-uncertain.
* Chest inventory definitions intentionally use Minecraft's special
  `builtin/entity` chest path; incorrect flat rendering needs client proof
  and was not changed speculatively.
* Coral Squid spawn egg uses the vanilla `template_spawn_egg` special path;
  its appearance remains runtime-uncertain.
* No Shelf family is present in current or bundled source/resources; this
  remains an audit-only missing-content question.
* No current `cracked_mud_bricks` or Mud/Cracked Mud Bricks registration,
  recipe, model, or texture exists. No replacement content was added.

Visual gameplay validation remains UNTESTED. Suspicious Sand/Gravel falling
behavior remains the independent P0 and was not modified.

## Phase 7Q.5 — client resource and item-registry audit

The audit confirmed canonical wood registration names use `<wood>_chiseled_bookshelf`.
High-confidence repairs corrected all 14 Chiseled Bookshelf loot outputs and
random-sequence IDs, added 42 current 1.21.10 wood-variant item definitions,
added Copper Piston item definitions, and added item definitions for the
bundled Ender Pearl, Gunpowder, and Sugar storage blocks.

All changed JSON parses strictly. Model, blockstate, and texture references
were checked against root and bundled-module assets.

Classification: A (fixed) for the loot IDs and missing item-definition layer;
C (expected special paths) for `minecraft:builtin/entity` and
`minecraft:item/template_spawn_egg`; E (runtime proof required) for
Woodcutter artwork, chest special-item presentation, Coral Squid spawn-egg
appearance, and Moobloom-head texture warnings. Suspicious falling persistence
remains the independent P0 and was not changed.

Gameplay and visual validation remain UNTESTED; external compile/build and
client resource-reload validation are still required.
## Phase 7Q.9 — Runtime visual renderer repair (audit/update)

The current visual audit compared the port against the original Charm 1.21 artifact.

Confirmed resource repairs in this pass:

- Moobloom head block models now use block-atlas textures (`charmony:block/moobloom/*`) rather than entity-only texture paths. The 14 source textures are mirrored into the block texture directory; entity rendering remains unchanged.
- Pale Oak chiseled bookshelf side/top artwork was structurally corrected from blank plank-like textures to the chiseled bookshelf pattern, with a Pale Oak-tinted palette.

The following remain runtime-gated and were not changed speculatively:

- Custom chest block-entity rendering/inventory presentation (the port has a dedicated `CustomChestRenderer`; a live client check is still required to determine whether the material atlas/renderer path is failing).
- Coral Squid head geometry/rendering (port-created content; no historical head asset exists).
- Pale Oak bookshelf appearance and other previously validated wood-family resources.

Historical renderer comparison: Charm 1.21 used a dedicated chest block-entity renderer with per-material `Sheets.CHEST_SHEET` materials, not a flat block/item model. The port has the corresponding `CustomChestRenderer` and normal/trapped material registrations; placed-chest invisibility therefore remains a live-client/runtime item to isolate (no speculative renderer rewrite was made).

Historical Charm has no Coral Squid head assets; those heads are port-created content. Historical Moobloom assets are entity textures, while the port's head blocks require block-atlas textures; the repaired models now use a dedicated block-atlas copy to avoid black item/placed rendering.

No gameplay, recipe, entity, AI, trade, networking, or Suspicious Block behavior was modified.
## Phase 7Q.10 — Client renderer/model path audit

Static comparison against the historical Charm 1.21 artifact found no safe mapping-only rewrite to apply:

- Coral Squid spawn egg already resolves through Minecraft's `template_spawn_egg`; colors are supplied by the native `SpawnEggItem` path.
- Bookshelf and Chiseled Bookshelf item definitions resolve through their corresponding block models, matching the historical hierarchy.
- Custom chests use a dedicated `CustomChestRenderer` and `Sheets.CHEST_SHEET` materials, the same renderer/material architecture as historical Charm.
- Moobloom head models now use block-atlas texture paths; historical Charm did not contain a separate Moobloom-head model family.

Runtime screenshots/resource-reload logs are still required before changing any of these mappings. No gameplay or renderer code was changed in this audit step.
## Temporary Q10 client diagnostics

Client-only diagnostics were added (no assets or gameplay changes):

- custom chest renderer registration and first invocation log the block-entity type, chest type, material texture (via reflective API probe), and block;
- Charmony wood client boot logs the expected bookshelf/chiseled-bookshelf model paths for all 14 families;
- Moobloom client boot logs the block-atlas texture convention used by head models.

Remove these diagnostics after collecting the dev-client log/screenshots.
