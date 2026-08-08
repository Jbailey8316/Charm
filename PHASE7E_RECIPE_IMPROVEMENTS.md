# Phase 7E — Recipe Improvements

## Historical behavior

The historical `recipe_improvements` feature contained thirteen recipe
definitions plus one recipe-unlocking behavior (fourteen user-facing entries
in total). The recipes were ordinary Minecraft recipe JSON and were gated by
the feature configuration. The historical bundle recipe is not copied because
Minecraft 1.21.10 now provides the intended leather-plus-string bundle recipe
vanilla.

## Recipe matrix

| Historical entry | Historical behavior | 1.21.10 result | Status |
|---|---|---|---|
| Raw copper block blasting | Raw Copper Block -> Copper Block in a blast furnace | Added as `charm:recipe_improvements/copper_block_from_blasting_raw_copper_block` | RESTORED |
| Raw gold block blasting | Raw Gold Block -> Gold Block in a blast furnace | Added as `charm:recipe_improvements/gold_block_from_blasting_raw_gold_block` | RESTORED |
| Raw iron block blasting | Raw Iron Block -> Iron Block in a blast furnace | Added as `charm:recipe_improvements/iron_block_from_blasting_raw_iron_block` | RESTORED |
| Gilded Blackstone | Eight gold nuggets around blackstone | Added as `charm:recipe_improvements/gilded_blackstone` | RESTORED |
| Cyan Dye | Warped Roots -> Cyan Dye | Added as `charm:recipe_improvements/cyan_dye` | RESTORED |
| Green Dye | Yellow Dye + Blue Dye -> Green Dye | Added as `charm:recipe_improvements/green_dye` | RESTORED |
| Snowballs | Snow Block -> four Snowballs | Added as `charm:recipe_improvements/snowballs_from_snow_block` | RESTORED |
| Quartz | Quartz Block -> four Quartz | Added as `charm:recipe_improvements/quartz_from_quartz_block` | RESTORED |
| Clay Balls | Clay Block -> four Clay Balls | Added as `charm:recipe_improvements/clay_balls_from_clay_block` | RESTORED |
| Soul Torch | Soul-fire-base block + stick -> two Soul Torches | Added as `charm:recipe_improvements/soul_torch` | RESTORED |
| Shapeless Bread | Three Wheat -> Bread | Added as `charm:shapeless_recipes/bread` | RESTORED |
| Shapeless Paper | Three Sugar Cane -> three Paper | Added as `charm:shapeless_recipes/paper` | RESTORED |
| Leather Bundle | Eight Leather + two String -> Bundle | 1.21.10 vanilla supplies leather + string -> Bundle | VANILLA SUPERSEDED |
| Recipe Unlocking | Award all recipes when a player joins | Preserved as an opt-in server-join feature | RESTORED |

No unresolved recipe collisions were found. The cyan, green, and soul-torch
recipes are additional historical inputs rather than duplicate vanilla
recipes; the reverse block recipes and shapeless forms likewise have distinct
inputs or recipe types.

## Implementation

`RecipeImprovements` is a root Charm common feature registered by
`CommonInitializer`. Each recipe has its own configuration toggle, defaulting
to enabled. The historical recipe-unlocking option is retained and defaults to
disabled because it changes player progression globally. Conditional recipes
are generated and parsed by Charmony's existing conditional-recipe reload
infrastructure, so disabling a toggle removes only that Charm recipe while
leaving vanilla recipes intact.

Charmony's `ConditionalRecipe` now supports the required shaped, shapeless,
and blasting JSON forms, including ingredients, cooking time, experience, and
result counts. All restored recipes remain normal server recipes and are
structurally compatible with the vanilla Crafter; no player-only Java crafting
logic was introduced.

## Recipe book and runtime status

The recipes are emitted through the normal recipe manager and therefore use
standard recipe-book discovery and server-authoritative crafting. Static
codec parsing and compilation succeeded. Interactive crafting, recipe-book,
Crafter, toggle, and dedicated-server tests were not available in this phase;
they remain in `RELEASE_VALIDATION_BACKLOG.md` and are recorded as UNTESTED.

## Files changed

- `src/main/java/svenhjol/charm/common/CommonInitializer.java`
- `src/main/java/svenhjol/charm/common/features/recipe_improvements/RecipeImprovements.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/features/conditional_recipes/ConditionalRecipe.java`
- `PHASE7B_MASTER_PARITY_AUDIT.md`
- `RELEASE_VALIDATION_BACKLOG.md`

## Validation

`:compileJava` and the aggregate `./gradlew build` pass. Resource/recipe codec
loading is performed by the existing conditional recipe manager; no parse or
duplicate-recipe errors were observed during static validation. A dev-client
startup/resource smoke test should still be run before release. No production
warnings specific to Recipe Improvements were found. The P0
`Suspicious Block Falling Item Persistence` blocker remains OPEN pending its
manual runtime test.
