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

Not started. The first implementation attempt was stopped at the safety gate:
the menu requires explicit client recipe synchronization and vanilla-equivalent
result/quick-move transaction handling. An unsafe shortcut could create stale
outputs, item loss, or duplication. It remains separated from the clean recipe
backend until those semantics are migrated and tested against 1.21.10
Stonecutter behavior.

## 7L.3 Lumberjack

Not started; it depends on a validated Woodcutter POI/workstation.

Historical Lumberjack trades span five levels and use normal Emerald-based
villager trading. The source has independent toggles for custom barrel,
bookshelf, and ladder outputs. No tree-felling mechanic was found.

Interactive tests for all three subphases remain unperformed and are listed in
`RELEASE_VALIDATION_BACKLOG.md`.
