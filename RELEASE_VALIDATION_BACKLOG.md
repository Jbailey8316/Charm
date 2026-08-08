# Release Validation Backlog

Interactive chest tests remain outstanding because GUI automation was unavailable during Phases 5B.2 and 5B.3.

- Normal chests: Oak, Pale Oak, Crimson/Warped, Azalea, and Ebony placement, all facings, opening/lid/sounds, inventory and custom-name save/reload, break/drop, hopper input/output, comparator output.
- Normal double chests: Oak, Pale Oak, Azalea, Ebony matching pairing, 54-slot inventory, both lids, persistence, and breaking one half.
- Normal cross-pairing: Oak + Birch and Azalea + Ebony remain separate.
- Trapped chests: Oak, Pale Oak, Crimson/Warped, Azalea, and Ebony placement, rendering, storage, persistence, hoppers, and comparators.
- Trapped double/cross pairing: matching double behavior; Oak + Birch; Azalea + Ebony; normal + trapped; all remain correctly separated where required.
- Trapped redstone: closed/open/close signal, neighbor response, double trapped behavior, and comparator signal distinction.
- Crafting From Inventory: no-table unavailable; hotbar and main-inventory table access; table removal closes/invalidates the menu; unchanged table count; shaped/shapeless/shift-click/recipe-book crafting; save/reload; config disable/re-enable.
- Crafting exploit checks: drop or container-move the table while open, disconnect/reconnect with the menu open, rapid crafting, and dedicated-server synchronization.
- Totems Work From Inventory: main-hand/offhand vanilla activation, hotbar and main-inventory activation, multiple-stack priority, exact one-stack consumption, disabled-feature behavior, and save/reload.
- Totem interaction checks: Emergency Swap followed by lethal damage, dedicated-server synchronization, disconnect/reconnect, and rapid activation without duplication, stale slots, or unrelated inventory mutation.
- Totem of Preserving death capture: normal death, keepInventory, empty/full inventory, fall/fire/lava/explosion/drowning/void, /kill, vanishing items, nested containers, damaged/enchanted/named/component-rich stacks, and multiple independent deaths.
- Totem of Preserving recovery: inventory-first insertion, safe overflow drops, one-use consumption, malformed data, use spam, simultaneous users, disconnect/restart/dimension travel, chest storage, logout/login, and another-player recovery.
- Suspicious Block Creating: distinctive component-rich item through piston into Sand and Gravel, full stack count, save/reload, brushing once, break-before-brushing, repeated piston/redstone activation, multiple entities, config on/off, and multiplayer synchronization.
- RELEASE BLOCKER — Suspicious Block Falling Item Persistence (Phase 7C fix implemented; still OPEN pending runtime proof): create item-filled Suspicious Sand and Suspicious Gravel, allow each to fall, save/reload, then brush and verify the exact stored ItemStack/components survive without duplication.
- Villager Attracting: main-hand Emerald Block attraction, stop/removal, offhand versus inventory-only behavior, range, obstacles, multiple villagers/players, work/sleep/panic priority, baby/nitwit/employed/unemployed eligibility, feature-disabled behavior, and dedicated-server synchronization.
- Coral Squids: summon/rendering, swimming/flee/ink behavior, bucket pickup and variant persistence, natural warm-ocean/coral spawning plus negative cases, save/reload, dedicated-server synchronization, normal coral drops, five head icons/placement/break persistence, player-kill head drops, Looting scaling, and Mob Drops toggle-off behavior.

## Phase 7B audit follow-up

- The master parity audit records 31 archived Charm features as missing and three as partial; each must receive a scoped parity phase before release. Build success is not runtime validation.
- Keep **RELEASE BLOCKER — Suspicious Block Falling Item Persistence** open until an item-filled falling Suspicious Sand and Suspicious Gravel block are brushed after landing and the exact ItemStack/components are verified.
