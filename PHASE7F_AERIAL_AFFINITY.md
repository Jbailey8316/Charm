# Phase 7F — Aerial Affinity

## Historical behavior

Charm's historical Aerial Affinity was a level-I boots enchantment. It added a
player attribute while the enchanted boots were equipped. When the player was
not on the ground, Charm multiplied the current block-destruction speed by
five, restoring normal mining speed from Minecraft's airborne penalty. The
effect did not apply on the ground and did not inspect water separately. It
was not treasure-only and used the normal foot-armor enchantment category with
weight 1, anvil cost 4, and maximum level 1. The historical source registered
the player attribute and handled the speed adjustment through its block-break
speed callback; it also triggered the `used_aerial_affinity` advancement.

## 1.21.10 architecture

Minecraft 1.21.10 uses data-driven enchantment definitions. The Charm
definition at `data/charm/enchantment/aerial_affinity.json` preserves the
historical costs, weight, level, feet slot, and `minecraft:enchantable/foot_armor`
supported-items tag. Its attribute effect adds 1.0 to the registered
`charm:player.aerial_mining_speed` attribute when boots carry the enchantment.

A narrow Charm `Player#getDestroySpeed` return hook reads that attribute and,
only when the feature is enabled and `Player.onGround()` is false, multiplies
the already calculated speed by 5.0. This is the direct 1.21.10 equivalent of
the historical block-break-speed callback and leaves ground mining and players
without the enchantment unchanged. The hook is server/client-safe because it
modifies the vanilla calculation rather than creating a client-only action.
The historical action advancement is triggered through Charmony's existing
`charmony_action_performed` infrastructure when the effect is used.

## Configuration and compatibility

Aerial Affinity is a normal Charm common feature and follows the existing
feature toggle. When disabled, the hook returns the vanilla speed unchanged;
the enchantment data remains harmless but grants no Charm effect. It is level I
only, applies to foot armor, and has no added incompatibility with Aqua
Affinity or Efficiency beyond vanilla enchantment rules.

## Files/resources changed

- `src/main/java/svenhjol/charm/common/CommonInitializer.java`
- `src/main/java/svenhjol/charm/common/features/aerial_affinity/AerialAffinity.java`
- `src/main/java/svenhjol/charm/common/mixins/aerial_affinity/PlayerMixin.java`
- `src/main/resources/charm.common.mixins.json`
- `src/main/resources/fabric.mod.json`
- `src/main/resources/data/charm/enchantment/aerial_affinity.json`
- `src/main/resources/data/charmony/advancement/aerial_affinity/used_aerial_affinity.json`
- `src/main/resources/assets/charmony/lang/en_us.json`
- `PHASE7B_MASTER_PARITY_AUDIT.md`
- `RELEASE_VALIDATION_BACKLOG.md`

## Validation

`:compileJava` and the aggregate `./gradlew.bat build` pass. Dev-client startup
loaded the Aerial Affinity feature and the root mixin without enchantment,
data-pack, or mixin errors. The log still contains unrelated existing
authentication/Realms failures and missing Moobloom-head asset warnings.
Interactive speed, acquisition, config, and multiplayer tests remain
UNTESTED and are listed in the release backlog. The completely missing-feature
count is now 30. The P0 Suspicious Block Falling Item Persistence blocker
remains OPEN.
