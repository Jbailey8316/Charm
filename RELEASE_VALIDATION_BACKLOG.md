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
- Kilns: craft/place/orientation, GUI and recipe-book behavior, clay/glass/brick/terracotta/stone processing, 100-tick cook time, fuel use, XP, hopper input/fuel/output, comparator fullness, save/reload, break-with-inventory, config disable, and dedicated-server synchronization.

## Phase 7B audit follow-up

- The master parity audit records 31 archived Charm features as missing and three as partial; each must receive a scoped parity phase before release. Build success is not runtime validation.
- Keep **RELEASE BLOCKER — Suspicious Block Falling Item Persistence** open until an item-filled falling Suspicious Sand and Suspicious Gravel block are brushed after landing and the exact ItemStack/components are verified.

## Phase 7D Storage Blocks

- Ender Pearl, Gunpowder, and Sugar compact blocks: craft/place/break, exact item counts, models/textures, save/reload, and creative-tab access.
- Ender Pearl Block: portal particles, Silverfish-to-Endermite conversion, config toggle, and dedicated-server behavior.
- Gunpowder Block: falling, lava dissolution, TNT recipe/config toggle, and explosion/Silk Touch behavior.
- Sugar Block: falling, water dissolution, config behavior, and Silk Touch/explosion behavior.

## Phase 7E Recipe Improvements

- Craft each restored recipe and verify ingredients, output count, consumption, remainders, and recipe-book discovery: raw copper/gold/iron block blasting, Gilded Blackstone, Cyan Dye from warped roots, Green Dye from yellow plus blue dye, Snowballs from Snow Block, Quartz from Quartz Block, Clay Balls from Clay Block, the historical two-input Soul Torch recipe, shapeless Bread, and shapeless Paper.
- Verify the vanilla 1.21.10 Bundle recipe remains authoritative (the historical leather bundle recipe is intentionally superseded), and check for no recipe-ID or ingredient collisions.
- Verify each Recipe Improvements toggle disables only its Charm recipe; vanilla recipes remain available. Verify the disabled-by-default recipe-unlocking option awards all recipes on join when enabled.
- Verify the restored normal recipes work in the 1.21.10 Crafter and on a dedicated server.

## Phase 7F Aerial Affinity

- Apply Aerial Affinity I to boots using the normal enchanting table, an enchanted book, and an anvil; verify foot-armor-only applicability and tooltip translation.
- Compare mining speed with no enchantment versus Aerial Affinity while airborne, on the ground, submerged, and airborne while submerged; verify only the historical airborne condition is changed.
- Verify the enchantment is not offered above level I, persists through save/reload, and does not create unintended Aqua Affinity/Efficiency incompatibilities.
- Disable the feature and verify the vanilla airborne penalty returns without affecting other players; test command acquisition and dedicated-server synchronization.

## Phase 7G Animal Armor Enchanting

- Enchant leather, iron, golden, diamond, and copper Horse Armor and Wolf Armor using the intended enchanting-table/book/anvil paths; verify only the historical nine-enchantment set is accepted.
- Equip enchanted armor on horses and wolves; verify protection, fire/blast/projectile protection, thorns, Frost Walker, Feather Falling, Respiration, and Soul Speed behavior where applicable rather than tooltip-only enchantment storage.
- Test durability, damage, save/reload, and server synchronization. Confirm Mending and Unbreaking behavior matches the current animal-equipment implementation and that curses/unsupported enchantments remain rejected.
- Disable Animal Armor Enchanting and verify vanilla applicability returns. Verify enchanted animal armor rendering/glint and the animal-armor advancement.

## Phase 7H Anvils Last Longer

- Run a large controlled sequence of successful anvil operations with the feature enabled; compare observed degradation against the configured 0.50 damage chance (historical/vanilla baseline 0.12), covering anvil, chipped, and damaged transitions and destruction.
- Repeat with the feature disabled and verify the exact vanilla 0.12 probability; confirm creative/infinite-material players remain unaffected.
- Verify XP/output/repair/enchantment/name behavior is unchanged, damaged-anvil destruction events and sounds remain correct, no ghost blocks occur, falling-anvil damage/state is unaffected, and dedicated-server synchronization is clean.

## Phase 7I Item Stacking approval tests (feature not implemented)

- In an isolated test world only, verify save/reload and ON → OFF behavior for oversized enchanted-book, potion, and stew stacks in player inventories, chests, barrels, hoppers, shulker boxes, and item entities; confirm no decode rejection, deletion, or silent overflow occurs.
- Measure ImpulseSV-style 64-stack filters, 16-stack filters, overflow protection, comparator thresholds, hopper locking, multi-item sorters, Crafter transfers, hopper minecarts, and chest minecarts with each historically affected item.
- Test component-rich enchanted books and potion/stew variants for correct merge separation, anvil one-at-a-time processing, eating remainders, crafting remainders, death drops, disconnect/reconnect, and server/client configuration mismatch.
