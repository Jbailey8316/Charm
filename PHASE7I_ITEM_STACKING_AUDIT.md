# Phase 7I — Item Stacking Compatibility and Safety Audit

## Scope and decision

This phase is audit-only. No Item Stacking production code, configuration,
stack-size override, mixin, or resource was added. The feature remains
`MISSING` and the completely-missing count remains 28.

The historical Charm implementation is recoverable, but enabling it on an
established Mythas server is not safe without an explicit default-off design
and isolated migration tests. Recommendation: **E — requires further isolated
testing before decision**, with a likely eventual architecture of **B — restore
default OFF with additional safeguards**.

## Historical implementation

Historical Charm 1.21.1 registered `item_stacking` as a common feature and
overrode `DataComponents.MAX_STACK_SIZE` on eight vanilla items when enabled.
It also patched `AnvilMenu` so stacked enchanted books were processed one book
at a time, and patched player eating to preserve a container-conversion result
when the enlarged stack had no inventory space. The historical feature was
enabled by the normal feature loader and had no safety-oriented default-off
guard.

Historical configured limits were:

| Item | Historical vanilla max | Historical Charm max |
|---|---:|---:|
| Enchanted Book | 1 | 16 |
| Potion | 1 | 16 |
| Splash Potion | 1 | 1 |
| Lingering Potion | 1 | 1 |
| Beetroot Soup | 1 | 16 |
| Mushroom Stew | 1 | 16 |
| Rabbit Stew | 1 | 16 |
| Suspicious Stew | 1 | 1 |

The last three unchanged-maximum entries are still part of the historical
affected list because Charm explicitly overrides them. No boats, minecarts,
buckets, tools, armor, beds, signs, or other max-1 items were changed by this
feature. No broad item tag was used; the list was explicit and each limit was
configurable from 1 through 64.

The historical anvil patch is behaviorally significant: a stack of enchanted
books is charged and consumed one at a time rather than triggering the vanilla
single-item/stacked-book mismatch. This is not a simple max-stack override.

## Minecraft 1.21.10 matrix

All eight items still exist and have the same vanilla maximums in 1.21.10:

| Item | Historical vanilla | Charm max | 1.21.10 vanilla | Classification | Risk |
|---|---:|---:|---:|---|---|
| Enchanted Book | 1 | 16 | 1 | STILL NEEDS CHARM CHANGE | HIGH |
| Potion | 1 | 16 | 1 | STILL NEEDS CHARM CHANGE | HIGH |
| Splash Potion | 1 | 1 | 1 | VANILLA SUPERSEDED | LOW |
| Lingering Potion | 1 | 1 | 1 | VANILLA SUPERSEDED | LOW |
| Beetroot Soup | 1 | 16 | 1 | STILL NEEDS CHARM CHANGE | MEDIUM |
| Mushroom Stew | 1 | 16 | 1 | STILL NEEDS CHARM CHANGE | MEDIUM |
| Rabbit Stew | 1 | 16 | 1 | STILL NEEDS CHARM CHANGE | MEDIUM |
| Suspicious Stew | 1 | 1 | 1 | VANILLA SUPERSEDED | LOW |

Minecraft 1.21.10 represents the limit through the `minecraft:max_stack_size`
data component (the `DataComponents.MAX_STACK_SIZE` Java component). An
`ItemStack` reads its effective limit from that component; it is not a
serialized per-stack permission to exceed the item limit. Components still
participate in stack equality, so potion contents, stew effects, names, and
other component data must match before stacks merge. The strict ItemStack codec
validates `count <= getMaxStackSize()` and returns a `DataResult` error when an
oversized stack is decoded.

## Automation and sorting impact

Changing a limit changes both inventory capacity and the quantities moved by
vanilla transfer code. Hoppers, hopper minecarts, droppers, dispensers,
chests, barrels, shulker boxes, bundles, Crafters, item entities, and recipe
consumption all use the effective stack maximum or stack count. The likely
effects are:

* Enchanted Books: high risk. Comparator fullness and batch transfer change;
  standard 64-stack ImpulseSV filters can unlock at different counts, and
  anvil processing must remain one-book-at-a-time. Component-different books
  do not merge, but equal enchanted-book components can reach 16 per slot.
* Potions: high risk. Potion components must match, but 16-capacity slots alter
  hopper throughput, comparator thresholds, brewing/storage assumptions, and
  overflow protection. Splash and lingering potions are unchanged.
* Beetroot, Mushroom, and Rabbit Stew: medium risk. Their 16-capacity slots
  alter storage capacity, hopper batches, comparator signals, and food-stack
  transfer. Eating has container conversion/remainder behavior.
* Suspicious Stew: low for stack size because its historical limit remains 1,
  but it remains component-bearing and must not be broadened accidentally.

Standard ImpulseSV filters, overflow-protected filters, filters designed for
16-stack items, multi-item sorters, hopper locking, and comparator fullness
thresholds all encode assumptions about counts per slot. Enabling the feature
can cause filter bleed, early/late unlocks, changed overflow behavior, and
different storage capacity. Unstackable-item sorters are not directly changed
by this historical list.

## Components and item integrity

The historical list does not make damaged tools, armor, containers, boats, or
entity buckets stackable. For the affected items, normal ItemStack component
equality remains the merge gate. Two potions with different potion contents,
custom names, or custom components must not merge; two enchanted books with
different enchantment components must not merge. Any future implementation
must test component-rich and modded-component stacks rather than relying only
on item identity.

## Existing worlds and ON → OFF

Changing an `Item`'s prototype component does not proactively rewrite every
existing player, container, or item-entity stack. Existing stacks are therefore
not safely migrated just by toggling a config. Subsequent insertion/extraction
uses the then-current effective maximum.

The 1.21.10 `ItemStack.validateStrict` path explicitly rejects a decoded stack
whose count exceeds the current `getMaxStackSize()`. Therefore a stack of 53
enchanted books created while Charm is enabled is not a valid strict stack when
the feature is later disabled (limit 1). The exact higher-level outcome depends
on the player/container codec caller—decode failure, skipped data, or load
failure are all materially unsafe—but silent automatic splitting is not
provided by the ItemStack codec. This is a release-risk finding, not a claim
that production behavior has been changed. Isolated save/reload tests are
required for player inventories, chests, barrels, hoppers, shulkers, and item
entities before any implementation approval.

The same oversized-stack risk affects manual movement, shift-click, hopper
insert/extract, chest transfers, dropping/pickup, death drops, crafting,
disconnect/reconnect, and restart. A safe implementation must migrate or
reject oversized stacks explicitly before disabling, rather than relying on
vanilla codecs.

## Client/server and economy considerations

The effective item component must be identical on the authoritative server and
client. A server-only override risks ghost stacks, rejected clicks, and
incorrect slot rendering; a client-only override is not authoritative and can
permit desynchronization. Configuration should be server-owned and require a
restart (or a controlled world migration), not a live toggle or ordinary
datapack reload.

Larger potion/book/stew stacks change physical shop capacity, job/barrel
storage, player carrying capacity, and future physical Mythas Coin storage.
The direct performance cost is small, but changed hopper throughput and larger
per-slot batches can materially alter redstone tick behavior and server load.

## Duplication and item-loss analysis

The credible hazards are oversized-stack rejection/loss on load, mismatched
client/server limits, hopper filter bleed, and anvil consumption bugs when
stacked enchanted books are not processed one at a time. Additional tests must
cover crafting remainders, eating container conversions, death drops, item
entities, disconnects, restarts, and toggling. No production duplication fix
was attempted in this audit.

## Proposed eventual architecture

If approved after isolated testing:

1. Add a Charm `ItemStacking` common feature with an explicit **default OFF**
   master toggle and per-item limits.
2. Apply `MAX_STACK_SIZE` only on the server and matching client runtime before
   inventories are opened; require a server restart for changes.
3. Preserve the historical explicit item list, omitting entries whose Charm
   limit equals vanilla (splash, lingering, suspicious stew) unless parity
   requires retaining their no-op declarations.
4. Port the enchanted-book anvil safeguards and eating-remainder handling only
   after dedicated tests prove their 1.21.10 hooks.
5. Provide a migration/guard for oversized stacks before ON → OFF; never
   silently clamp or delete overflow.

Recommended warning text:

> Changes maximum stack sizes for selected vanilla items. May affect hopper
> filters, sorting systems, comparator outputs, and existing oversized stacks.
> Disabled by default; changing this setting requires a server restart and an
> inventory migration review.

## Validation and status

Static 1.21.10 mapping inspection confirms the component-based maximum and
strict count validation described above. No production files were modified.
Interactive automation/filter tests and isolated oversized-stack save/reload
tests remain UNTESTED and are listed in `RELEASE_VALIDATION_BACKLOG.md`.
The baseline aggregate `./gradlew.bat build` is run for this audit and must
remain successful.

