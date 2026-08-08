# Phase 2A: Charmony API Unblock

## Scope and outcome

This phase removes the first Minecraft 1.21.10 compilation boundary without migrating the newly exposed Charmony core or feature-module errors. Charmony API now compiles successfully. The aggregate build advances to Charmony core and reports its first complete compiler set.

No feature was disabled, stubbed, commented out, or removed.

## Level side API changes

Minecraft 1.21.10 retains `Level`'s client-side flag as a private field and exposes it through the public `boolean isClientSide()` method. The method returns that field directly, so it is the exact modern equivalent for these side-selection checks; no context-specific replacement is needed.

| File | Uses | Old expression | 1.21.10 expression | Preserved behavior |
|---|---:|---|---|---|
| `GrindstoneEvents.java` | 3 | `player.level().isClientSide` | `player.level().isClientSide()` | Put, retrieve, and remove grindstone menu instances from the same client/server maps as before. |
| `SmithingTableEvents.java` | 3 | `player.level().isClientSide` | `player.level().isClientSide()` | Put, retrieve, and remove smithing menu instances from the same client/server maps as before. |

No event ordering, UUID lookup, map behavior, or fallback behavior changed.

## Particle access-widener changes

### Deferred particle registration

In the recovered source, Charmony widened and called:

- `ParticleEngine$SpriteParticleRegistration`
- `ParticleEngine.register(ParticleType, SpriteParticleRegistration)`

Minecraft 1.21.10 moved the corresponding vanilla implementation to the private internals of `ParticleResources`, including `ParticleResources$SpriteParticleRegistration` and its private registration method. Retargeting the widener to those new private internals is unnecessary because Fabric API 0.138.4 provides the maintained public equivalent: `ParticleFactoryRegistry` and `PendingParticleFactory`.

The deferred registration record, `ClientRegistry.particle`, and the existing registration loop now use `ParticleFactoryRegistry.PendingParticleFactory` and `ParticleFactoryRegistry.getInstance().register(...)`. Fabric's sprite provider implements Minecraft's `SpriteSet`, so existing factory constructor references keep the same sprite-driven particle-provider behavior. Fabric's registry supports both registration before particle resources initialize and direct registration afterward, preserving the existing deferred/client-start lifecycle.

The two obsolete Charmony access-widener entries were removed. No replacement vanilla widener is required.

### Particle alpha

The recovered Tweaks widener targeted public `Particle.setAlpha(float)`. In 1.21.10 alpha is quad-rendering state and the method is now `protected SingleQuadParticle.setAlpha(float)`. The item-frame-hiding particle provider still invokes this operation from outside the particle class hierarchy, so widened access remains required to preserve its randomized opacity.

The entry was retargeted precisely to:

```text
accessible method net/minecraft/client/particle/SingleQuadParticle setAlpha (F)V
```

The unrelated, duplicate `ParticleEngine$SpriteParticleRegistration` entry in the Tweaks widener was removed because registration is owned by Charmony core and now uses Fabric API.

Both original particle access-widener validation failures are resolved. Validation now proceeds farther and exposes unrelated downstream widener changes recorded below.

## Validation

### Charmony API compilation

Command:

```text
gradlew.bat :charmony-api:compileJava --console=plain
```

Result: **BUILD SUCCESSFUL**. One task executed. All 79 Charmony API Java files compile against Minecraft 1.21.10.

### Aggregate build

Command:

```text
gradlew.bat build --continue --console=plain
```

Result: **failed in `:charmony:compileJava` after Charmony API completed successfully**. This is the expected next migration boundary.

Javac reports **45 newly exposed Charmony core errors**:

| Category | Errors | Summary |
|---|---:|---|
| Particle rendering API | 16 | Removed `TextureSheetParticle`, changed particle render type/group APIs, and resulting missing inherited members/methods in `CustomParticle`. |
| Control-panel GUI API | 21 | Selection-list rendering contracts, mouse event signatures, widget sprite representation/access, and overrides changed. |
| Block-entity renderer generics | 1 | `BlockEntityRendererProvider` now requires two type arguments. |
| Block property construction | 6 | `BlockBehaviour.Properties.noCollission()` is no longer available under that name. |
| Server-player access | 1 | `ServerPlayer.getServer()` is no longer available under that name. |
| **Total** | **45** | All are in Charmony core; feature-module Java compilation remains dependency-blocked. |

The build also advances beyond the two particle widener entries and exposes two unrelated access-widener validation failures for later phases:

- Charmony: the widened `SpriteIconButton.sprite` field changed type/visibility and no longer matches `ResourceLocation`.
- Charmony Tweaks: `Player.respawnEntityOnShoulder(CompoundTag)` changed or moved.

These downstream Java and access-widener failures were diagnosed but not changed in this narrowly scoped phase.

## Files changed

Java:

- `modules/charmony-api/src/main/java/svenhjol/charmony/api/events/GrindstoneEvents.java`
- `modules/charmony-api/src/main/java/svenhjol/charmony/api/events/SmithingTableEvents.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/client/ClientRegistry.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/client/DeferredParticle.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/client/features/core/Registers.java`

Access wideners:

- `modules/charmony/src/main/resources/charmony.accesswidener`
- `modules/charmony-tweaks/src/main/resources/charmony-tweaks.accesswidener`

Documentation:

- `PHASE2A_API_UNBLOCK.md`

## Next migration boundary

The next failing Java project is `:charmony`. Its core client particle and control-panel GUI migrations are the largest immediate groups. The two newly exposed non-particle access-widener targets should be resolved alongside the corresponding core/Tweaks behavior rather than removed merely to pass validation.
