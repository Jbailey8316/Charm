# Phase 7L.3B — Wood-variant Bookshelves

## Scope and historical parity

Historical Charm registered 13 Bookshelf and 13 Chiseled Bookshelf variants:
Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry, Bamboo,
Crimson, Warped, Azalea, and Ebony. Ladders and Lumberjack trades are not part
of this phase. Pale Oak is added as an intentional semantic-parity extension
of the categorical wood-family system, producing 14 variants of each block.

The historical Bookshelf was a vanilla-equivalent bookshelf with material
fuel/flammability. Chiseled Bookshelves retained the vanilla six-slot book
inventory, slot interaction, comparator signal, last-interacted-slot state,
and block-entity persistence.

## Implementation

`WoodRegistry` now exposes `bookshelf` and `chiseledBookshelf` registrations.
`CustomBookshelfBlock` copies vanilla Bookshelf properties and contributes to
the vanilla `minecraft:enchantment_power_provider` tag. Its item uses the
material fuel provider and the block uses material ignition/burn values.
`CustomChiseledBookshelfBlock` subclasses the 1.21.10
`ChiseledBookShelfBlock`; each block is added to vanilla
`BlockEntityType.CHISELED_BOOKSHELF`, so no parallel inventory or block entity
logic exists. This preserves vanilla six-slot ownership and serialization.

Registrations cover 11 vanilla families, Pale Oak, Azalea, and Ebony. The
historical 13-family assets were recovered from the local Charm archive. Pale
Oak has dedicated generated 16x16 pixel-art textures in the established pale
cream/pink palette; it is not a historical Charm asset.

## Resources and recipes

Each family has blockstate, block/item models, bookshelf and chiseled slot
models, textures, crafting recipe, loot table, and translation keys. The two
14-family crafting sets use planks/books and planks/slabs respectively, with
historical shaped patterns and one output.

Twenty-eight targeted `charm:woodcutting` recipes were added: one Bookshelf and
one Chiseled Bookshelf output for each family. Existing Woodcutting recipes were
not regenerated. The `charmony:chiseled_bookshelves` block tag contains all 14
variants for the subsequent Lumberjack implementation.

Material semantics remain centralized: overworld, Azalea, Ebony, and Pale Oak
use their material fuel/ignition behavior; Nether Crimson and Warped materials
retain their existing nonflammable semantics. Chiseled Bookshelves do not gain
enchanting power, matching vanilla.

## Safety and validation

Static checks passed: Charmony, Azalea Wood, and Ebony Wood compile; aggregate
`./gradlew.bat build` passes; generated JSON resources parse; registrations and
block-entity associations use vanilla types; no persistent custom inventory,
hopper implementation, or global vanilla bookshelf mixin was added. Client
startup was attempted but timed out in this environment; no bookshelf-specific
error appeared in the latest log snapshot.

Interactive validation is UNTESTED: placement/breaking, enchanting power,
fuel/fire, six-slot insertion/removal, comparator and hopper behavior,
save/reload, break-with-contents, component preservation, creative-tab access,
and representative Nether/Pale Oak behavior remain in
`RELEASE_VALIDATION_BACKLOG.md`.

Files changed include Charmony wood registration/block classes and types,
vanilla/Azalea/Ebony registration wiring, 28 Woodcutting recipes, 28 crafting
recipes, 28 loot tables, 56 block/item model groups plus chiseled slot models,
14-family blockstates, textures, translations, enchanting-power and
Lumberjack-preparation tags, and this documentation.
