# Phase 6C — Mythas Grave Mode Totem of Preserving

## Historical audit

Historical Charm exposed two modes. In normal mode a player needed an empty
Totem of Preserving (in hand or, when configured, inventory) at death; the
totem stored the player inventory and could be used repeatedly according to a
durability setting. Echo Shards repaired that durability through an anvil. In
Grave Mode the mod created a holder at every eligible death without a
prerequisite totem, stored the inventory in the holder, protected the holder
from block replacement/explosions, and released the contents when a player
entered it. The holder used the deceased player's scoreboard name and UUID,
and its position search moved void deaths to a recoverable height.

The historical/current callback is invoked from `Player.dropEquipment` around
`Inventory.dropAll()`. Consequently it runs on an actual death, after vanilla
has applied keep-inventory and vanishing-item rules, and returning `SUCCESS`
prevents ordinary inventory drops. This is the authoritative server-side
capture point.

## Mythas adaptation

Grave Mode now defaults to enabled. A death with `keepInventory=true` does not
invoke the inventory-drop callback, so no duplicate Totem is created. With
keep-inventory disabled, the callback captures the complete stacks that would
otherwise drop: normal inventory, armor, and offhand, including components,
damage, enchantments, names, lore, container contents, and modded data. The
existing `ItemStack` codec/data-component storage is retained; no item-ID-only
conversion is used. Vanishing-prevented items are already excluded by vanilla
before the callback.

Exactly one holder/Totem is created per successful death capture. Failed
placement leaves the normal drop path available. Each holder has independent
serialized contents, so later deaths do not invalidate older recoveries.

The deceased player's scoreboard name remains in the Totem data and appears
in its tooltip. `ownerOnly` remains configurable but defaults false, so any
player may recover a Totem. The holder's existing protection remains in place:
block replacement/removal is intercepted, explosions do not destroy it, and
the position search avoids lava/solid blocks and relocates void deaths toward
the world's sea level or another valid nearby position. It is a persistent
block entity rather than an immortal item entity; normal block persistence
applies and no natural item despawn timer is involved.

## Recovery and single-use behavior

Right-clicking a populated Totem on the server copies each stored stack into
the recovering player's inventory using vanilla `placeItemBackInInventory`.
That helper fills compatible stacks/free slots first and safely drops any
remainder at the player's current location. Only after all stacks have been
processed is the held Totem count set to zero. Empty/malformed data is not
consumed. Server-thread execution and the emptied source stack prevent replay
or double recovery; client-side use only performs normal prediction/feedback.

The Mythas Totem is non-stackable, has no durability, and is not repairable.
The Totem-specific Echo Shard anvil repair handler and repairable item property
were removed. Echo Shards remain unchanged vanilla items. The historical
crafting recipe still uses Echo Shards as its ordinary crafting ingredient; no
repair interaction consumes or modifies them.

Legacy normal-mode configuration and inventory-provider plumbing remain in the
source for compatibility, but the Mythas/default setting is Grave Mode and
the durability/repair system is gone. Loot-table/trader additions remain
disabled in Grave Mode as in the historical implementation.

## Files changed

In `modules/charmony-totem-of-preserving`:

- `TotemOfPreserving.java`: Grave Mode default enabled; removed durability
  configuration/API.
- `TotemItem.java`: removed durability and Echo Shard repair properties.
- `Registers.java`: removed Anvil repair event registration.
- `Handlers.java`: removed repair/durability flow; inventory-first recovery;
  single-use server consumption.
- `TotemBlockEntity.java`: removed obsolete durability persistence.

At the root, `RELEASE_VALIDATION_BACKLOG.md` records the remaining death,
recovery, persistence, and duplication tests.

## Validation

- `:charmony-totem-of-preserving:build`: PASS.
- Aggregate `./gradlew build`: PASS (run after implementation).
- Dev client/resource smoke: PASS. Totem holder mixin applied and
  `TotemOfPreserving` initialized without Charm holder, serialization, or
  recovery errors.
- Automated death/recovery, keep-inventory, overflow, environmental, void,
  multiplayer, restart, disconnect, and duplication scenarios: UNTESTED
  manually because gameplay/GUI automation is unavailable. They remain in the
  release backlog and must be completed before release.

Known log noise is limited to external Mojang Realms TLS/Services failures;
no new Charm Totem exceptions or packet/inventory errors were observed.
