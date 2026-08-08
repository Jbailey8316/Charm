# Phase 5B.1 - Wood Variant Barrels

## Scope and result

Phase 5B.1 restores fourteen native Charm barrel variants for Minecraft 1.21.10:

| Family | Source | Registration owner |
| --- | --- | --- |
| Acacia | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Bamboo | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Birch | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Cherry | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Crimson | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Dark Oak | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Jungle | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Mangrove | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Oak | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Pale Oak | New 1.21.10-derived asset | Root `VanillaWoodVariants` feature |
| Spruce | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Warped | Historical Charm asset | Root `VanillaWoodVariants` feature |
| Azalea | Historical Charm asset | Azalea Wood feature |
| Ebony | Historical Charm asset | Ebony Wood feature |

Chests and trapped chests remain outside this phase.

## Historical reference and architecture

The technical reference was Charm 7.0.28 at revision `8abe6f8` on the historical `1.21.1-fabric` line:

- <https://github.com/muon-rw/Charm/tree/1.21.1-fabric>
- <https://web.archive.org/web/20260216024040/https://charmony.work/>

The current Charmony core retained the reusable `Barrel` registration helper and
`CustomBarrelBlock`. The implementation continues to subclass vanilla
`BarrelBlock`, create vanilla `BarrelBlockEntity` instances, associate every
variant with `BlockEntityType.BARREL`, add the variants to the Fisherman POI,
and use the common creative-tab and fuel registration paths. Thus inventory,
opening, facing, names, serialization, hopper access, comparator calculation,
piglin anger, spectator handling, and menu behavior remain vanilla-derived
rather than copied into fourteen implementations.

The root aggregate previously had no common initializer. A small root Charm
entrypoint now owns the twelve vanilla-family registrations under the existing
feature/config system. Azalea and Ebony register through their existing module
features. All registry IDs remain in the established `charmony` namespace.

## Resources

For the eleven historical vanilla families plus Azalea and Ebony, all four
historical barrel textures (`bottom`, `side`, `top`, and `top_open`) were
recovered unchanged from Charm 7.0.28. Historical model structure and recipe
shape were restored with 1.21.10 resource locations and item definitions.

Pale Oak had no historical Charm art. Its four textures use the recovered Birch
barrel pixel geometry as the closest light-wood member of the original set. A
deterministic luminance mapping recolored that geometry using only palette
colors sampled from Minecraft 1.21.10 `pale_oak_planks.png`:
`C0B2B2`, `C7B8B8`, `D2C6C7`, `DDCECD`, `E7E3E1`, `FAEFEE`, and `FFFBF8`.
No third-party art or runtime texture service is used.

Each family has:

- six-direction open/closed blockstate mappings;
- closed/open block models and inventory item definitions/models;
- a corresponding planks-and-slabs shaped recipe;
- recipe advancement and self-dropping loot table with custom-name copying;
- block and item translations;
- axe-mineable and conventional `c:barrels` / `c:barrels/wooden` tags.

The root feature is inserted after the vanilla barrel in family order. Azalea
and Ebony retain their module-local creative placement.

## API and migration notes

- `BlockSetType.PALE_OAK` and `WoodType.PALE_OAK` are used for the new family.
- Crimson and Warped remain non-flammable; all other registered families use
  the shared wooden fuel behavior.
- Minecraft 1.21.10 item-definition JSON under `assets/charmony/items` is
  supplied in addition to the traditional item model.
- Loot tables use the current `copy_components` function to retain
  `minecraft:custom_name` when a named barrel is broken.
- No shared barrel behavior needed redesign or replacement.

## Validation

### Static and build

- All fourteen registration paths are present exactly once.
- 124 changed or directly relevant JSON files parse successfully.
- Every expected model, item definition, texture, recipe, advancement, loot
  table, translation, and tag resource exists.
- Integrated-server reload reported 1,510 recipes and 1,660 advancements with
  no Charm recipe, loot, registry, block-entity, model, or texture errors.
- `:charmony:build`, `:charmony-azalea-wood:build`, and
  `:charmony-ebony-wood:build` pass.
- Aggregate `./gradlew build` passes.

### Runtime smoke test

The existing `New World1` development world was loaded, saved, exited, and
reloaded successfully.

| Test | Result |
| --- | --- |
| Root feature and all module registrations load | PASS |
| Oak, Pale Oak, Crimson, Azalea, and Ebony inventory icons | PASS |
| Representative placement and distinct textures | PASS |
| Horizontal plus up/down facing blockstates | PASS |
| Closed/open model switching while using the container | PASS |
| Vanilla 27-slot menu and stored items | PASS |
| Custom name and inventory after save/reload | PASS |
| Hopper insertion and extraction | PASS |
| Correct Pale Oak loot-table drop | PASS |
| Comparator signal calculation from stored inventory | PASS (shared vanilla method returned signal 1 for the test inventory) |

Runtime logs contain no barrel-specific errors or warnings. The known local
Mojang authentication/Realms PKIX failures remain external development-network
noise. Existing Moobloom-head texture warnings and the previously documented
Tweaks disabled-feature messages are unrelated to this phase.

## Files changed

- Root Charm initializer and `VanillaWoodVariants` feature/material/registration
  classes.
- Root `fabric.mod.json` entrypoint declaration.
- Root resources for twelve vanilla-family barrels.
- Azalea Wood registration and Azalea barrel resources.
- Ebony Wood registration and Ebony barrel resources.
- This phase document.

## Remaining work

Wood-variant normal and trapped chests are still absent and should be handled in
a later Phase 5B.x task. Full recipe crafting for all fourteen permutations and
long-duration automation testing remain suitable for the broader parity pass;
the recipes loaded cleanly and representative runtime storage behavior passed.
