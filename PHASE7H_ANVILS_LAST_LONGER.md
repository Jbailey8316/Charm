# Phase 7H — Anvils Last Longer

## Historical behavior

The historical Charm `anvils_last_longer` feature changed the chance that a
successful anvil operation damages the anvil. Historical vanilla used a 12%
damage roll; Charm's default `chanceToDamage` was `0.5`, described as making
anvils 50% stronger. A failed roll kept the current anvil state. A successful
roll still used vanilla `AnvilBlock.damage`, so the normal sequence remained:

`anvil -> chipped_anvil -> damaged_anvil -> destroyed`.

The historical implementation intercepted the anvil damage decision rather
than changing XP costs, output construction, repair materials, naming,
enchantments, or falling-anvil damage.

## 1.21.10 vanilla path

Minecraft 1.21.10 performs the degradation roll in
`AnvilMenu.onTake`: the server uses `player.getRandom().nextFloat() < 0.12f`
after a successful operation and only when the player does not have infinite
materials. The resulting state is passed to `AnvilBlock.damage`; `null` removes
the damaged anvil and emits the vanilla destruction event, while a returned
state is placed and emits the normal damage event. This path preserves creative
behavior and all normal menu/output semantics.

## Implementation

Charm registers `AnvilsLastLonger` as a common configurable feature. Its
`Damage chance` value defaults to `0.5` and is clamped to `[0, 1]`.
`AnvilBlockMixin` uses a narrow cancellable HEAD injection on the static
`AnvilBlock.damage(BlockState)` transition point. A failed Charm roll returns
the unchanged state; otherwise the original vanilla method runs:

* feature enabled: the configured Charm chance is used (default `0.50`);
* feature disabled or unavailable: the untouched vanilla `0.12` is returned.

The server-side damage decision remains authoritative. Vanilla state
transitions, level events, sounds, creative/infinite-material handling,
XP costs, repair behavior, and output construction are not replaced. Falling
anvils are outside this hook and are intentionally unchanged.

## Validation

Static validation passed: the 1.21.10 bytecode/source path contains the single
`0.12f` degradation constant in the vanilla `onTake` lambda, while Charm hooks
the shared `AnvilBlock.damage` transition point. The configured enabled
probability is mathematically `50%`; disabled behavior leaves the vanilla
`12%` roll untouched. The three vanilla state transitions and creative guard
remain in the unmodified `onTake` method.

Interactive statistical testing of many anvil operations, config toggling,
creative mode, destruction events, and dedicated-server synchronization remains
UNTESTED and is recorded in `RELEASE_VALIDATION_BACKLOG.md`. No feature-specific
startup or mixin errors were observed; unrelated pre-existing development
environment warnings remain documented in the runtime backlog.

## Files changed

* `src/main/java/svenhjol/charm/common/features/anvils_last_longer/AnvilsLastLonger.java`
* `src/main/java/svenhjol/charm/common/mixins/anvils_last_longer/AnvilBlockMixin.java`
* `src/main/java/svenhjol/charm/common/CommonInitializer.java`
* `src/main/resources/charm.common.mixins.json`
* `PHASE7B_MASTER_PARITY_AUDIT.md`
* `RELEASE_VALIDATION_BACKLOG.md`

## Build and release status

`./gradlew.bat :compileJava` and the aggregate `./gradlew.bat build` both pass
after this phase's changes. Aerial Affinity and Suspicious
Block Creating were not modified. The P0 blocker
`Suspicious Block Falling Item Persistence` remains OPEN pending runtime proof.
