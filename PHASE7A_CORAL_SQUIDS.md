# Phase 7A — Coral Squids

## Audit and restoration decision

The current 1.21.10 port had no Coral Squid classes, registrations, resources, entity renderer, spawn tag, bucket, spawn egg, loot, or configuration. The historical Charm 1.21.1 source was recoverable locally, so the feature was restored using the existing Charm/Charmony registration and client-feature architecture.

## Historical behavior

Coral Squid was a distinct `EntityType` and a custom `WaterAnimal`/`Bucketable`, not a renamed vanilla Squid. It used a 0.54 × 0.54 hitbox, water-ambient category, a maximum natural group size of four, vanilla squid-like movement and sounds, random movement, and a short-range flee goal after being hurt. It had no baby state, breeding, feeding, glow, color-changing, or coral interaction beyond its spawn predicate.

There are exactly five variants, selected randomly for natural spawns and synchronized/persisted by integer ID:

| Variant | Texture | Normal coral drop |
| --- | --- | --- |
| Tube | `textures/entity/coral_squid/tube.png` | Tube Coral |
| Brain | `textures/entity/coral_squid/brain.png` | Brain Coral |
| Bubble | `textures/entity/coral_squid/bubble.png` | Bubble Coral |
| Fire | `textures/entity/coral_squid/fire.png` | Fire Coral |
| Horn | `textures/entity/coral_squid/horn.png` | Horn Coral |

Natural spawning is added to the `minecraft:warm_ocean` biome tag with weight 50 and groups of 2–4. The spawn predicate requires Y > 20, below sea level, water placement, and a `BaseCoralPlantTypeBlock` somewhere from the spawn position down through the next 15 blocks. This preserves the “near coral in warm oceans” ecological intent and rejects non-warm/non-coral locations. Natural entities may despawn when far away; bucketed or named entities persist.

The original configurable normal drop is retained: a variant’s corresponding coral item drops with the feature’s default 0.2 probability. Bucket pickup preserves the variant in `BUCKET_ENTITY_DATA`; entity save/load preserves it in `Variant`. The historical wandering-trader bucket offer and bucket-fill sound are restored.

## 1.21.10 architecture

Common code is under `svenhjol.charm.common.features.coral_squids` and is registered by the root Charm mod. A separate client feature registers a model layer and render-state-based `MobRenderer`, adapted to the 1.21.10 renderer API. The renderer uses the five local Charm textures and preserves tentacle animation, body rotation, and squid-style bobbing.

The root Charm mod now declares both Common and Client sides so the client renderer can be booted. No Villager Attracting or unrelated module code was changed.

## Coral Squid heads

Five native Charm head blocks/items were added: Tube, Brain, Bubble, Fire, and Horn Coral Squid Head. Each uses a reusable directional head block, local Charm coral-squid art copied into the block atlas as required by Minecraft’s block-model renderer, per-variant block/item models, creative-tab entries, and translations. Placing and breaking a head preserves its variant because each variant is a separate registered block/item.

Head drops are attached to the existing `EntityKilledDropCallback` path used by the Charm Mob Drops framework. The callback checks the `charmony-tweaks:mob_drops` feature toggle, requires a `ServerPlayer` kill, and leaves the Coral Squid’s normal coral drop untouched. The verified Vanilla Tweaks More Mob Heads reference for the closest equivalent (Squid) is a **5% base chance plus 1 percentage point per Looting level** (8% at Looting III), with a player-kill requirement. Reference: https://www.reddit.com/r/HermitCraft/comments/g1t9hj

## 1.21.10 migrations

- `MobSpawnType` was migrated to `EntitySpawnReason`.
- Entity persistence uses `ValueInput`/`ValueOutput`; bucket component data continues to use `CompoundTag` through `Bucketable`.
- Client rendering uses `EntityRenderState`, `EntityModel`, and the current `MobRenderer` hooks rather than the removed entity-generic renderer signatures.
- Server-side item spawning uses the 1.21.10 `spawnAtLocation(ServerLevel, ItemLike)` path.
- Resource identifiers use the port’s `charmony` namespace, matching the recovered Charm storage assets and registry helper.

## Validation

Static validation confirms all five variants are registered exactly once, all referenced Java classes/resources exist, and the root build processes the new resources. `./gradlew build` passes.

The dev client reached its normal running state. Logs show both `Running common feature CoralSquids` and `Running client feature CoralSquids`; after correcting atlas namespace/resource issues, no Coral Squid missing-texture or missing-sound warnings remain. Authentication/Realms PKIX errors and Netty reflective-access diagnostics are external development-environment warnings.

Gameplay automation was unavailable. The following remain **UNTESTED** and are listed in `RELEASE_VALIDATION_BACKLOG.md`: `/summon` rendering, swimming/flee/ink behavior, bucket pickup and variant round-trip, natural warm-ocean/coral spawning and negative cases, save/reload, dedicated-server synchronization, normal coral drops, all five head icons/placement/break persistence, deterministic player-kill head drops, Looting scaling, and Mob Drops toggle-off behavior.

## Files changed

Root Charm Java/client classes, Coral Squid common/client feature code, five head registrations and models, entity/bucket/head textures, sound and item definitions, the warm-ocean spawn tag, English translations, this document, and the release-validation backlog were changed. No nested module source was modified.

