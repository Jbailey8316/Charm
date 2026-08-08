# Charm 1.21.10 Restoration Roadmap

## Milestone A — Test JAR

Mandatory scope: retain the current restored baseline, resolve the P0 static
and runtime test gate, and restore Endermite Powder plus Arcane Purpur after
their source audits. The Chiseled Bookshelf model-reference gap must be fixed
before visual testing. Build gate: aggregate build, dev-client initialization,
dedicated-server startup, and resource/tag validation. Manual gate: all
release-critical matrices in `RELEASE_VALIDATION_BACKLOG.md`.

## Milestone B — Production Candidate

Dependencies: Milestone A. Requirements: P0 closed; no known duplication,
item-loss, data-corruption, stale-result, or invalid-trade path; dedicated
server and multiplayer validation; config ON/OFF validation; save/reload
coverage for inventories, entities, villagers, menus, and worldgen; acceptable
resource and client parity. This milestone does not restore intentionally
omitted Item Stacking or reopen Mythas Totem decisions.

## Milestone C — Full Charm Parity

Restore remaining approved P2/P3 systems in dependency order: Atlases, Bat
Buckets, Beacons Heal Mobs, Beekeepers, Colored Sea Lanterns, Doors Open
Together, Echolocation, Item Hover Sorting, Note Block behavior, Player
Pressure Plates, Potion of Radiance, Raid Horns, Redstone Sand, Silence,
Smooth Glowstone, Suspicious Effect Improvements, Tooltip Improvements, and
Waypoints. Each phase requires historical source recovery, current API mapping,
resource/orphan validation, build, and runtime backlog coverage. Item Stacking
and the approved Totem of Preserving Mythas divergence remain excluded.

## Phase proposals

| Phase | Scope | Dependencies | Risk | Test JAR? | Production Candidate? |
|---|---|---|---|---|---|
| 7N | Resolve Chiseled Bookshelf resource gap | current Wood content | Medium | Yes | Yes |
| 7O | Endermite Powder | source audit complete; End structures | High | Yes | Yes |
| 7P | Arcane Purpur | Endermite Powder | High | Yes | Yes |
| 7Q | P0 Suspicious Blocks validation closure | dedicated/runtime harness | Critical | Yes | Yes |
| 7R | Release-critical restored-system matrix | 7Q, current backlog | Critical | Yes | Yes |
| 8A+ | Remaining P2/P3 systems | per-feature audits | Low–Medium | No | No |

Every phase must stop on an unresolved data-integrity or dependency issue.
