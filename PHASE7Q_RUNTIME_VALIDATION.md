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
returns only their native `getBodyArmorItem()` stack for `BODY`. Players,
generic living entities, and all non-Horse/Wolf entities retain vanilla slot
lookup. The feature toggle still gates the hook; no supported enchantment set
or equipment tags changed.

Validation evidence:

* `compileJava`: PASS.
* Integrated-server quick-play world load: PASS; player joined and remained in
  the world for the observation window without the Animal Armor exception.
* Player-tick crash: RESOLVED.
* Full Animal Armor enchanting/effect, config-disabled, and multiplayer rows
  remain `NOT TESTED` and are not promoted by this blocker repair.
* Existing unrelated data/resource parse errors remain in the dev log and are
  tracked outside this isolated fix.

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
