# Phase 5B.2: Variant Normal Chests

## Scope

This phase restores Charm's normal (non-trapped) wood chests for Minecraft 1.21.10. Trapped chests remain a Phase 5B.3 task.

## Architecture

The implementation uses one shared Charmony `CustomChestBlock`, `CustomChestBlockEntity`, and client `CustomChestRenderer`. The block entity subclasses vanilla `ChestBlockEntity`, so vanilla inventory, menu, persistence, comparator, hopper, lid, and double-chest behavior remain in the common implementation. A `WoodMaterial` on the block selects the renderer material; the renderer reuses vanilla single, double-left, and double-right chest model layers with Charm texture materials.

Each wood family has one registered block/item pair and one recipe, loot table, blockstate, item definition, and model set. Pairing is deliberately restricted to another `CustomChestBlock` with the same `WoodMaterial`, preventing Oak/Birch or Azalea/Ebony from forming a mixed double chest.

## Registered families

The 14 registered families are Acacia, Bamboo, Birch, Cherry, Crimson, Dark Oak, Jungle, Mangrove, Oak, Pale Oak, Spruce, Warped, Azalea, and Ebony.

The 13 historical Charm families reuse the recovered normal single/left/right entity chest textures from the 1.21.1 source. Pale Oak has newly generated single/left/right textures: the historical Birch normal textures were used as the geometry/layout source and their wood palette was deterministically remapped to the 1.21.10 Pale Oak palette used by Charm's Pale Oak block textures. No third-party artwork or remote texture service is used.

## Resource and integration coverage

All 14 families have blockstates, block/item models and item definitions, shaped plank recipes, block loot tables preserving custom names, translations, creative-tab registration, axe mineability, guarded-by-piglins entries, and `c:chests`/`c:chests/wooden` tags. The normal chest texture set is limited to this phase; trapped textures are intentionally not registered.

Vanilla's chest special item model is retained for held/inventory rendering, while world rendering uses the variant-aware Charmony renderer. Christmas chest date handling remains vanilla's special-item behavior; no historical Charm-specific override was found that requires a separate implementation.

## Validation

- `:charmony:compileJava`, `:charmony-azalea-wood:compileJava`, `:charmony-ebony-wood:compileJava`, and aggregate `compileJava`: PASS.
- Aggregate `./gradlew build`: PASS.
- JSON resources were generated from the same schema used by the existing barrel resources; the build's resource processing and remapping tasks completed successfully.
- The 1.21.10 client launched with all Charm/Charmony modules enabled. After correcting vanilla-vs-Charmony plank particle namespaces, no chest-specific missing-model or missing-texture warnings remained in the reload log. Existing development-environment authentication/Realms TLS warnings and pre-existing Moobloom texture warnings are unrelated.

Representative interactive checks to run in the existing development world are Oak, Pale Oak, Crimson (or Warped), Azalea, and Ebony: place/open, lid animation, custom name, save/reload, break/drop, hopper input/output, comparator output, matching double chest, and cross-variant non-pairing. The shared implementation preserves these vanilla paths; a GUI automation harness was not available in this environment, so these are the remaining hands-on runtime checks.

## Follow-up

Phase 5B.3 should add trapped chest variants and their redstone-powered behavior, textures, recipes, loot, and renderer materials without changing the normal chest foundation.
