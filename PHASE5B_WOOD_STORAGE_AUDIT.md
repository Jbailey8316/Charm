# Phase 5B: Wood Storage Audit

## Scope and baseline

This is an audit-only phase covering wood-variant chests, trapped chests, and barrels. It makes no implementation change to Java or resources.

- Root baseline: `e0c52d7` (`Add variant-specific Moobloom heads`)
- Charmony baseline: `763ef3339b18af6de47bb6f87affc95592f9d208`
- Azalea Wood baseline: `95ed50c58a7adeda7e3769540323abdbbb719d73`
- Ebony Wood baseline: `197073a7cad6d384282213fc360f644766931404`
- Target: Minecraft 1.21.10, Fabric, Java 21
- Audit reference: original Charm 7.0.28 commit `8abe6f82952ea4d1ee2f60f60e97588711c7e61f` (Minecraft 1.21), preserved in the `muon-rw/Charm` fork history

The archived Charm feature description says that the Wood feature provided barrels, bookcases, chests, and ladders in all vanilla wood types. The Charm 7.0.28 source is the controlling implementation reference for this audit. It also shows that Azalea and Ebony explicitly opted into normal chests, trapped chests, and barrels even though the website's short description calls out only the vanilla variants.

Reference links:

- Archived feature site: <https://web.archive.org/web/20260216024040/https://charmony.work/>
- Preserved source repository: <https://github.com/muon-rw/Charm/tree/1.21.1-fabric>
- Exact original 7.0.28 commit: <https://github.com/muon-rw/Charm/commit/8abe6f82952ea4d1ee2f60f60e97588711c7e61f>
- Published 7.0.28 build: <https://modrinth.com/mod/charm/version/7.0.28>

## Executive result

Neither wood-variant chests nor wood-variant barrels currently exist at runtime in the 1.21.10 aggregate.

The current Charmony core contains a partially recovered, uncalled barrel helper. It has no accompanying storage assets or data. Chest and trapped-chest names remain in the `CustomWood` enum and creative-position map, but their block classes, holders, block entities, renderers, registrations, and resources were not recovered into the modular codebase. No evidence indicates that Phase 1 through Phase 5A intentionally removed these features; they were already absent from the exact recovered 1.21.6 baseline and appear to have been omitted during the earlier monolith-to-Charmony split.

Restoring barrels is a bounded registration-and-resource task. Restoring chests is substantial reconstruction because the original used two custom block-entity types, a custom chest renderer/material map, special inventory-item block-entity rendering, and a `Sheets.chooseMaterial` mixin. Per the Phase 5B instruction, implementation stops here pending approval of the proposed architecture.

## Original Charm behavior

### Material coverage in Charm 7.0.28

The original `VanillaWood` enum covered exactly 11 vanilla families:

1. Acacia
2. Bamboo
3. Birch
4. Cherry
5. Crimson
6. Dark Oak
7. Jungle
8. Mangrove
9. Oak
10. Spruce
11. Warped

The Azalea and Ebony `WoodDefinition` classes separately included `BARREL`, `CHEST`, and `TRAPPED_CHEST`. Original effective coverage was therefore:

| Content | Vanilla variants | Charm variants | Total |
|---|---:|---:|---:|
| Normal chest | 11 | Azalea + Ebony | 13 |
| Trapped chest | 11 | Azalea + Ebony | 13 |
| Barrel | 11 | Azalea + Ebony | 13 |
| Registered storage blocks/items | 33 | 6 | 39 |

Oak was deliberately registered as a distinct `charm:oak_chest`, `charm:trapped_oak_chest`, and `charm:oak_barrel`; the vanilla oak-like chest and barrel did not substitute for these variants.

### New vanilla family in 1.21.10

Minecraft 1.21.10 has the same 11 families above plus Pale Oak, for 12 vanilla wood families. Pale Oak was introduced after Charm 7.0.28, in Minecraft 1.21.4, and consequently has no original Charm registration, recipe, translation, chest texture, or barrel texture.

Pale Oak is an explicit parity/design decision:

- Exact historical parity: restore the original 11 vanilla families and Azalea/Ebony only.
- Continue the advertised rule, "all vanilla wood types": also add Pale Oak, requiring a new coherent texture set and data entries.

Recommendation: include Pale Oak in the eventual implementation because that preserves the feature's semantic rule on the target Minecraft version, but treat its art as new work requiring review rather than silently deriving or substituting textures.

### Original chest architecture and behavior

Original normal chests used `CustomChestBlock extends ChestBlock` and `CustomChestBlockEntity extends ChestBlockEntity`. Original trapped chests used a separate `CustomTrappedChestBlock extends ChestBlock` and `CustomTrappedChestBlockEntity`, with trapped-chest statistics and redstone signal behavior copied from vanilla.

The architecture preserved vanilla container behavior instead of recreating inventory logic:

- `ChestBlock` supplied placement orientation, single/double formation, obstruction checks, menus, comparator support, and open handling.
- `ChestBlockEntity` supplied 27-slot storage, custom names, serialization, lid/open-count animation, player tracking, and double-container composition.
- The normal and trapped custom block-entity types were associated with every registered variant.
- Normal and trapped blocks had distinct types, recipes, tags, loot tables, and textures.
- Loot tables copied the block-entity custom name. Vanilla removal behavior handled dropping stored contents; the block item itself retained the exact wood variant.
- Custom chest items rendered through cached custom block entities, so inventory, held, and dropped-item rendering used the same variant texture family.

For every material, the original shipped six entity textures:

- normal single, left, and right;
- trapped single, left, and right.

That is 78 chest textures for the 13 original materials. The left/right files supported double chests; `ChestRenderer` supplied the lid animation and geometry.

The renderer registered material mappings for `ChestType.SINGLE`, `LEFT`, and `RIGHT`. A mixin intercepted `Sheets.chooseMaterial(BlockEntity, ChestType, boolean)` and returned the appropriate variant material. The original mixin ignored the `christmas` argument whenever a custom material was found, and no custom Christmas textures were shipped. Therefore original variant chests did **not** switch to vanilla Christmas chest textures on December 24-26. Restoring that exact behavior is preferable unless a deliberate parity change is approved.

The original explicitly supported trapped variants for all 13 materials. Their redstone behavior used the open-count clamp from vanilla trapped chests. Hopper and comparator behavior came from the inherited vanilla chest/block-entity implementation and applicable chest tags rather than a separate Charm inventory system.

### Original barrel architecture and behavior

Original barrels used `CustomBarrelBlock extends BarrelBlock`, copied all properties from `Blocks.BARREL`, returned a vanilla `BarrelBlockEntity`, and initialized the vanilla `FACING` and `OPEN` states. The registration dynamically added every custom barrel to `BlockEntityType.BARREL`, added all states to the Fisherman point-of-interest type, registered the item as fuel, and placed it in the functional-blocks creative tab.

This inherited vanilla behavior for:

- six-direction placement and orientation;
- open/closed state and sound;
- menu and 27-slot inventory;
- custom names and persistence;
- hopper insertion/extraction;
- comparator output;
- stored-content drops and the correct variant block drop.

Each material shipped four barrel textures (bottom, side, top, and open top), for 52 original textures, plus shared generated blockstate/model/item-model, recipe, advancement, loot, tag, and language data.

## Current state: chests

### Runtime availability

- Registered wood-variant normal chests: **0**
- Registered wood-variant trapped chests: **0**
- Vanilla families represented: **none**
- Azalea normal/trapped chest: **absent**
- Ebony normal/trapped chest: **absent**

Chest boats are present for Azalea and Ebony, but they are vanilla-style vehicle content and are not evidence of storage chest block registration.

### Code state

`modules/charmony/src/main/java/svenhjol/charmony/core/common/features/wood/CustomWood.java` still declares `CHEST` and `TRAPPED_CHEST`, and `WoodMaterial` still has creative-menu anchors for them. There is no corresponding `types/Chest.java`, `types/TrappedChest.java`, custom chest block, chest block entity, chest block-entity registration, client renderer, item renderer path, texture-material registry, or chest mixin in current Charmony.

Neither Azalea nor Ebony calls a chest registration method, and `WoodRegistry` exposes no such method. There is no vanilla-wood-variant registration feature anywhere in the aggregate.

### Resource/data state

No wood chest blockstates, block/item definitions, chest entity textures, recipes, loot tables, chest tags, advancements, or translations survive in the current modules. The only Azalea/Ebony files matching "chest" are chest-boat resources. Thus there are no dormant chest assets that merely need registration.

### Migration risk

This is not a small registration repair. The 1.21 implementation cannot be pasted unchanged:

- Chest rendering must be ported to the 1.21.10 block-entity renderer/render-state interfaces and current material-selection API.
- The old `Sheets.chooseMaterial` target and descriptor must be verified against 1.21.10; the exact hook may have moved or changed shape.
- Inventory/held/dropped block-item rendering must use the current special-model or block-entity item-rendering path.
- Custom block-entity types must be registered with all variant blocks without mutating registries after freeze.
- Normal and trapped blocks must retain correct block-entity types, double-chest pairing, redstone behavior, and screen/menu validity.
- All single/left/right texture mappings must be tested; a compiling renderer is insufficient.

## Current state: barrels

### Runtime availability

- Registered wood-variant barrels: **0**
- Vanilla families represented: **none**
- Azalea barrel: **absent**
- Ebony barrel: **absent**

### Code state

Two reusable pieces survive in Charmony core:

- `core/common/features/wood/types/Barrel.java`
- `core/common/features/wood/blocks/CustomBarrelBlock.java`

They closely preserve the original inheritance model and are already compiled against 1.21.10. However, there are no calls to `WoodRegistry.barrel(...)` in Charmony, Azalea Wood, Ebony Wood, or any other module. There is also no current `VanillaWood` material enumeration or a feature that registers the vanilla families.

The helper's intended behavior is therefore untested at runtime. In particular, its dynamic association with `BlockEntityType.BARREL` and Fisherman POI state set must be confirmed during implementation, even though the aggregate build currently accepts the code.

### Resource/data state

No wood barrel textures, blockstates, models, item definitions, recipes, loot tables, tags, advancements, or translations survive in the current modules. Registering the existing helper alone would create missing-model/data failures and would not constitute a valid repair.

## Completeness matrix

| Area | Chests | Barrels |
|---|---|---|
| Block/item registration | Missing | Helper exists; never invoked |
| Vanilla wood registrations | Missing | Missing |
| Azalea registration | Missing | Missing |
| Ebony registration | Missing | Missing |
| Block entities | Missing custom types/classes | Vanilla BE helper path exists |
| Client renderer | Missing | Vanilla block-model renderer applicable |
| Single/double/left/right | Missing | Not applicable |
| Open/lid state | Missing with chest renderer | Inherited by helper; untested |
| Trapped variants/redstone | Missing | Not applicable |
| Textures | Missing (78 original files) | Missing (52 original files) |
| Blockstates/models/item definitions | Missing | Missing |
| Recipes/advancements | Missing | Missing |
| Loot tables | Missing | Missing |
| Block/item tags | Missing | Missing |
| Creative placement | Anchors only | Helper code exists; no items |
| Language entries | Missing | Missing |
| Runtime validation | Impossible | Impossible |

## Intended module ownership

The original monolith registered vanilla variants under the Charm `Wood -> Vanilla Wood Variants` feature, while Azalea and Ebony opted into the same shared `CustomWood` machinery from their own features.

For the modular port, the least invasive ownership model is:

1. Put only reusable chest/trapped-chest block, block-entity, renderer, and registration machinery in `modules/charmony` beside the existing barrel helper.
2. Let `modules/charmony-azalea-wood` register and own `charmony:azalea_chest`, `charmony:trapped_azalea_chest`, and `charmony:azalea_barrel`, with its resources and config lifecycle.
3. Let `modules/charmony-ebony-wood` do the equivalent for Ebony.
4. Add a small Charm-owned vanilla-storage feature/entrypoint in the root project (or a dedicated `charmony-wood` module if separate distribution is desired) to register the 11 historical vanilla families. Do not make standalone Charmony core automatically add Charm gameplay content.
5. Decide explicitly whether the target feature also registers Pale Oak.

The root project currently has no Java entrypoint, so option 4 requires a scoped bootstrap addition. A dedicated module is architecturally cleaner for independent distribution but changes the recovered module topology. The recommended default is a root Charm entrypoint because these variants were originally a Charm feature and the root aggregate already owns mod ID `charm`.

## Exact implementation areas that would change

No files below were changed in this audit. The expected implementation surface is:

### Charmony core (shared mechanics)

- `modules/charmony/src/main/java/.../core/common/features/wood/WoodRegistry.java`
- New shared chest and trapped-chest type holders under `.../features/wood/types/`
- New custom chest/trapped-chest blocks under `.../features/wood/blocks/`
- New custom chest/trapped-chest block entities and registrations
- Client renderer/material mapping and block-item rendering support under `.../core/client/features/wood/`
- A narrowly targeted client mixin only if the supported 1.21.10 renderer APIs cannot select per-material chest textures
- Charmony mixin configuration/access widener only as demonstrated necessary

### Root Charm (vanilla variants)

- Root `fabric.mod.json` entrypoints and a small Charm common/client initializer
- A vanilla wood material definition covering the historical 11 families (plus Pale Oak only after the decision)
- Registration calls and a shared feature/config toggle matching original Vanilla Wood Variants behavior
- Vanilla-family recipes, loot tables, tags, advancements, translations, blockstates/models/item definitions, and chest/barrel textures

### Azalea Wood and Ebony Wood

- Their `Registers.java` files to invoke the shared chest, trapped-chest, and barrel registrations
- Client registration only where required by the shared chest renderer
- Each module's recipes, loot tables, tags, advancements, translations, blockstates/models/item definitions, six chest textures, and four barrel textures

### Build configuration

No dependency change is expected. Root source compilation/entrypoints may need only existing source-set wiring. If a new module is chosen instead, `settings.gradle`, `build.gradle`, and `.gitmodules` would require explicit changes and a separately established repository baseline.

## Recommended implementation sequence

Because chest reconstruction is high-risk, split implementation into reviewable subphases:

1. **5B.1 - Barrel restoration:** restore the 13 original barrels using the existing helper and recovered original assets/data; validate orientation, open state, inventory persistence, hopper/comparator behavior, Fisherman POI registration, breaking, and save/reload.
2. **5B.2 - Chest foundation:** port custom normal/trapped blocks and block entities; validate registrations, menus, single/double formation, inventory persistence, hoppers, comparators, trapped redstone, and breaking before custom rendering.
3. **5B.3 - Chest rendering/resources:** port single/left/right texture selection, lid animation, inventory/held/dropped rendering, and all data/resources; confirm original non-Christmas behavior or approve a deliberate change.
4. **5B.4 - Pale Oak decision:** add reviewed Pale Oak art/data if preserving "all vanilla wood types" on 1.21.10 is selected.
5. **5B.5 - parity/runtime sweep:** test every material at least once and representative variants exhaustively, then audit recipes, tags, advancements, creative ordering, names, and save compatibility.

Recovered original binary assets should be imported without placeholder replacement. Resource namespaces must be adapted deliberately: original assets used `charm`, while current Azalea/Ebony modules generally use `charmony` IDs.

## Validation and build result

- Implementation/runtime tests: not run because no storage content is currently registered and this phase stopped at the required architecture gate.
- JSON parsing: not applicable; no resource files were added or changed.
- Aggregate build: `./gradlew build` passed on 2026-08-08 (`BUILD SUCCESSFUL in 16s`; 92 tasks up-to-date).

**Final aggregate build result:** PASS.

## Unresolved parity decisions

1. Whether to add Pale Oak to honor the feature's target-version meaning of "all vanilla wood types," despite its absence in Charm 7.0.28.
2. Whether vanilla variants should live in the root Charm mod (recommended) or a new independently distributable module.
3. Whether to preserve the original lack of Christmas texture substitution (recommended for exact behavior) or add new per-material Christmas behavior as an intentional change.
4. Whether existing worlds from Charm 7 should retain the old `charm:*` storage IDs. The root-owned vanilla variants should use those IDs for compatibility; Azalea/Ebony modular ownership needs an explicit ID/remapping policy before registration.
