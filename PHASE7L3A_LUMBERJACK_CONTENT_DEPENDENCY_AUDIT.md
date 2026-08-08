# Phase 7L.3A — Lumberjack content dependency audit

## Historical result

The local `charm-1.21.1.zip` contains the common wood-family infrastructure
under `feature/core/custom_wood`, not a separate Bookshelves or Ladders feature.
`BookshelfHolder`, `ChiseledBookshelfHolder`, and `LadderHolder` register the
blocks/items for each `CustomWoodDefinition`. The historical Lumberjack
feature references these through tags when its custom trade toggles are true.

## Content matrix

| Content | Historical variants | Families |
|---|---:|---|
| Bookshelf | 13 | Acacia, Azalea, Bamboo, Birch, Cherry, Crimson, Dark Oak, Ebony, Jungle, Mangrove, Oak, Spruce, Warped |
| Chiseled bookshelf | 13 | Same 13 families |
| Ladder | 13 | Same 13 families |

The historical vanilla-family set is 11 families (Acacia, Bamboo, Birch,
Cherry, Crimson, Dark Oak, Jungle, Mangrove, Oak, Spruce, Warped), plus Azalea
and Ebony. Pale Oak did not exist historically. No bookshelf or ladder has a
separate custom machine or menu.

## Behavior and resources

Custom Bookshelf extends the vanilla Bookshelf block and preserves enchanting
power, placement, piston behavior, and normal block semantics. It adds Charm
flammability and furnace fuel through the material provider. Custom Chiseled
Bookshelf extends the vanilla chiseled bookshelf and preserves its block entity
inventory behavior, adding material fuel behavior. Custom Ladder extends the
vanilla LadderBlock, preserving support, climbing, orientation, and
waterlogging; it adds material fuel behavior. Historical resources include
per-family blockstates, models, item models, recipes, loot tables,
translations, creative-tab placement, and textures. The archive contains 13
single bookshelf textures and 13 ladder textures, plus the chiseled bookshelf
texture sets.

## Lumberjack dependency

Historical configuration defaults are all true:

* `customBarrels`: tier-4 barrel trade uses the Charm barrel tag;
* `customBookshelves`: tier-4 bookshelf trade uses the Charm chiseled-bookshelf tag;
* `customLadders`: novice ladder trade uses the Charm ladder tag.

When false, the source explicitly falls back to vanilla `BARREL`,
`BOOKSHELF`, and `LADDER` outputs. Other trade values are unchanged. The
custom trades use normal emerald costs, max uses, XP, and random tag selection.

## Current port and parity cross-check

The current port has no Bookshelf, Chiseled Bookshelf, or Ladder registrations,
holders, custom block classes, textures, models, recipes, or Charm tags. It
does have Woodcutting recipes producing the missing ladder IDs, so those
recipes are currently orphaned. Existing custom barrel tags/storage do not
provide the missing content. The 71-row master audit records the broader
`Wood` row as PARTIAL; it does not enumerate Bookshelves or Ladders as
separate user-facing rows. Therefore the missing-feature count of 23 is not
increased mechanically, but the audit has omitted substantial missing
subcontent and should note it in the restoration roadmap.

## Pale Oak recommendation

Pale Oak should receive semantic-parity Bookshelf, Chiseled Bookshelf, and
Ladder variants because the historical architecture is categorical across
vanilla wood families and the project already approved the same extension for
storage and Woodcutting. This requires new Charm-style Pale Oak textures,
models, recipes, loot, tags, translations, and creative ordering. It should be
implemented together with the historical 13-family content rather than added
as an isolated trade workaround.

## Complexity and recommendation

Bookshelves/Chiseled Bookshelves: MEDIUM — shared custom block/entity holders,
material fuel/flammability, block models, recipes, loot, tags, and client
resources. Ladders: MEDIUM — shared LadderBlock holder, placement/waterlogging
resources, fuel, recipes, loot, tags, and creative integration. Existing
Charmony wood registries and material abstractions are reusable, but the
current registry API does not yet expose these holder types.

Classification: **A — RESTORE CONTENT FIRST**. Lumberjack should not be
implemented with a lossy fallback while its default custom trade options point
at absent content. Restore the wood-family content and tags first, then port
the exact profession/trades.

No production code was changed in Phase 7L.3A.
