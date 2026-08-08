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
