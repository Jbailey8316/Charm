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
Endermite Powder -----------> Arcane Purpur recipe and locator mechanic
```

## Dependency classifications

| Dependency | Classification | Finding |
|---|---|---|
| Woodcutting -> Woodcutter | PRESENT | Dedicated recipe/menu path; interactive transaction tests pending |
| Woodcutter -> Lumberjack | PRESENT | Native POI/profession registration; villager tests pending |
| Custom wood tags -> Lumberjack | PRESENT | Ladder, Chiseled Bookshelf, and merged barrel tags resolve |
| Firing -> Kiln | PRESENT | `charm:firing` is registered and consumed by Kiln |
| Endermite Powder -> Arcane Purpur | MISSING prerequisite | Both production systems remain absent |

## Orphan report

### Confirmed

- Fourteen Chiseled Bookshelf base model references are unresolved because the
  blockstate naming convention and model filenames disagree.
- The 21 top-level missing features have no current production registration;
  their historical resources are therefore intentionally absent rather than
  accidentally orphaned.

### Not confirmed

- No orphaned Lumberjack trade tag was found.
- No orphaned Woodcutting output was found after the Bookshelf/Ladder phases.
- No orphaned Firing/Kiln recipe type was found.

## Cross-feature risks

The highest-risk dependency chains are Woodcutter transient inventory
transactions, Chiseled Bookshelf inventory persistence, Kiln furnace inventory,
Suspicious Block persistence, and Totem of Preserving death inventory. These
are not declared runtime-safe from static compilation alone.
