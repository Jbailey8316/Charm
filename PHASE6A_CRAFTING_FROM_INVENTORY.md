# Phase 6A — Crafting From Inventory

## Original behavior

The archived Charm implementation (`feature/crafting_from_inventory`, based on
the historical 1.21.1 source) provided a portable vanilla 3×3 crafting table.
It checked for a crafting-table item in the main hand, offhand, or ordinary
inventory, and opened a server-side `CraftingMenu` through a V keybind and an
inventory-screen button. The item was never consumed. The historical source
used the `charm:crafting_tables` item tag and a server packet before opening the
menu. Nested containers were not searched.

References: the archived Charm documentation at
<https://web.archive.org/web/20260216024040/https://charmony.work/> and the
historical `CraftingFromInventory`, `Handlers`, `Networking`, `Menu`, and client
classes in Charm's `1.21.1-fabric` source.

## Port mismatch

Before this phase, the port exposed `CraftingTableNearby`: a client-only V-key
workflow that searched for a *placed* crafting table within a short radius and
used it. Carrying a crafting table did not provide portable crafting, so it did
not match the original feature.

## 1.21.10 implementation

The feature was restored in `charmony-tweaks` using one common/server feature
and one client feature:

- `CraftingFromInventory.Handlers` checks main hand, offhand, and every stack in
  the normal player inventory against the `charmony:crafting_tables` tag.
- The client V key (`key.charmony.open_portable_crafting`, default V) and an
  inventory-screen button send a C2S `open_portable_crafting` payload.
- The server validates possession again, then opens the vanilla
  `CraftingMenu` with `ContainerLevelAccess.NULL`; no client-side crafting or
  item creation is used.
- The custom menu re-checks possession in `stillValid`, so dropping or moving
  the last table invalidates the portable menu. Vanilla menu removal handles
  remaining crafting-grid contents.
- The existing common feature toggle controls the behavior. The client feature
  does not register/use the controls when the common feature is disabled.
- Only ordinary carried stacks are searched; tables inside other containers or
  bundles are not treated as possession.

The feature leaves placed vanilla crafting tables, recipes, and unrelated
menus unchanged. An advancement records successful portable-table use.

## Files changed

In `modules/charmony-tweaks`:

- Added common/client crafting-from-inventory feature, menu, packet,
  registration, tag, and advancement classes/resources.
- Replaced the nearby-table client feature and its HUD/handlers/registration.
- Updated English translations and feature initialization.

At the root:

- Updated `RELEASE_VALIDATION_BACKLOG.md` with the remaining interactive and
  exploit checks.

## Validation

- `:charmony-tweaks:compileJava`: PASS.
- Aggregate `./gradlew build`: PASS.
- Dev client launch/resource smoke: PASS. The log shows both common and client
  `CraftingFromInventory` features initializing and no portable-crafting,
  screen-handler, or packet errors.
- Full GUI/gameplay checklist (hotbar, main inventory, removal, recipes,
  shift-click, recipe book, persistence, config toggle, dedicated server, and
  exploit cases) remains manual/UNTESTED because GUI automation is unavailable;
  it is retained in `RELEASE_VALIDATION_BACKLOG.md`.

Known log noise remains limited to previously documented development/network
warnings (for example Mojang authentication TLS/Services-key messages and
existing Moobloom texture warnings); no new Charm crafting errors were found.

## Recommended manual checks

With the feature enabled, test no-table, hotbar, main-inventory, removal,
repeated crafting, recipe-book/shift-click, save/reload, and configuration
toggle behavior. While the menu is open, drop the table or move it into another
container, then test disconnect/reconnect and rapid crafting for duplication,
ghost-output, or item-loss issues. Repeat against a dedicated server when
available.
