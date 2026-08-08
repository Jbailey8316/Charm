# Phase 7B — Master Charm Feature Parity Audit

## 1. Executive summary

This is an audit-only snapshot of the Minecraft 1.21.10 FullPort. No Java,
resource, registry, mixin, build, or configuration files were changed. The
archived Charm feature index describes more than 70 user-facing features; the
historical 1.21.1 source contains 78 feature directories. The current port
contains a substantial, buildable subset, but compilation is not evidence of
gameplay parity. Twenty-three archived features are absent, three are only
partial, and the remaining present features require runtime validation unless
an existing phase supplied sufficient evidence.

The archived feature index is the primary user-facing inventory ([Charm feature
index](https://charmony.work/charm-features.php)); the local historical source
checkout at `C:/Users/jbail/AppData/Local/Temp/charm-1.21.1-src/Charm-1.21.1-fabric`
was used to verify implementation names and source locations.

### Baseline

| Item | Result |
|---|---|
| Original reference | Charm 7.x / historical 1.21.1 Fabric source |
| Target | Minecraft 1.21.10, Fabric, Java 21 |
| Current root | `248ab3a` (`Restore Coral Squids and add heads`) |
| Modules audited | Charmony/core, Charmony API, Tweaks, Brew and Stew, Collection, Decor, Glint Colors, Mooblooms, Azalea Wood, Ebony Wood, root Coral Squids |
| Build | `./gradlew build` — **BUILD SUCCESSFUL** (baseline rerun before this document) |
| Production changes | None |

## 2. Inventory and counting method

Counts below are practical repository counts, not claims that generated/internal
registries equal user-facing features.

| Current-port category | Count |
|---|---:|
| Java source files | 644 |
| Resource recipes | 125 |
| Loot tables | 96 |
| Advancements | 87 |
| Tags | 116 |
| Textures/sounds/metadata (`png`, `ogg`, `mcmeta`) | 315 |
| Model/item JSON | 372 |
| Configurable feature definitions (`@FeatureDefinition`, common/client) | 87 sided declarations |
| Registered custom entity types | 3 (Chair, Moobloom, Coral Squid) |
| Entity variants | 14 Mooblooms; 5 Coral Squids |
| Native Charm mob heads | 19 (14 Moobloom, 5 Coral Squid) |
| Wood storage variants | 14 barrels, 14 normal chests, 14 trapped chests (42 total) |

The historical source contains 78 feature directories. Some are shared/core
or implementation helpers rather than separate user-facing rows; the master
table below follows the archived user-facing feature names and has 71 rows.

## 3. Master parity table

Status values are standardized as requested. `PRESENT` means an implementation
is found; `PARTIAL` means only a subset/renamed portion is present. Parity is
conservative: no feature is marked `PASS` solely because it compiles.

| Feature | Archived category | Original behavior summary | Historical source | Current module | Current status | Config toggle? | Assets/resources | Build | Runtime | Parity | Recommended action | Priority | Notes |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Aerial Affinity | Enchanting | Restore full mining speed while airborne | `feature/aerial_affinity` | Root Charm | PRESENT | yes | present | UNTESTED | UNTESTED | NEEDS VALIDATION | runtime enchantment matrix | P1 | Boots enchantment, level I; historical airborne speed hook restored |
| Animal Armor Enchanting | Enchanting | Enchant horse and wolf armor with the historical armor set | `animal_armor_enchanting` | Root Charm | PRESENT | yes | present | UNTESTED | UNTESTED | NEEDS VALIDATION | runtime animal-armor matrix | P1 | Horse, copper-horse, and wolf armor support restored |
| Animal Armor Grinding | Enchanting | Grind animal armor | `animal_armor_grinding` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Runtime evidence absent |
| Animal Damage Immunity | Mobs | Prevent configured animal damage | `animal_damage_immunity` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Verify exact exclusions |
| Animal Reviving | Mobs | Revive animals with the Charm mechanic | `animal_reviving` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Verify death/event edge cases |
| Anvils Last Longer | Anvils | Increase anvil durability | `anvils_last_longer` | Root Charm | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | runtime/statistical anvil matrix | P1 | 1.21.10 hook changes the vanilla 12% damage roll to configurable 50% by default; state transitions remain vanilla |
| Arcane Purpur | Blocks | Arcane Purpur block behavior | `arcane_purpur` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | P1 | Entire feature absent |
| Atlases | Client/rendering | Atlas/map-style client utility | `atlases` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | P2 | Historical client feature |
| Bat Buckets | Items/mobs | Capture bats in buckets | `bat_buckets` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | P2 | Entire feature absent |
| Beacons Heal Mobs | Utility | Beacons heal nearby mobs | `beacons_heal_mobs` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | P2 | Entire feature absent |
| Beekeepers | Villagers | Beekeeper villager behavior/content | `beekeepers` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | P2 | Entire feature absent |
| Campfires Heal Players | Utility | Campfires heal players | `campfires_heal_players` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Runtime test outstanding |
| Casks | Brewing/storage | Cask container and brewing | `casks` | Brew and Stew | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Phase 4B not fully interactive |
| Chairs | Decoration | Sit on chair blocks | `chairs` | Decor | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Entity interaction untested |
| Collection | QoL | Collection/player-facing utility features | `collection` | Collection | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | feature matrix | P2 | Audit individual subfeatures |
| Colored Sea Lanterns | Building | Coral-colored sea lantern variants | `coral_sea_lanterns` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | P2 | Entire feature absent |
| Compasses Show Position | Client/QoL | Show position on compass HUD | `compasses_show_position` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Client-only behavior |
| Cooking Pots | Food | Cooking-pot food processing | `cooking_pots` | Brew and Stew | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Recipes/container persistence |
| Copper Pistons | Redstone | Copper piston variants without quasi-connectivity | `copper_pistons` | Root Charm | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | validate movement/redstone | P1 | Normal and sticky variants restored; vanilla piston machinery reused; no oxidation/waxed variants in historical Charm |
| Coral Squids | Mobs/worldgen | Five coral variants near warm-ocean coral | `coral_squids` | Root Charm | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | runtime matrix | P2 | Phase 7A; all gameplay tests remain backlog |
| Crafting From Inventory | Crafting/QoL | Open 3x3 crafting while carrying a table | `crafting_from_inventory` | Tweaks | PRESENT | yes | present | PASS | PARTIAL | NEEDS VALIDATION | complete backlog | P2 | Phase 6A implementation; exploit tests pending |
| Crop Feather Falling | Farming | Crop fall-damage protection | `crop_feather_falling` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Verify crop behavior |
| Crop Replanting | Farming | Replant crops automatically | `crop_replanting` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Verify seeds/age rules |
| Deepslate Dungeons | Worldgen | Additional deepslate dungeon generation | `deepslate_dungeons` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | worldgen test | P2 | Generation evidence pending |
| Discs Stop Background Music | Client/audio | Music stops while discs play | `discs_stop_background_music` | Tweaks (`jukeboxes_stop_background_music`) | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | client test | P3 | Renamed implementation |
| Doors Open Together | Blocks | Paired doors open together | `doors_open_together` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | P2 | Entire feature absent |
| Echolocation | Mobs/audio | Echolocation-style entity/player aid | `echolocation` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | P2 | Verify exact historical semantics |
| Endermite Powder | Items | Endermite powder item/mechanic | `endermite_powder` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Firing | Utility | Historical firing utility mechanic | `firing` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Source-only discrepancy; audit before implementation |
| Glint Color Templates | Smithing | Smithing templates for glint colors | `glint_color_templates` | Glint Colors | PRESENT | yes | present | PASS | PARTIAL | NEEDS VALIDATION | smithing matrix | P2 | Phase 4C fixed output path; runtime matrix pending |
| Glint Coloring | Rendering | Color enchantment glints | `glint_coloring` | Glint Colors | PRESENT | yes | present | PASS | PARTIAL | NEEDS VALIDATION | rendering matrix | P2 | GUI/world/special models pending |
| Grindstone Disenchanting | Enchanting | Disenchant with configurable behavior | `grindstone_disenchanting` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Verify XP/item outcomes |
| Item Frame Hiding | Utility/rendering | Hide item-frame contents via Charm item | `item_frame_hiding` | Tweaks | PRESENT | yes | present | PASS | PARTIAL | NEEDS VALIDATION | multiplayer test | P2 | Phase 4C packet repair; visual test pending |
| Item Hover Sorting | Inventory/QoL | Sort/organize by hover interaction | `item_hover_sorting` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | P2 | Entire feature absent |
| Item Repairing | Items | Repair items through Charm utility | `item_repairing` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Verify component preservation |
| Item Restocking | Inventory/QoL | Restock depleted hotbar items | `item_restocking` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay/exploit test | P2 | Duplication-sensitive |
| Item Stacking | Inventory/QoL | Stack selected books, potions, and stews | `item_stacking` | — | INTENTIONALLY OMITTED | no (omitted) | historical only | n/a | n/a | OMIT | retain vanilla behavior | P1 | Mythas automation/world-safety decision: comparator, hopper, filter, storage, and unsafe oversized-stack disable risks; see `PHASE7I_ITEM_STACKING_AUDIT.md` |
| Item Tidying | Inventory/QoL | Tidy inventory stacks | `item_tidying` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay/exploit test | P2 | Inventory semantics |
| Kilns | Blocks/food | Kiln processing block | `kilns` | Root Charm + Firing | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | validate processing/automation | Historical custom firing recipe type; 100-tick cooking recipes restored |
| Lumberjacks | Villagers | Lumberjack villager/job behavior | `lumberjacks` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Mineshaft Improvements | Worldgen | Improve mineshaft generation | `mineshaft_improvements` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | worldgen test | P2 | Generation evidence pending |
| Mob Drops | Mob drops | Extra drops and configured mob rules | `mob_drops` | Tweaks | PRESENT | yes | present | PASS | PARTIAL | NEEDS VALIDATION | deterministic drop matrix | P2 | 19 native heads added; gameplay pending |
| Mob Textures | Client/rendering | Alternate/custom mob textures | `mob_textures` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | client matrix | P3 | Visual parity pending |
| Mooblooms | Mobs/worldgen | Fourteen flower Moobloom variants | `mooblooms` | Mooblooms | PRESENT | yes | present | PASS | PARTIAL | NEEDS VALIDATION | entity matrix | P2 | Variant heads are native additions |
| Nether Portal Blocks | Blocks | Nether portal block utility changes | `nether_portal_blocks` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | P2 | Verify portal edge cases |
| Note Block Lower Pitch | Redstone/audio | Lower note-block pitches | `note_block_lower_pitch` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Noteblocks | Redstone/audio | Expanded note-block behavior | `note_blocks` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Parrots Stay on Shoulder | Mobs/QoL | Keep parrots attached to shoulders | `parrots_stay_on_shoulder` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | Persistence pending |
| Path Converting | Villagers/world | Convert path blocks | `path_converting` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | Verify tool/shape rules |
| Piglin Pointing | Mobs | Piglin pointing behavior | `piglin_pointing` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | AI interaction pending |
| Pigs Find Mushrooms | Mobs | Pigs locate mushrooms | `pigs_find_mushrooms` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | AI interaction pending |
| Player Pressure Plates | Redstone | Player-only pressure plates | `player_pressure_plates` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Potion of Radiance | Items/effects | Radiance potion/effect | `potion_of_radiance` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Raid Horns | Audio/raids | Horn feedback for raids | `raid_horns` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Recipe Improvements | Crafting | Historical recipe conveniences | `recipe_improvements` | Root Charm + Charmony conditional recipes | PRESENT | yes | conditional | UNTESTED | UNTESTED | NEEDS VALIDATION | runtime recipe matrix | P1 | Twelve recipes and recipe-unlocking behavior restored; leather bundle is vanilla-superseded |
| Redstone Sand | Redstone | Redstone-triggered sand mechanic | `redstone_sand` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Repair Cost Unlimited | Anvils | Remove repair-cost ceiling | `repair_cost_unlimited` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | exploit test | Anvil safety pending |
| Repair Cost Visible | UI/anvils | Show repair cost | `repair_cost_visible` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | UI test | Client rendering pending |
| Respawn Anchors Work Everywhere | Utility | Use anchors outside Nether | `respawn_anchors_work_everywhere` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | Explosion/world rules pending |
| Shulker Box Transferring | Inventory/QoL | Transfer shulker contents | `shulker_box_transferring` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | exploit test | Duplication-sensitive |
| Silence | Mobs | Silence named mobs | `silence` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Smooth Glowstone | Blocks | Smooth glowstone variant/recipe | `smooth_glowstone` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Spawners Drop Items | Blocks | Spawners drop themselves/items | `spawners_drop_items` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | survival test | High-value block behavior |
| Spyglass Scope Hiding | Client/rendering | Hide/reduce spyglass scope overlay | `spyglass_scope_hiding` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | client test | Rendering pending |
| Storage Blocks | Storage/building | Wood storage families plus compact Ender Pearl, Gunpowder, and Sugar blocks | `storage_blocks` | Charmony | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | validate restored blocks | P1 | 14-family barrels/chests/trapped chests and the three historical compact blocks are now registered; gameplay remains untested |
| Suspicious Block Creating | Archaeology | Piston inserts an ItemStack into suspicious sand/gravel | `suspicious_block_creating` | Tweaks | PRESENT | yes | present | PASS | PARTIAL | NEEDS VALIDATION | resolve blocker | P0 | `Suspicious Block Falling Item Persistence` remains a release blocker |
| Suspicious Effect Improvements | Archaeology | Improve suspicious-effect interactions | `suspicious_effect_improvements` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Tooltip Improvements | UI/QoL | Improve item tooltips | `tooltip_improvements` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Torchflowers Emit Light | Blocks | Torchflower light emission | `torchflowers_emit_light` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | gameplay test | Light level/rendering pending |
| Totems Work From Inventory | Inventory/QoL | Totem of Undying activates from inventory | `totems_work_from_inventory` | Tweaks | PRESENT | yes | present | PASS | PARTIAL | NEEDS VALIDATION | death matrix | Phase 6B; interactive tests pending |
| Totem of Preserving | Totems | Preserve death inventory in a recoverable holder | `totem_of_preserving` | Totem of Preserving | PRESENT | Mythas | present | PASS | PARTIAL | INTENTIONAL MYTHAS DIVERGENCE | validate release matrix | P1 | Grave Mode/no repair is deliberate; see §6 |
| Trade Improvements | Villagers | Trade/merchant improvements | `trade_improvements` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | trading matrix | P2 | Current wandering-trader tiers are reorganized |
| Villager Attracting | Villagers | Loved-tag item attracts villagers | `villager_attracting` | Tweaks | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | AI matrix | P2 | Emerald Block tag behavior untested |
| Waypoints | Navigation | Waypoint/position utility | `waypoints` | — | MISSING | historical | absent | n/a | UNTESTED | NEEDS RESTORATION | restore | Entire feature absent |
| Wood | Building/storage | Variant wood blocks and families | `wood` | Charmony, Azalea, Ebony | PARTIAL | yes | present | PASS | PARTIAL | NEEDS VALIDATION | finish family audit | P2 | Pale Oak is intentional semantic addition |
| Woodcutters | Villagers | Woodcutter workstation/menu behavior | `woodcutters` | Root Charm | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | complete runtime matrix | P1 | Dedicated transient workstation/menu restored; Lumberjack profession remains separate |
| Woodcutting | Utility | Woodcutting utility/recipes | `woodcutting` | Root Charm | PRESENT | yes | present | PASS | UNTESTED | NEEDS VALIDATION | validate recipe/menu matrix | P1 | Custom recipe type and dedicated Woodcutter menu restored; 277 concrete recipes including Pale Oak |

### Table totals

Of 71 archived user-facing rows: **PRESENT 45**, **PARTIAL 1**, **MISSING 23**,
**INTENTIONALLY OMITTED 1**, and **INTENTIONALLY MODIFIED 1**. Parity is
**NEEDS VALIDATION 43**, **NEEDS RESTORATION 26**, **OMIT 1**,
**INTENTIONAL MYTHAS DIVERGENCE 1**; no row is
marked `NEEDS FIX`, `PASS`, `OMIT`, or `UNKNOWN` at this audit gate. This is
deliberately conservative: the absence of a `PASS` is not a claim that every
present implementation is broken.

## 4. Discrepancies and current-only content

### A. Archive features missing from the current port

The 23 `MISSING` rows above are confirmed by searching feature classes,
registrations, resources, config, mixins, translations, recipes, loot, tags,
and assets. Highest-risk missing systems are Arcane Purpur,
Lumberjacks and remaining high-impact storage scope. Smaller but
still user-facing omissions include Atlases, Bat
Buckets, Beacons Heal Mobs, Beekeepers, Colored Sea Lanterns, Doors Open
Together, Echolocation, Endermite Powder, Firing, Item Hover Sorting, Item
Stacking, Note Blocks, Player Pressure Plates, Potion of Radiance, Raid Horns,
Redstone Sand, Silence, Smooth Glowstone, Suspicious Effect Improvements,
Tooltip Improvements, and Waypoints.

### B. Intentional Mythas omission: Item Stacking

Item Stacking is intentionally omitted from the Mythas Charm build. Vanilla
stack limits remain unchanged because the historical overrides would alter
hopper throughput, comparator thresholds, item filters, overflow protection,
storage capacity, and Crafter behavior. More importantly, disabling altered
limits after oversized stacks exist can fail strict ItemStack decoding and
requires a migration system that Charm does not provide. The complete audit and
future approval tests remain in `PHASE7I_ITEM_STACKING_AUDIT.md`.

### C. Historical-source features not obvious in the archive/current list

`core`, `firing`, `woodcutting`, and several client/helper directories are
source-level registrations or implementation groupings rather than additional
site rows. They are retained in the discrepancy inventory so they are not
silently lost. `storage_blocks` is a source feature whose implementation was
restored in Phase 7D; `recipe_improvements` is now implemented through the
root feature and Charmony conditional recipes.

### D. Current-port features not represented as exact historical names

Current-only/reorganized directories include `crafting_table_nearby`,
`compact_recipes`, `wandering_trader_tiers`, `burning_has_reduced_view_blocking`,
`chiseled_bookshelves_show_book_on_hover`, `maps_show_when_hovering`,
`shields_have_reduced_view_blocking`, `shulker_box_menu_colors`,
`shulker_boxes_show_contents_when_hovering`, `telemetry`, `hud_item_scaling`,
`tint_background`, and `test_feature`. Some are likely implementation splits
or later QoL additions; `crafting_table_nearby` is a parity-risk name because
Phase 6A restored the historical inventory-based behavior. They should not be
counted as original features without a source/archive citation.

### E. Renamed/reorganized features

`discs_stop_background_music` is implemented as
`jukeboxes_stop_background_music`; glint templates/coloring are split into
two current modules; historical recipe improvements are split between
`conditional_recipes` and `compact_recipes`; historical wood/storage is now
distributed among Charmony, Azalea, and Ebony. These are counted against the
historical row, not as new features.

## 5. Intentional Mythas divergences and semantic additions

These are not accidental parity failures:

* **Totem of Preserving:** Grave Mode is the default; no prerequisite totem is
  required; one independent holder is created per death; full inventory,
  armor, and offhand ItemStacks/components are preserved; recovery is any-player,
  single-use, non-durable, and has no Echo Shard repair. Void-safe/protected
  holder behavior is intentional. Normal Charm behavior remains documented in
  `PHASE6C_TOTEM_OF_PRESERVING.md`.
* **Echo Shards:** only Totem-specific repair use was removed. Vanilla Echo
  Shards remain ordinary usable items. Mythas Coin/economy is out of scope.
* **Pale Oak storage:** Pale Oak barrels, normal chests, and trapped chests are
  semantic 1.21.10 additions to Charm's “all vanilla wood families” promise;
  historical Charm predates Pale Oak.
* **Mob heads:** Moobloom and Coral Squid heads are native Charm blocks/items.
  Vanilla Tweaks is only a behavioral rate reference, never a runtime asset or
  dependency.

## 6. Mob/entity parity

Historical Charm's major custom entities include Chairs, Mooblooms, and Coral
Squids. The current port has all three registered. Mooblooms have 14 flower
variants and 14 local variant heads. Coral Squids have five synchronized,
persisted variants (Tube, Brain, Bubble, Fire, Horn), a bucket, spawn egg,
warm-ocean/coral spawning, historical coral drops, and five local directional
heads. The entity implementation and resources build, but Phase 7A records all
interactive spawn/render/bucket/drop/head tests as UNTESTED. Therefore neither
entity receives PASS.

No historical babies were found for Coral Squids; no baby state was added.
Moobloom behavior was preserved and the head feature does not alter spawning,
AI, pollination, breeding, shearing, or normal loot.

## 7. Block/item/storage parity

Current storage coverage is exactly 14 families × 3 forms: barrel, normal
chest, trapped chest. Pairing, renderer, recipes, tags, loot, and Pale Oak
resources are present and build-clean. Interactive single/double chest,
trapped-redstone, hopper, comparator, save/reload, and cross-pair tests remain
backlog items. Historical storage scope is broader than these restored forms;
the remaining Storage Blocks/woodcutting audit must not be inferred from the
presence of these 42 registrations.

Other current native additions include casks, cooking pots, glint templates,
Moobloom heads, Coral Squid heads, Azalea/Ebony wood families, and the Mythas
Totem holder. Each must be included in final release testing rather than
counted as parity solely from resource presence.

## 8. Worldgen, recipes, resources, and tags

Current resources contain practical counts shown in §2 and are processed by
the aggregate build. That validates JSON/resource processing, not semantic
recipe/loot/worldgen parity. Worldgen features currently represented include
deepslate dungeons, mineshaft improvements, Moobloom/Coral Squid spawn data,
and wood/biome integration. Missing historical world/biome systems include
the omitted feature rows such as Colored Sea
Lanterns, Redstone Sand, and other absent registrations.

The resource audit found no production edits in this phase. A follow-up should
compare every registered item/block/entity against translations, models,
recipes, loot tables, tags, and advancements by registry ID; also compare
orphaned historical assets in the recovered source against current resources.

## 9. Configuration parity

The current port has 87 sided feature declarations across common/client code.
Present features generally retain a feature toggle, but this audit did not
promote any toggle to PASS without runtime evidence. The following require
explicit follow-up: prove that toggles disable the actual injected behavior,
identify orphaned toggles for omitted historical features, and identify active
current-only features without a historical toggle. The Mythas Totem toggle
and Mob Drops toggle are especially important because they gate inventory/death
mutation and custom drops.

## 10. Runtime validation cross-reference

Existing phase documents and `RELEASE_VALIDATION_BACKLOG.md` were treated as
the runtime authority. Static/build checks remain separate from gameplay checks.
The backlog covers storage, crafting exploits, totem inventory/recovery,
Suspicious Block Creating, Villager Attracting, and Coral Squids. The explicit
release blocker is retained:

**RELEASE BLOCKER — Suspicious Block Falling Item Persistence**

No current phase document establishes that an item-filled falling suspicious
sand/gravel block transfers its BrushableBlockEntity payload safely through
1.21.10 falling-block behavior. This remains P0 until manually proven or fixed.

The remaining backlog entries are UNTESTED/PARTIALLY VALIDATED manual coverage,
not additional confirmed defects.

## 11. Vanilla Tweaks head-rate verification

The closest behavioral reference is Squid for Coral Squid and Cow/Mooshroom for
Moobloom. The available Vanilla Tweaks rate table mirror records Squid at 5%
base plus 1 percentage point per Looting level (8% at Looting III), with a
player-kill requirement ([rate table discussion](https://www.reddit.com/r/HermitCraft/comments/g1t9hj)).
This matches the Phase 7A implementation: **Coral Squid 5% +1%/Looting,
player kill required — VERIFIED for the selected reference, runtime still
UNTESTED**.

The existing Phase 5A implementation records the Cow/normal-Mooshroom rule as
**1.0% +0.1 percentage point per Looting level** (1.3% at Looting III), player
kill required, and uses one probability roll in the shared Mob Drops provider.
That is the correct Moobloom behavioral mapping for this audit: **VERIFIED
against the established Cow/Mooshroom reference and source formula; runtime
drop matrix still UNTESTED**. No production rates were changed in Phase 7B.

## 12. Release blockers and priority counts

| Priority | Count | Issues |
|---|---:|---|
| P0 | 1 | Suspicious Block Falling Item Persistence |
| P1 | 9 | High-impact systems (Arcane Purpur, Lumberjacks, Storage scope, Woodcutters) plus Aerial Affinity, Animal Armor Enchanting, Anvils Last Longer, and Recipe Improvements validation; Item Stacking is intentionally omitted |
| P2 | 23 | Remaining missing features and present-but-unvalidated gameplay/content parity |
| P3 | 3 | Client/audio/polish discrepancies and renamed/reorganized feature validation |

These priority counts are roadmap issue groups, not a second feature count;
one issue can cover several tightly related rows.

## 13. Recommended remaining phases

1. **P0 runtime safety:** manually validate/fix Suspicious Block Falling Item
   Persistence, then run the full death/inventory/storage exploit matrix.
2. **P1 missing foundations:** validate Recipe Improvements and restore Storage remaining
   scope and the high-impact item/block systems (Anvils Last Longer is now
   restored and requires runtime validation; next is Arcane Purpur,
   Item Stacking is intentionally omitted for server safety; next are
   Lumberjacks/Woodcutters/Woodcutting). Keep each large system in a focused
   phase with parity audit first.
3. **P2 gameplay matrix:** validate existing modules and entities (storage,
   crafting, totems, Mob Drops, Mooblooms, Coral Squids, Villager Attracting,
   worldgen, Glint Colors), then address confirmed fixes before adding smaller
   missing features.

After those phases, restore the remaining small omitted systems (Atlases,
Colored Sea Lanterns, Doors Open Together, Echolocation, Endermite Powder,
Item Hover Sorting, Note Blocks, Potion/Raid/Silence/Smooth Glowstone,
Suspicious Effects, Tooltip Improvements, Waypoints, etc.) in grouped,
dependency-ordered batches. A release claim should wait for runtime validation
of every feature marked NEEDS VALIDATION and closure of the P0 blocker.

## 14. Production-readiness estimate

The port is **build-ready but not feature-parity-ready**. The aggregate build,
resource processing, and several focused runtime smoke tests are clean. The
23 missing archived features, three partial systems, one intentional omission,
broad untested gameplay
matrix, and one explicit P0 persistence blocker prevent a release-complete
claim. The next milestone is a safe, runtime-validated P0/P1 baseline rather
than another broad mechanical rewrite.
