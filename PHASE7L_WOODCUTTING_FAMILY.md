# Phase 7L - Woodcutting Family

## 7L.1 Recipe System

Historical Charm used a custom single-input `charm:woodcutting` recipe type.
The archived 1.21.1 source contains 63 source recipes/templates covering
planks/wood/stripped variants, slabs, stairs, fences, fence gates, doors,
trapdoors, pressure plates, buttons, ladders, signs, boats and bamboo rafts.
Concrete Azalea and Ebony recipes are included separately.

The 1.21.10 port registers a native `WoodcuttingRecipe` type and serializer,
isolated from crafting, Stonecutting, smelting, and Kiln firing. Historical
templates were expanded for Oak, Spruce, Birch, Jungle, Acacia, Dark Oak,
Mangrove, Cherry, Crimson, Warped, and Pale Oak. Azalea, Ebony, and the
historical Bamboo conversions are retained. The result is 277 concrete
recipes. Historical generator metadata was stripped from runtime JSON, leaving
normal single-input recipes accepted by the 1.21.10 loader; all 277 files parse.

Pale Oak is a semantic-parity extension of the categorical vanilla-wood
templates. No Pale Oak-specific conversion or balance change was invented.

Validation:

* `./gradlew.bat :compileJava` - PASS
* `./gradlew.bat build` - PASS
* recipe JSON parsing (277 files) - PASS
* recipe-manager/runtime loading - pending client startup verification

## 7L.2 Woodcutter

The workstation is now a dedicated Charm `StonecutterBlock` subclass with a
transient one-slot input, derived result slot, custom menu type, and client
screen. It does not create a block entity, persistent inventory, hopper path,
or comparator signal. The server queries the current recipe manager for only
`charm:woodcutting` recipes and owns the selected recipe, generation, result,
and input consumption.

`charm:woodcutter_recipes` is an isolated server-to-client payload containing
the menu ID, generation, and matching stable recipe IDs. The client sends only
the menu ID, generation, and selected recipe ID in
`charm:woodcutter_select`; the server validates menu identity, workstation
validity, generation, current recipe type, current input, and membership in
the current matching set before selecting. Reload/input changes advance the
generation and invalidate stale selections. Normal take and quick-move use
the same result-slot `onTake` path, with destination-capacity checking before
the server consumes one input.

The screen provides a compact selector using the synchronized IDs and the
vanilla Stonecutter background. Blockstate, item definition, crafting recipe,
loot, translation, creative-tab placement, and orientation resources are
present. Vanilla Stonecutter synchronization and `minecraft:stonecutting`
remain untouched.

Static transaction/mutation review: PASS. Compilation, aggregate build,
startup, and payload registration are PASS. Interactive normal take,
quick-move, full-inventory, close/disconnect, block-removal, stale-selection,
reload, and duplication/item-loss tests are UNTESTED; the release-critical
matrix remains in `RELEASE_VALIDATION_BACKLOG.md`.

## 7L.3 Lumberjack

Not started; it depends on a validated Woodcutter POI/workstation.

Historical Lumberjack trades span five levels and use normal Emerald-based
villager trading. The source has independent toggles for custom barrel,
bookshelf, and ladder outputs. No tree-felling mechanic was found.

Interactive tests for all three subphases remain unperformed and are listed in
`RELEASE_VALIDATION_BACKLOG.md`.

## 7L.3B Bookshelves and Chiseled Bookshelves

Historical Charm supplied 13 Bookshelf and 13 Chiseled Bookshelf variants over
11 vanilla wood families plus Azalea and Ebony. The port adds Pale Oak by the
approved categorical wood-family semantic-parity rule, for 14 of each. The
Ladder family remains intentionally out of scope for this subphase.

Charmony's reusable `WoodRegistry` now registers material-specific blocks and
items. Bookshelves retain vanilla block properties and enchanting power through
the `minecraft:enchantment_power_provider` tag; chiseled bookshelves subclass
the current 1.21.10 `ChiseledBookShelfBlock` and are added to the vanilla
`CHISELED_BOOKSHELF` block-entity type, preserving six-slot storage, slot
selection, comparator behavior, persistence, and vanilla interaction logic.
No custom block entity or hopper implementation was introduced.

Historical bookshelf/chiseled-bookcase recipes and loot behavior were restored
for all 14 families. Twenty-eight targeted `charm:woodcutting` recipes repair
the previously orphaned bookshelf outputs (14 Bookshelves and 14 Chiseled
Bookshelves); the existing 277 recipe files were otherwise left untouched.
The `charmony:chiseled_bookshelves` block tag contains all 14 variants for the
future Lumberjack trade implementation. Pale Oak uses dedicated generated
pixel-art textures; the 13 historical families use recovered Charm assets.

Static validation: Charmony, Azalea Wood, Ebony Wood, aggregate build, and
resource JSON parsing passed. Client startup was attempted and timed out in the
available environment; no bookshelf-specific error was observed in the latest
log snapshot. Interactive placement, enchanting-power, fuel/fire, six-slot
insertion/removal, comparator, hopper, save/reload, break-with-contents, and
component-preservation tests are UNTESTED and remain release-backlog items.
Lumberjacks remain unimplemented.
