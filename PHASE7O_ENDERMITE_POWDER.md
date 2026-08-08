# Phase 7O — Endermite Powder

## Phase 7O.1 — 1.21.10 Architecture Proof

This phase is recovery and architecture only. No production Java or resource
files are changed. The historical reference is the official Charm Fabric
1.19.2 artifact (`charm-fabric-1.19.2-4.4.4.jar`), inspected from its classes
and bundled resources.

## Historical component map

| Component | Historical implementation | 1.21.10 status |
|---|---|---|
| Feature | `EndermitePowder` module | Create a common `SidedFeature` |
| Item | `EndermitePowderItem` | `Item.use` migration |
| Drop hook | `EntityDropItemsCallback.AFTER`; Endermite check | Existing Charmony `EntityKilledDropCallback` is the supported equivalent |
| Entity | `EndermitePowderEntity` | Dedicated `Entity` with current synced-data/value APIs |
| Renderer | Empty `EntityRenderer`; texture returns null | Dedicated no-op current `EntityRenderer` and client registration |
| Registration | Misc entity, 2×2, tracking 80, update 10 | `EntityType.Builder` with the same values |
| Trader | `ExtraWanderingTrades.registerRareItem(item, 3, 12)` | Dedicated listing through `CommonRegistry.wandererTrade`; custom listing required to preserve historical multiplier |
| Advancements | `obtain_endermite_powder`, `use_endermite_powder` | Inventory trigger is direct; use trigger maps to Charmony action advancement |
| Structure tag | `charm:endermite_powder_located` → `minecraft:end_city` | Current Charm namespace must be `charmony:endermite_powder_located` |
| Sound | `endermite_powder_launch` using `endermite_powder.ogg` | Current `CommonRegistry.sound` and `assets/charmony/sounds.json` |
| Item resources | generated model and `item/endermite_powder.png` | Adapt namespace and add 1.21.10 item definition |

No entity attributes, model layers, custom entity texture, owner reference, or
entity AI exist historically.

## Exact historical behavior

### Drops

The historical callback runs after an Endermite drops its normal loot. It is
server-side, checks only that the killed entity is an Endermite, and does not
require a player kill; environmental and non-player deaths therefore follow the
same callback path. It creates an item entity even when the calculated stack is
empty, matching the historical implementation.

`maxDrops` is a configurable integer defaulting to `2`. The hard-coded looting
boost is `0.3`. The exact formula is:

```text
base = random.nextInt(max(1, lootingLevel + 1))
bonus = 1 when random.nextFloat() < 0.3 × maxDrops, otherwise 0
count = base + bonus
```

Representative ranges/probabilities:

| Looting | Base values | Bonus condition | Final range |
|---:|---|---|---|
| 0 | `{0}` | 60% chance | 0–1 |
| 1 | `{0,1}` | 60% chance | 0–2 |
| 2 | `{0,1,2}` | 60% chance | 0–3 |
| 3 | `{0,1,2,3}` | 60% chance | 0–4 |

The normal Endermite loot path is preserved.

### Item use and failure ordering

Historical `use` performs these operations in this order:

1. Return pass immediately outside the End.
2. Apply a 40-tick item cooldown.
3. Consume one item unless the player has infinite materials.
4. On the client, play the launch sound and return success.
5. On the server, search for the nearest tagged structure within 1,500 blocks.
6. If no structure is found, return failure; the prior consumption/cooldown remain.
7. If found, create the locator at the player block position plus twice the
   player look vector horizontally and `blockY + 0.5` vertically.
8. Persist target X/Z in synchronized entity data, add the entity, and trigger
   the use advancement.

Thus invalid dimensions do not consume or cool down; a valid-End search failure
does consume and cool down, exactly as the historical code does.

### Locator tick behavior

The entity stores only target X/Z. On each tick it constructs a target vector
from the current block position to `(targetX, currentBlockY, targetZ)`,
normalizes it, and scales it by `0.2`. Position is then calculated as:

```text
x = currentBlockX + direction.x × ticks
y = currentBlockY + direction.y × ticks + ticks × 0.03
z = currentBlockZ + direction.z × ticks
```

The age counter increments once per tick. At `ticks > 1000`, the entity is
discarded and the counter is reset. There is no earlier distance-based removal,
collision handling, gravity, owner, or target re-search. Server ticks emit 18
portal particles per tick with the historical random offsets; clients do not
authoritatively emit them.

Target X/Z are synchronized through tracked integer data and persisted as
`targetX`/`targetZ`. Age is not persisted historically, so a reload resets the
counter. There is no owner or movement serialization.

### Trader offer

The historical rare wandering-trader offer is generated as:

| Cost | Result | Max uses | XP | Price multiplier | Pool |
|---:|---:|---:|---:|---:|---|
| 20 Emeralds | 3 Endermite Powder | 1 | 1 | 1.0 | Rare wandering-trader pool |

The item/count are fixed at offer generation; there is no transaction-time
reroll. Current `CommonRegistry.wandererTrade` is the injection lifecycle, but
its generic listing hard-codes a different price multiplier, so 7O.2 needs a
small dedicated `ItemListing` to preserve `1.0`.

### Advancements

`obtain_endermite_powder` is a task advancement with icon Endermite Powder,
toast enabled, chat announcement disabled, and an
`inventory_changed` criterion requiring the item. Its historical parent is
`charm:block_of_ender_pearls/convert_silverfish`, which is not present in the
current port; this parent must be either restored by the owning Phase 7P
content or explicitly deferred rather than referenced as a missing resource.

`use_endermite_powder` is a challenge advancement, parented to the obtain
advancement, with toast enabled, chat announcement disabled, and the custom
`action_performed` criterion for `charm:used_endermite_powder`. The current
Charmony action trigger can represent the criterion, but the missing historical
parent chain makes both advancement resources `DEFERRED` unless a parent
decision is approved.

## Current 1.21.10 API mapping

| Historical API | Current equivalent | Confidence |
|---|---|---|
| Fabric entity builder | `EntityType.Builder.of(...).sized(2,2).clientTrackingRange(80).updateInterval(10)` | HIGH |
| Tracked integers | `SynchedEntityData.defineId` with `EntityDataSerializers.INT` and `SynchedEntityData.Builder` | HIGH |
| NBT entity data | `ValueOutput.putInt` / `ValueInput.getIntOr` | HIGH (current Coral Squid pattern) |
| Item use | `Item.use(Level, Player, InteractionHand)` | HIGH |
| End check | `level.dimension().equals(Level.END)` | HIGH |
| Structure search | `ServerLevel.findNearestMapStructure(TagKey<Structure>, BlockPos, 1500, false)` | HIGH (current Piglin Pointing call site) |
| Cooldown | `Player.getCooldowns().addCooldown(item, 40)` | HIGH |
| Entity insertion | `ServerLevel.addFreshEntity` | HIGH |
| Server particles | `ServerLevel.sendParticles` | HIGH |
| Drop hook | Charmony `EntityKilledDropCallback` | HIGH |
| Rare trader insertion | `CommonRegistry.wandererTrade` | HIGH; listing multiplier requires custom class |
| Renderer | Client `ClientRegistry.entityRenderer` with current `EntityRenderer`/render-state path | MEDIUM; compile proof required in 7O.2 |
| Advancement use trigger | Charmony `Advancements.trigger` | HIGH |

The renderer is intentionally empty historically: it returns no texture and
does not render a model. The 1.21.10 implementation should preserve that
behavior with the narrowest current renderer class and no model layer.

## Structure tag and boundary

The historical tag contains exactly `minecraft:end_city`. The current resource
must use `data/charmony/tags/worldgen/structure/endermite_powder_located.json`
with that single value. The locator has no Arcane Purpur dependency and is
independent. No Arcane Purpur recipe, block, advancement parent, or resource is
to be created in 7O.2. The obtain-advancement parent chain is the only
identified deferred resource dependency.

## Resource plan for 7O.2

### Create

- `src/main/java/svenhjol/charm/common/features/endermite_powder/EndermitePowder.java`
- `.../common/EndermitePowderItem.java`
- `.../common/EndermitePowderEntity.java`
- `.../common/Registers.java`
- `.../common/Handlers.java`
- client feature/renderer registration under `src/main/java/svenhjol/charm/client/features/endermite_powder/`

### Modify

- `src/main/java/svenhjol/charm/common/CommonInitializer.java`
- `src/main/java/svenhjol/charm/client/ClientInitializer.java`
- `src/main/resources/assets/charmony/sounds.json`
- `src/main/resources/assets/charmony/lang/en_us.json`
- `PHASE7M_COMPLETE_CONTENT_PARITY_AUDIT.md`
- `PHASE7M_ORPHAN_AND_DEPENDENCY_REPORT.md`
- `RELEASE_VALIDATION_BACKLOG.md`
- `CHARM_1_21_10_RESTORATION_ROADMAP.md`

### Copy/adapt historical assets

- `assets/charm/textures/item/endermite_powder.png` → current Charmony item texture
- `assets/charm/sounds/endermite_powder.ogg` → current Charmony sounds directory
- historical generated item model → current item model plus 1.21.10 item definition
- structure tag → current Charmony worldgen structure tag

### Generate/adapt data

- two advancement JSONs only after the parent decision is resolved;
- no Arcane Purpur recipes or references in 7O.2.

## Safety review

- **Duplication/loss:** historical use consumes before search; preserve that exact
  ordering and make the server the only entity-spawn authority. Client sound is
  cosmetic only.
- **Search spam:** one synchronous search occurs per valid-End use; the native
  40-tick cooldown prevents immediate repeat use. The entity never searches per
  tick.
- **Orphan entities:** tracked target X/Z and a 1,000-tick cap ensure cleanup;
  invalid target data defaults to zero and expires normally.
- **Client manipulation:** target comes only from the server's structure search;
  client receives tracked data and cannot choose coordinates.
- **Chunk abuse:** the search is bounded to 1,500 blocks and only runs on item
  use; runtime validation must measure dedicated-server impact.
- **Trade economy:** one rare offer, fixed 20→3, one use, XP 1, multiplier 1.0;
  no transaction-time reroll.
- **Resources:** advancement parent absence is a known deferred dependency;
  7O.2 must not ship a dangling parent.
- **Dedicated server:** all item/entity logic is common; only the no-op renderer
  is client-side.

## 7O.2 incremental implementation order

1. Item, exact config, item model/definition, texture, translation, and sound.
2. Exact Charmony End City structure tag.
3. Endermite callback and verified drop formula.
4. Entity registration, tracked target data, persistence, and exact tick math.
5. Client no-op renderer registration; compile-gate the current render-state API.
6. Item-use dimension/search/spawn/consumption/cooldown ordering.
7. Portal particles and launch sound.
8. Custom rare trader listing preserving max uses, XP, and multiplier.
9. Advancement resources only after the historical parent is resolved or a
   documented parent decision is approved.
10. Static/resource validation, client startup, runtime matrix, and final docs.

Phase 7O.2 should have compile/build gates after steps 1–4, 5–8, and the final
resource/advancement pass.

## Phase 7O.2 — Implementation

The independent gameplay slice is implemented in small compile-gated steps.
`EndermitePowder` registers the Charmony item and exact configurable maximum
drop count (default 2). `Registers` installs the existing server-side
`EntityKilledDropCallback`; it preserves normal Endermite loot and applies the
recovered formula `nextInt(max(1, looting + 1))` plus one when
`nextFloat() < 0.3 * maxDrops`. There is no player-kill gate, matching the
historical callback.

The `charmony:endermite_powder_located` structure tag contains only
`minecraft:end_city`. `EndermitePowderItem.use` preserves historical ordering:
invalid dimensions return PASS without changes; in the End, cooldown 40 and
survival consumption happen before the synchronous 1,500-block tagged search.
Search failure therefore retains the historical consumed-item/cooldown result.
Successful search creates one server-owned locator at the historical launch
offset and never lets the client choose a target.

`EndermitePowderEntity` is registered as MISC with dimensions 2×2, tracking
range 80, and update interval 10. Target X/Z are synchronized entity data and
saved as `targetX`/`targetZ`; age is intentionally transient, as in historical
Charm. Its tick movement, 18 portal-particle emissions per tick, and >1000-tick
discard follow the recovered implementation. The client uses the current
1.21.10 render-state `EntityRenderer` path with the historical no-texture
renderer architecture.

The rare Wandering Trader offer is a custom listing with 20 Emeralds for 3
powder, max uses 1, XP 1, and multiplier 1.0, inserted only into the rare
pool. Launch sound and the recovered item texture/model are present. The two
historical advancements are intentionally **DEFERRED**: both depend on the
missing `charm:block_of_ender_pearls/convert_silverfish` parent, and no broken
or invented parent was introduced. Arcane Purpur recipes remain deferred to
Phase 7P.

Static validation: Java compile, aggregate build, resource processing, entity
renderer registration, tag registration, and client initialization passed.
Interactive drop, use, locator, persistence, trader, and advancement cases are
UNTESTED and remain release-critical backlog entries. The dev client reached
feature initialization; its only observed errors were external authentication/
Realms TLS failures and pre-existing Moobloom resource warnings, with no
Endermite Powder registry, renderer, tag, sound, or advancement errors.

## Phase 7O.1 status

Endermite Powder remains `MISSING`. No production files were modified in this
phase. Interactive behavior remains untested. Arcane Purpur remains deferred to
Phase 7P.
