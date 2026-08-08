# Phase 7L.3C — Wood-variant Ladders

## Historical and final content

Historical Charm registered 13 Ladder variants: Oak, Spruce, Birch, Jungle,
Acacia, Dark Oak, Mangrove, Cherry, Bamboo, Crimson, Warped, Azalea, and
Ebony. Pale Oak is added as approved semantic parity, producing 14 variants.

The implementation subclasses Minecraft 1.21.10 `LadderBlock`, preserving its
horizontal facing, support/survival checks, waterlogging, collision shape,
climbing behavior, neighbor updates, and fluid handling. No custom movement,
collision, or block entity code was introduced. Each item is registered through
the shared `WoodRegistry` and uses the material fuel provider. Crimson and
Warped therefore remain non-fuel according to their existing material
semantics; overworld woods, Bamboo, Azalea, Ebony, and Pale Oak use their
material fuel values. The historical ladder class did not add a separate fire
ignition hook, so vanilla Ladder fire behavior remains untouched.

## Recipes, tags, and resources

All 14 shaped recipes use the historical stick/wood-plank pattern and produce
three ladders. Fourteen `charm:woodcutting` recipes now resolve to registered
`charmony:*_ladder` items; eleven previously orphaned files were corrected and
three missing Bamboo/Crimson/Warped files were added. Fourteen loot tables,
blockstates, block/item models, translations, and textures are present. The 13
historical texture files were recovered from the local Charm archive. Pale Oak
uses a dedicated generated 16x16 pixel-art texture in the established pale
cream/pink palette.

The historical Charm `charm:ladders` block tag is represented by the current
Charmony `charmony:ladders` tag, and the shared `c:ladders` block tag includes
all 14 variants. These tags are prepared for the future Lumberjack trade
implementation; Lumberjack itself is not changed here. Climbing is inherited
from `LadderBlock` rather than tag-dependent custom code.

## Validation

Charmony, Azalea Wood, and Ebony Wood compile successfully. All 14 ladder
registration/resource/recipe counts were checked, JSON resources parse, and
`./gradlew.bat build` passes. No global vanilla Ladder, Scaffolding, or Vine
behavior was modified. Client startup was not interactively completed in this
environment; no ladder-specific runtime result is claimed.

Placement on each face, climbing/descending, support loss, waterlogging,
save/reload, drops, fire/fuel, representative Pale Oak/Azalea/Ebony/Crimson/
Warped/Bamboo behavior, and Woodcutter output are UNTESTED and listed in
`RELEASE_VALIDATION_BACKLOG.md`.
