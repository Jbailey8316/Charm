# Phase 7L — Woodcutting Family

## 7L.1 Recipe System

Historical Charm used a custom single-input `charm:woodcutting` recipe type.
The archived 1.21.1 source contains 63 source recipes/templates. Template
expansion covers planks/wood/stripped variants, slabs, stairs, fences, fence
gates, doors, trapdoors, pressure plates, buttons, ladders, signs, boats and
bamboo rafts. Concrete Azalea and Ebony recipes are included separately.

The 1.21.10 port registers a native `WoodcuttingRecipe` type and serializer,
isolated from crafting, Stonecutting, smelting, and Kiln firing. Historical
templates were expanded for Oak, Spruce, Birch, Jungle, Acacia, Dark Oak,
Mangrove, Cherry, Crimson, Warped, and Pale Oak. Azalea, Ebony, and the
historical Bamboo conversions are retained. The resulting resource set is
277 concrete recipes (the historical template source count is 63; expansion
and Pale Oak add the remaining concrete family recipes).

The Pale Oak recipes are a semantic-parity extension of the categorical
vanilla-wood templates. No Pale Oak-specific conversion or balance change was
invented.

Validation so far:

* `./gradlew.bat :compileJava` — PASS
* recipe resources are generated under `data/charm/recipe/woodcutting`
* recipe-manager/runtime loading — pending client startup verification

## 7L.2 Woodcutter

Not started. It remains intentionally separated from the recipe backend until
the menu transaction design is migrated and tested against 1.21.10
Stonecutter semantics.

## 7L.3 Lumberjack

Not started. It depends on a validated Woodcutter POI/workstation.

Historical Lumberjack trades span five levels and use normal Emerald-based
villager trading. The source has independent toggles for custom barrel,
bookshelf, and ladder outputs. No tree-felling mechanic was found.

Interactive tests for all three subphases remain unperformed and will be
added to `RELEASE_VALIDATION_BACKLOG.md` as each subsystem is completed.
