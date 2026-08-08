# Phase 4B: Module Gameplay Validation

## Scope and method

- Date: 2026-08-07
- Minecraft: 1.21.10 Fabric development client
- World: existing Phase 4A world, `New World1`
- Original reference: recovered Minecraft 1.21.6 sources and generated default configuration
- Setup used normal in-game commands; no save files were edited.
- `PASS` means the stated behavior was directly observed, not merely that the game did not crash.
- `BLOCKED` means the behavior was not conclusively exercised in this first pass. It does not mean the feature is absent.

## Results

| Module | Feature tested | Expected original behavior | Observed 1.21.10 behavior | Result | Runtime warnings/errors | Follow-up required |
|---|---|---|---|---|---|---|
| Azalea Wood | Representative family registration, placement, and rendering | Logs, planks, slabs, stairs, fences, doors, trapdoors, signs, and hanging signs place and render as their vanilla wood equivalents. | All nine representative block IDs accepted placement and rendered with the expected Azalea models/textures. | PASS | None | Exercise player placement/orientation, doors, trapdoors, and editable sign screens in the focused block-interaction pass. |
| Azalea Wood | Recipes | Family recipes, including the hanging sign, load and craft the registered outputs. | The datapack loaded without parse errors and the recipe book unlocked the complete loaded recipe set. A manual crafting-grid execution was not completed. | BLOCKED | None | Craft planks, stairs, sign, and hanging sign manually. |
| Ebony Wood | Representative family registration, placement, and rendering | The Ebony wood family behaves like the original wood set. | Logs, planks, slabs, stairs, fences, doors, trapdoors, signs, and hanging signs placed and rendered. | PASS | None | Exercise interaction/orientation and sapling/tree growth. |
| Ebony Wood | Recipes | Family and corrected hanging-sign recipes load and produce the expected outputs. | Recipes loaded and unlocked without parse errors; manual crafting was not completed. | BLOCKED | None | Craft representative outputs manually. |
| Brew and Stew | Cask add/take behavior | A potion adds one bottle; an empty bottle retrieves the stored potion. | A healing potion changed the Cask from 0 to 1 bottle. A subsequent bottle interaction returned it to 0. | PASS | None | Test mixed effects, capacity, fermentation, dispensers, and dropped-block component retention. |
| Brew and Stew | Cask persistence | Bottle count/effects survive save and reload. | A Cask saved with `bottles: 1` reloaded with `bottles: 1`, fermentation `1.0`, and its block entity intact. | PASS | None | Longer fermentation/effect-duration validation remains. |
| Brew and Stew | Items and recipes | Cask, cooking pot, mixed stew, and relevant recipes remain available. | Cask and cooking pot registered and rendered; recipes loaded and unlocked. Cooking-pot food combination and manual crafting were not exercised. | BLOCKED | None | Test cooking-pot portions, mixed stew, food/effect handling, and recipe grids. |
| Collection | Collection enchantment | Breaking a block with a Collection-enchanted tool puts its drop directly into the player's inventory. | `/enchant` applied `charmony:collection`; breaking diamond ore in Survival put one diamond directly in inventory and left no nearby item entity. | PASS | None | Test full inventory overflow, multi-drop blocks, Silk Touch/Fortune, and container drops. |
| Decor | Chairs | Empty-hand use of a reachable bottom stair creates a chair and mounts the player. | Right-clicking an oak stair logged chair creation, mounted/moved the player, and produced a `charmony:chair` entity; dismount succeeded. | PASS | None | Test waterlogged stairs, blocked upper space, range checks, and save/reload while mounted. |
| Glint Colors | Template registration and smithing workflow | Template + enchanted item + colored dye produces the copied item with `charmony:glint_color`. | The template, Sharpness sword, and red dye occupied the three correct smithing slots, but the output remained empty. | FAIL | No warning was logged. | Diagnose the 1.21.10 SmithingTableEvents calculation hook/injection before further workflow testing. |
| Glint Colors | GUI/held rendering with known component data | Items carrying red, blue, or green glint components use their corresponding animated glint in GUI and held rendering without leaking color. | Command-created red sword, blue pickaxe, and green shield components loaded and rendered in inventory/hand without exception; the sword/pickaxe showed distinct tinted glints. Static inspection was not sufficient to validate every animated/special-model path. | PASS | None | After smithing is fixed, record animated GUI, dropped/in-world, armor, shield, and trident comparisons and color-leak tests. |
| Glint Colors | Multiple items and persistence | Per-item colors survive save/reload and remain independent. | Workflow-produced items could not be created because of the smithing failure. | BLOCKED | None | Repeat after the smithing output regression is fixed. |
| Mooblooms | Model/rendering | A flower-specific cow model and texture render correctly. | Pink-tulip Mooblooms rendered with the expected flower growth and custom texture/model. | PASS | None | Check every flower variant and baby model. |
| Mooblooms | AI/movement | Normal Mooblooms use cow-like AI and move normally. | A normal Moobloom moved from `[18.5,100,15.5]` to approximately `[16.76,100,16.51]`. | PASS | None | Longer navigation/tempt/panic checks remain. |
| Mooblooms | Persistence | Type and entity data survive save/reload. | A Moobloom selector matched after the world reload; earlier entity data exposed `Type`, `Pollinated`, health, and movement attributes. | PASS | None | Verify flower type and pollination values before/after reload on a named entity. |
| Mooblooms | Pollination, milking, shearing, flower planting, and breeding | Bees pollinate Mooblooms; a pollinated adult gives suspicious/mushroom stew, can be sheared, plants its flower, and breeds cow-like offspring. | Two controlled bowl attempts were inconclusive because the test target disappeared or could not be reselected; no stew was observed. Shearing, planting, and breeding were not completed. | BLOCKED | None attributable to the module | Repeat with isolated pens and naturally pollinated, named, persistent entities. |
| Totem of Preserving | Death capture | A clean Totem anywhere in inventory intercepts player drops and creates a holder containing them. | An actual Survival `/kill` created a holder at the death position. Its block entity contained exactly 7 diamonds and 5 gold ingots; the respawned inventory contained neither. | PASS | None | Test armor/offhand/ender integrations and non-command damage deaths. |
| Totem of Preserving | Persistence | Holder ownership, message, damage, and stored stacks survive save/reload. | All fields and both exact stacks were unchanged after a full save/exit/reload. | PASS | None | Test owner-only mode separately. |
| Totem of Preserving | Recovery/release/durability | Entering the holder returns a loaded Totem; using it releases contents and increments damage while leaving a clean Totem. | Entering removed the holder and returned a Totem whose component contained both stacks. Using it returned the diamond/gold to inventory and left a clean Totem with damage 1. | PASS | None | Test uses 2/3, destruction, echo-shard repair, lava/void placement, and full inventory. |

## Tweaks enabled-by-default inventory

The generated configuration contains 29 enabled common features and 14 enabled client features. `RespawnAnchorsWorkEverywhere` and `Telemetry` remain disabled by default and were not enabled for this pass.

| Module | Feature tested | Expected original behavior | Observed 1.21.10 behavior | Result | Runtime warnings/errors | Follow-up required |
|---|---|---|---|---|---|---|
| Tweaks | PathConverting | Shovel converts dirt to path; hoe converts path to dirt. | A shovel right-click changed the test dirt block to `minecraft:dirt_path`. | PASS | None | Test the reverse hoe conversion. |
| Tweaks | ItemRestocking | Consuming the selected hotbar stack pulls a matching inventory stack into that slot. | Placing the only selected stone refilled the selected item from the inventory stack; `SelectedItem` became stone count 10. | PASS | None | Test food, projectiles, durability, and no-match behavior. |
| Tweaks | CraftingTableNearby input | V opens the 3x3 grid when a crafting table is within four blocks. | Pressing V beside a table opened the expected 3x3 crafting screen. | PASS | None | Test range boundary and key conflicts. |
| Tweaks | TotemEmergencySwap input | Z moves a non-held inventory Totem of Undying to offhand. | With the totem in inventory slot 9 and neither hand holding one, Z left it in slot 9. | FAIL | None | Audit the 1.21.10 inventory-menu slot indices/click sequence and migrated input timing. |
| Tweaks | ItemFrameHiding and migrated particle provider | Amethyst hides a nonempty frame and client networking displays the custom particle. | The frame became `Invisible: 1b`, but the client rejected the feedback packet. | FAIL | `Unknown custom packet payload: charmony:add_amethyst_to_item_frame` | Register/route the S2C payload on the 1.21.10 client and retest particle appearance/removal. |
| Tweaks | TorchflowersEmitLight | Torchflowers emit configured light level 8. | Torchflower placement and feature initialization succeeded; light level was not measured conclusively. | BLOCKED | None | Measure client/server light at night. |
| Tweaks | ChiseledBookshelvesShowBookOnHover | Crosshair hover identifies the stored book. | Not exercised. | BLOCKED | None | Build a shelf with named/enchanted books and inspect every slot. |
| Tweaks | CampfiresHealPlayers | A visible lit campfire grants regeneration when no hostile mob is nearby. | Not exercised. | BLOCKED | None | Compare health ticks with and without nearby hostiles. |
| Tweaks | CropReplanting / CropFeatherFalling | Hoe-use harvests/replants mature crops; Feather Falling prevents trampling. | Not exercised. | BLOCKED | None | Create a mature crop plot and test both paths in Survival. |
| Tweaks | AnimalDamageImmunity / AnimalReviving / AnimalArmorGrinding | Tamed animals resist owner damage, named pets revive from a name tag + Totem, and armor grinds to material. | Not exercised. | BLOCKED | None | Use a controlled tamed-wolf/horse fixture. |
| Tweaks | GrindstoneDisenchanting / ItemRepairing / RepairCostUnlimited / RepairCostVisible | Grindstone extracts enchantments; extra repair materials and high-cost anvils work; repair cost appears. | Not exercised. | BLOCKED | None | Run controlled anvil/grindstone inventories with known component data. |
| Tweaks | TotemsWorkFromInventory | Vanilla Totem activates from any inventory slot. | Not exercised; the Totem of Preserving death test validates a separate provider path. | BLOCKED | None | Perform a lethal Survival test with a non-held vanilla totem. |
| Tweaks | ParrotsStayOnShoulder | Jump/fall keeps shoulder parrots; crouch dismounts them. | Not exercised. | BLOCKED | None | Tame and shoulder a parrot, then test jump, fall, damage, water, and crouch. |
| Tweaks | ShulkerBoxTransferring / menu color / hover contents | Inventory drag transfers into a box; GUI tint and hover contents match its color/data. | Not exercised. | BLOCKED | None | Test colored boxes with mixed contents in inventory and container screens. |
| Tweaks | ItemTidying input/UI | Apostrophe/button sorts supported inventories without item loss. | Feature and keybind initialized, but a controlled before/after inventory was not tested. | BLOCKED | None | Test player, chest, and mixed container sorting. |
| Tweaks | CompassesShowPosition / MapsShowWhenHovering | Compass overlay shows facing/XYZ/biome; map hover renders map. | Not exercised. | BLOCKED | None | Validate HUD and tooltip render-state paths. |
| Tweaks | Burning/Shield reduced view blocking / SpyglassScopeHiding | First-person overlays are repositioned/scaled and spyglass border is removed. | Migrated mixins applied, but visual gameplay comparisons were not completed. | BLOCKED | None | Capture before/reference comparisons for fire, shield, and spyglass. |
| Tweaks | SuspiciousBlockCreating | Piston pushes an item into sand/gravel, creating suspicious material with particles/data. | Not exercised. | BLOCKED | None | Build the piston fixture and brush the result. |
| Tweaks | NetherPortalBlocks | Tagged blocks such as crying obsidian form valid portal frames. | Not exercised. | BLOCKED | None | Build and ignite mixed crying-obsidian frames. |
| Tweaks | PiglinPointing / PigsFindMushrooms / VillagerAttracting | Mobs perform their item/structure-driven behaviors. | Not exercised. | BLOCKED | None | Use isolated AI fixtures and known nearby structures/blocks. |
| Tweaks | MobDrops / SpawnersDropItems | Configured mobs/spawners yield extra drops under their required conditions. | Not exercised. | BLOCKED | None | Run deterministic loot trials, including Peaceful spawner breaking. |
| Tweaks | TradeImprovements / WanderingTraderTiers | Extra villager/trader offers and tiers appear. | Not exercised. | BLOCKED | None | Spawn/profession-level traders and inventory all offers. |
| Tweaks | DeepslateDungeons / MineshaftImprovements / iron-chain lantern path | Structures receive the original material/decor/ore/minecart modifications; iron chains can receive hanging lanterns. | No fresh target structures were generated/located during this pass. | BLOCKED | None | Generate fresh chunks with known seeds and inspect structure pieces, especially the migrated `Blocks.IRON_CHAIN` branch. |
| Tweaks | ItemFrameHiding remove path | Attacking an invisible, nonempty frame restores visibility and returns a shard. | Add path failed client networking before the removal/particle pair could be validated. | BLOCKED | Same unknown payload affects feedback | Retest both directions after networking repair. |
| Tweaks | JukeboxesStopBackgroundMusic / MobTextures | Disc audio suppresses background music; snow golems/traders can use alternate textures. | Not exercised. | BLOCKED | None | Use controlled audio timing and repeated entity spawns. |

## Runtime log review

Confirmed Charm/Charmony issues:

1. Glint Color smithing produced no output and emitted no diagnostic warning.
2. Item-frame hiding emitted `Unknown custom packet payload: charmony:add_amethyst_to_item_frame`; server state still changed to invisible.
3. Emergency totem swap did not move a non-held totem from inventory slot 9 to offhand and emitted no warning.

Test-fixture-only log entry:

- `Block-attached entity at invalid position: null` occurred while command-summoning an item frame before it was attached. The frame subsequently existed and the Charm interaction ran; this is not attributed to Charm.

Previously classified environmental/cosmetic warnings remained unchanged:

- Mojang authentication TLS/PKIX failures;
- missing offline Services/chat key;
- nine untranslated convention-tag warnings.

No fatal mixin, registry, datapack, recipe parse, missing model/texture, renderer, block-entity serialization, or world-save error occurred. The final save wrote all three dimensions successfully.

## Module status summary

- Fully passing for the functionality exercised: Collection, Decor, Totem of Preserving.
- Partially passing: Azalea Wood, Ebony Wood, Brew and Stew, Mooblooms, Tweaks.
- Confirmed failing module: Glint Colors smithing workflow.
- Confirmed failing Tweaks features: Totem Emergency Swap; Item Frame Hiding client payload/particle feedback.
- Broadly blocked for later focused fixtures: manual crafting, Cooking Pot, Moobloom breeding/pollination/shearing/planting, worldgen/trades, and many opt-in-by-gameplay Tweaks behaviors listed above.

## Files changed

- `PHASE4B_GAMEPLAY_VALIDATION.md`

No source, build, configuration, resource, data, or existing documentation file was modified.
