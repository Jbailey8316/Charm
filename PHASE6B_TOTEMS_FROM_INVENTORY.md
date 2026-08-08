# Phase 6B — Totems Work From Inventory

## Audit and behavior reference

The historical Charm `TotemsWorkFromInventory` feature describes a Totem of
Undying working from anywhere in the player's inventory, in addition to the
main and off hands. Its provider checks main hand first, offhand second, and
then the normal inventory list in slot order. It does not search armor slots,
nested containers, or bundles. Vanilla's death-protection path remains
authoritative and consumes the returned stack.

The historical provider architecture also supports `TotemType.Preserving`.
The current Totem of Preserving module registers that provider for its own
death-inventory-drop workflow; it is not a vanilla `DeathProtection` item and
is not activated by the Undying mixin. Phase 6C remains responsible for its
Grave Mode redesign.

## Current 1.21.10 implementation

`charmony-tweaks` retains the same architecture:

- `LivingEntityMixin` wraps `checkTotemDeathProtection` and supplies a found
  stack when the feature is enabled.
- `TotemInventoryProviders` checks `TotemType.Undying` only and searches main
  hand, offhand, then `Player.getInventory().items`.
- `Handlers` delegates provider selection and does not mutate the stack.
  Vanilla 1.21.10 copies the selected stack, shrinks that exact stack once,
  applies `DeathProtection`, and sends the normal statistics/advancement/event
  updates.
- The existing feature toggle controls provider registration. When disabled,
  the provider list is not consumed and the mixin falls through to vanilla
  hand-only behavior.

This preserves the historical priority and avoids client-side prediction or
custom inventory mutation. Totem Emergency Swap remains separate; no Phase 6B
changes were needed, and the provider returns the current stack references so
the Phase 4C stale-slot repair is not bypassed.

## Supported Totems and scope

| Totem type | Inventory provider | Phase 6B result |
|---|---|---|
| Vanilla Totem of Undying | Yes | Preserved and statically verified |
| Totem of Preserving | Registered by its own module for preservation/death-drop logic | Not activated by this feature; deferred to Phase 6C |

Counted slots are selected hotbar, non-selected hotbar, main inventory, main
hand, and offhand. Armor, nested containers, and bundles are excluded.

## Validation

- Source comparison and bytecode inspection: PASS. The 1.21.10 vanilla method
  shrinks the exact `ItemStack` returned by the mixin, so an inventory-only
  activation consumes one selected totem without touching an unrelated held
  stack.
- Mixin startup/resource smoke test: PASS. The dev client loaded
  `totems_work_from_inventory.LivingEntityMixin` and initialized the common
  feature without Charm mixin or inventory errors.
- Aggregate `./gradlew build`: PASS.
- Main-hand, offhand, hotbar, main-inventory lethal-damage tests; multiple-stack
  priority; slot-by-slot before/after counts; disabled-feature behavior;
  Emergency Swap overlap; dedicated-server synchronization; save/reload; and
  disconnect/rapid-use exploit tests: UNTESTED manually because GUI/gameplay
  automation is unavailable. These are recorded in
  `RELEASE_VALIDATION_BACKLOG.md`.

No production code was changed. Existing external Mojang Realms TLS/Services
warnings remain development-environment noise; no Charm totem, packet,
inventory-sync, or death-event errors appeared in the smoke-test log.

## Follow-up

Complete the listed interactive tests before release, with special attention
to multiple totems, Emergency Swap followed by lethal damage, and dropping or
moving a selected stack during activation. Do not begin Totem of Preserving
Grave Mode work as part of this validation phase.
