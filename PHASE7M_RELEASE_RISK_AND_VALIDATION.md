# Phase 7M — Release Risk and Validation

## P0

`Suspicious Block Falling Item Persistence` remains P0. The static fix exists,
but falling, save/reload, repeated falls, component preservation, brushing,
and duplication tests have not been completed. No second confirmed P0 was found
by static audit.

## Release-critical untested systems

- Suspicious Block Creating persistence and duplication safety.
- Woodcutter normal/quick-move/close/disconnect/block-removal/reload paths.
- Chiseled Bookshelf insertion/removal, hopper, comparator, break, and reload.
- Kiln inventory, hopper, comparator, XP, and reload paths.
- Totem of Preserving death/overflow/fire/lava/multiplayer persistence.
- Lumberjack profession acquisition, five tiers, randomized offer persistence,
  restocking, and config fallbacks.
- Copper Piston movement and rapid-pulse safety.
- Storage Blocks hopper/comparator/dissolution/conversion safety.

## P1/P2/P3 reclassification

P1: Arcane Purpur, Endermite Powder, Storage Blocks runtime safety, Woodcutter
runtime safety, Lumberjack runtime safety, Aerial Affinity, Animal Armor
Enchanting, Anvils Last Longer, Recipe Improvements, and Totem of Preserving
validation.

P2: the remaining missing normal gameplay systems (Atlases, Bat Buckets,
Beacons Heal Mobs, Beekeepers, Colored Sea Lanterns, Doors Open Together,
Echolocation, Item Hover Sorting, Note Blocks, Player Pressure Plates, Potion of
Radiance, Raid Horns, Redstone Sand, Silence, Smooth Glowstone, Suspicious
Effects, Tooltip Improvements, Waypoints), plus ordinary runtime validation.

P3: cosmetic/client/audio polish and the Chiseled Bookshelf model naming gap.

## Test-JAR matrix additions

The eventual Test JAR must include the Suspicious Blocks, Woodcutter,
Lumberjack, Totem, Moobloom, Coral Squid, Kiln, Wood Storage,
Bookshelf/Chiseled Bookshelf, Ladder, Copper Piston, and all restored feature
runtime cases listed in the release backlog. Any duplication, deletion,
stale-result, invalid trade, or save/load failure is a release blocker.
