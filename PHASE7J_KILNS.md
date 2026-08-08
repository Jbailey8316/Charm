# Phase 7J — Kilns

## Historical behavior

The archived Charm 1.21.1 implementation describes the Kiln as a functional
block that speeds up firing of clay, glass, bricks and terracotta. It is a
vanilla furnace-family container, but uses Charm's `firing` cooking recipe type
and a 100-tick cook time (half the normal furnace time). Historical recipes
covered clay balls to bricks, clay to terracotta, sand to glass, cobblestone to
stone, cobbled deepslate to deepslate, netherrack to nether bricks, colored
terracotta to glazed terracotta, cracked brick families, and smooth stone-like
families. Historical fuel duration was halved as well, preserving the furnace
fuel-per-output ratio while reducing elapsed processing time.

The historical crafting recipe is four bricks around a furnace. The block uses
furnace-style orientation, lit state, inventory persistence, sided automation,
comparator fullness, hopper input/fuel/output, custom naming, and the smoker
GUI artwork/sounds with a Charm-specific kiln bake sound entry.

## 1.21.10 architecture

The root Charm module now provides:

* `Firing` — a native `charm:firing` `RecipeType` and cooking serializer;
* `FiringRecipe` — an `AbstractCookingRecipe` implementation with current
  1.21.10 codec fields and recipe-book category;
* `Kilns.KilnBlock` — an `AbstractFurnaceBlock` with a current `MapCodec`;
* `Kilns.KilnBlockEntity` — an `AbstractFurnaceBlockEntity` using the firing
  recipe type;
* `Kilns.KilnMenu` — current `AbstractFurnaceMenu` constructors and recipe
  property key;
* `KilnsClient` — smoker-layout screen registration and functional-block tab
  placement.

The furnace server tick is reused. A narrow mixin halves only the fuel duration
when the furnace instance is a Charm Kiln; ordinary furnaces, blast furnaces,
smokers, and campfires are unchanged. The mixin leaves vanilla state changes,
XP, hopper rules, comparator output, and block-entity save/load paths intact.

## Processing matrix

The restored data contains 33 valid `charm:firing` recipes (all 100 ticks):

| Group | Inputs | Outputs | XP |
|---|---|---|---:|
| Clay/stone/nether | clay ball, clay, cobblestone, cobbled deepslate, netherrack, sand tag | brick, terracotta, stone, deepslate, nether brick, glass | 0.35 for clay/brick and clay/terracotta; 0.1 otherwise |
| Glazed terracotta | 16 colored terracotta items | corresponding glazed terracotta | 0.1 |
| Cracked bricks | stone, nether, deepslate, mud, and polished-blackstone brick families | corresponding cracked blocks | 0.1 |
| Smooth families | stone, sandstone, red sandstone, quartz block, basalt | smooth variants | 0.1 |

The historical template resources used placeholder names that are not valid
1.21.10 item identifiers. They were replaced with concrete, namespaced recipe
files so resource loading cannot attempt to resolve `COLOR_*`, `NOT_SMOOTH`, or
`NOT_CRACKED` as literal items. Vanilla recipes remain available in their
normal furnace/blast-furnace categories; Kiln recipes are a separate, faster
processing path.

## Resources and configuration

Restored resources include blockstates, lit/unlit block models, item definition,
Charm kiln textures, the crafting recipe, block loot table, 33 firing recipes,
and Charm translations. The feature is registered under the existing root
Charm feature architecture and can be disabled through the normal Charmony
feature configuration. Item Stacking is not involved.

## Validation

* `:compileJava`: PASS.
* `./gradlew.bat build`: PASS.
* Client launch reached Fabric/Minecraft initialization and loaded the Charm
  modules and Kiln mixin configuration. The timed development launch did not
  reach interactive title-screen testing; the process was terminated by the
  harness. Authentication PKIX errors are development-environment network
  warnings, not Kiln errors. No Kiln recipe parse, registry, renderer, or
  block-entity error appeared in the captured log.
* Interactive crafting, processing, XP, hopper, comparator, save/reload,
  break-with-inventory, config-off, and dedicated-server tests remain
  `UNTESTED` and are recorded in `RELEASE_VALIDATION_BACKLOG.md`.

## Known limitations and follow-up

The historical custom REI integration and custom recipe-book enum are not
reintroduced; the current screen uses the maintained smoker recipe-book layout
and normal recipe data. Runtime verification should confirm recipe-book display
and all 33 recipe entries. The P0 Suspicious Block Falling Item Persistence
blocker remains open.
