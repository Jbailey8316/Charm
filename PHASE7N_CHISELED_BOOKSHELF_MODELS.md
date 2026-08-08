# Phase 7N — Chiseled Bookshelf Model References

## Scope and root cause

Phase 7M identified a resource-only defect in all 14 custom Chiseled
Bookshelf blockstates. Their four facing entries referenced base models using
`charmony:block/chiseled_<family>_bookshelf`, but the registered base model
resources are named `charmony:block/<family>_chiseled_bookshelf`.

The occupancy models intentionally retain the separate vanilla-compatible
`chiseled_<family>_bookshelf_<empty|occupied>_slot_*` naming and were not
renamed.

## Repaired variants

| Family | Correct base model | Occupancy model set |
|---|---|---|
| Acacia | `charmony:block/acacia_chiseled_bookshelf` | six empty + six occupied slot models |
| Bamboo | `charmony:block/bamboo_chiseled_bookshelf` | six empty + six occupied slot models |
| Birch | `charmony:block/birch_chiseled_bookshelf` | six empty + six occupied slot models |
| Cherry | `charmony:block/cherry_chiseled_bookshelf` | six empty + six occupied slot models |
| Crimson | `charmony:block/crimson_chiseled_bookshelf` | six empty + six occupied slot models |
| Dark Oak | `charmony:block/dark_oak_chiseled_bookshelf` | six empty + six occupied slot models |
| Jungle | `charmony:block/jungle_chiseled_bookshelf` | six empty + six occupied slot models |
| Mangrove | `charmony:block/mangrove_chiseled_bookshelf` | six empty + six occupied slot models |
| Oak | `charmony:block/oak_chiseled_bookshelf` | six empty + six occupied slot models |
| Pale Oak | `charmony:block/pale_oak_chiseled_bookshelf` | six empty + six occupied slot models |
| Spruce | `charmony:block/spruce_chiseled_bookshelf` | six empty + six occupied slot models |
| Warped | `charmony:block/warped_chiseled_bookshelf` | six empty + six occupied slot models |
| Azalea | `charmony:block/azalea_chiseled_bookshelf` | six empty + six occupied slot models |
| Ebony | `charmony:block/ebony_chiseled_bookshelf` | six empty + six occupied slot models |

All 14 blockstates parse, all base and occupancy references resolve to model
JSON, and all Charm texture references in the affected model graph resolve to
PNG resources. No circular parent was introduced. The erroneous base-reference
pattern has no remaining matches in the affected blockstates.

## Files and behavior

The production repair changes only the 14 Chiseled Bookshelf blockstate JSON
files. Block entities, six-slot storage, comparator/enchanting behavior,
recipes, loot, tags, Woodcutting, and Lumberjack trades are unchanged.

Before the repair, client resource loading reported 14 missing base-model
warnings. After the repair, the known Chiseled Bookshelf model warnings are
absent; unrelated pre-existing client warnings remain documented separately.

Compilation, aggregate build, and client resource initialization are PASS.
Interactive placement, occupancy, comparator, hopper, save/reload, and
break-with-contents tests remain UNTESTED and release validation is still
required.
