# Phase 7M — Orphan and Dependency Report

## Confirmed dependencies

```text
Woodcutting recipes
  -> Woodcutter block/menu/payloads
    -> Woodcutter POI
      -> Lumberjack profession/trades

Custom Ladders -------------> Lumberjack categorical trade
Custom Barrels -------------> Lumberjack categorical trade
Custom Chiseled Bookshelves -> Lumberjack categorical trade

Firing recipe type ---------> Kiln block entity/menu/recipes
Endermite Powder -----------> Arcane Purpur recipes and target blocks
```

## Dependency classifications

| Dependency | Classification | Finding |
|---|---|---|
| Woodcutting -> Woodcutter | PRESENT | Dedicated recipe/menu path; interactive transaction tests pending |
| Woodcutter -> Lumberjack | PRESENT | Native POI/profession registration; villager tests pending |
| Custom wood tags -> Lumberjack | PRESENT | Ladder, Chiseled Bookshelf, and merged barrel tags resolve |
| Firing -> Kiln | PRESENT | `charm:firing` is registered and consumed by Kiln |
| Endermite Powder -> Arcane Purpur | PRESENT | Arcane Purpur recipes and target blocks resolve; interactive teleport tests pending |

## Orphan report

### Confirmed

- Resolved in Phase 7N: all fourteen Chiseled Bookshelf blockstates now point
  to the existing `<family>_chiseled_bookshelf.json` base models. The six empty
  and six occupied slot model sets remain valid. Interactive visual and
  inventory validation is still pending.
- The 21 top-level missing features have no current production registration;
  their historical resources are therefore intentionally absent rather than
  accidentally orphaned.

### Not confirmed

- No orphaned Lumberjack trade tag was found.
- No orphaned Woodcutting output was found after the Bookshelf/Ladder phases.
- No orphaned Firing/Kiln recipe type was found.
- Resolved in Phase 7O: Endermite Powder item, structure tag, locator entity,
  drop hook, sound, and rare trader offer are registered. The historical
  advancement parent `charm:block_of_ender_pearls/convert_silverfish` remains
  a documented deferred dependency; no dangling advancement JSON was added.
- Resolved in Phase 7P: all six Arcane Purpur blocks/items, recipes, loot,
  models, textures, and `charmony:chorus_teleports` tag. The historical
  advancement remains deferred because its Endermite Powder parent chain is
  not restored.

## Cross-feature risks

The highest-risk dependency chains are Woodcutter transient inventory
transactions, Chiseled Bookshelf inventory persistence, Kiln furnace inventory,
Suspicious Block persistence, and Totem of Preserving death inventory. These
are not declared runtime-safe from static compilation alone.
