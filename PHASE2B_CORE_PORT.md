# Phase 2B: Charmony Core Port

## Scope and outcome

Charmony core now compiles and completes its Gradle `build` lifecycle against Minecraft 1.21.10. The changes are limited to 13 Charmony core Java files, the Charmony access widener, and the strictly necessary Charmony Tweaks access-widener owner migration. No feature-module Java source was changed.

The implementation follows the original author's later 1.21.9/1.21.10 migration where it covers the recovered code, while retaining the Phase 2A Fabric particle-factory registration. No feature, control, rendering path, or behavior was stubbed, disabled, commented out, or removed.

## Particle rendering migration

`TextureSheetParticle` and the old `ParticleRenderType` selection method no longer exist in Minecraft 1.21.10. `CustomParticle` now extends `SingleQuadParticle`, which owns the equivalent textured quad state.

Notable mappings:

| Recovered API | Minecraft 1.21.10 API | Behavioral preservation |
|---|---|---|
| `TextureSheetParticle` | `SingleQuadParticle` | Keeps a textured, camera-facing particle quad. |
| superclass constructor without a sprite | superclass constructor with `spriteSet.first()` | Supplies the required initial sprite while retaining the complete sprite set for animation. |
| `getRenderType()` returning `PARTICLE_SHEET_TRANSLUCENT` | `getLayer()` returning `Layer.OPAQUE` | Uses the layer selected by the original author's modern port for this particle pipeline. |
| `setSpriteFromAge(spriteProvider)` | unchanged on `SingleQuadParticle` | Initial and per-tick sprite animation remains tied to particle age. |

The original velocity, friction (`0.6`), collision-speed behavior, physics flag, quad-size multiplier (`0.78`), lifetime/age processing, color/alpha state, light calculation, and tick-time sprite updates remain unchanged. The Phase 2A `ParticleFactoryRegistry.PendingParticleFactory` path continues to supply a Fabric sprite provider implementing `SpriteSet`; no vanilla placeholder particle was substituted.

## Control-panel GUI migration

Minecraft's selection-list entry rendering and mouse input contracts changed. Four control-panel classes were ported using the modern entry-owned geometry and event objects.

Notable mappings:

| Recovered API | Minecraft 1.21.10 API | Preserved behavior |
|---|---|---|
| entry `render(...)` with row index/position/size arguments | `renderContent(...)` plus `getX()`/`getY()` | Labels, buttons, text fields, icons, tooltips, margins, and colors remain at their corresponding row-relative locations. |
| `mouseClicked(double, double, int)` | `mouseClicked(MouseButtonEvent, boolean)` | Clicks are still routed only to the hovered enable, disable, configure, or mod-name button, avoiding erroneous clicks while scrolling. |
| child rendering with inherited row geometry arguments | child rendering using the entry's `getY()` | Boolean buttons and single/multiline inputs retain their original offsets and widths. |
| removed `isSelectedItem(int)` override | inherited modern selection behavior | The obsolete override always returned false and no longer exists in the superclass contract. |
| `SpriteIconButton.sprite` as `ResourceLocation` | `WidgetSprites`, checking `enabled()` | The title-screen accessibility button is still identified by its `icon/accessibility` sprite before placing the Charmony control-panel button beside it. |

Narration hooks, feature enable/disable state, restart tracking, default-value highlighting, configuration input parsing, tooltip content, and screen navigation remain intact.

## Renderer generic migration

`BlockEntityRendererProvider` now has entity and render-state type parameters. `ClientRegistry.blockEntityRenderer` changed from:

```text
<BE extends BlockEntity> BlockEntityRendererProvider<BE>
```

to:

```text
<E extends BlockEntity, S extends BlockEntityRenderState> BlockEntityRendererProvider<E, S>
```

Registration still delegates to `BlockEntityRenderers.register` with the same block-entity type and provider. No renderer behavior was changed. Feature modules with renderers must migrate their implementations to the new extraction/render-state API separately.

## Block property migration

Minecraft corrected the property-builder method name from `noCollission()` to `noCollision()`. Six Charmony wood-family block constructors were updated:

- ceiling hanging sign
- sapling
- standing sign
- wall hanging sign
- wall sign
- wooden pressure plate

Only the method spelling changed. All existing property chains remain in place, preserving strength, sounds, map colors, instruments, lava ignition, random ticking, instant breaking, loot/description overrides, solidity behavior, registry IDs, and the intended absence of collision.

## Server-player accessor migration

The server networking callback no longer obtains the server through removed `ServerPlayer.getServer()`. It now uses the public Fabric networking context accessor `context.server()`, which is the authoritative server for the received payload. The handler continues to execute on that server and receives the same player and payload.

No reflection or new mixin was introduced.

## Access-widener migrations

### `SpriteIconButton.sprite`

The member still exists and remains non-public, so widening is still required. Its 1.21.10 type is `WidgetSprites`, not `ResourceLocation`. The Charmony widener was retargeted precisely to the new descriptor, and the call site reads the enabled sprite from that value.

### Shoulder entity methods

In 1.21.10, `respawnEntityOnShoulder`, `setShoulderEntityLeft`, and `setShoulderEntityRight` are owned by `ServerPlayer` rather than `Player`. They remain inaccessible to the Tweaks feature, so all three existing entries were retargeted to `net/minecraft/server/level/ServerPlayer` with unchanged `CompoundTag` descriptors.

No other Charmony Tweaks source or access-widener entry was changed. Both `:charmony:validateAccessWidener` and `:charmony-tweaks:validateAccessWidener` now succeed.

## Validation

### Charmony core compilation

Command:

```text
gradlew.bat :charmony:compileJava --console=plain
```

Result: **BUILD SUCCESSFUL**. Charmony API was up to date and Charmony core compiled successfully. Javac notes existing deprecated API use; it reports no core compilation error.

### Aggregate build

Command:

```text
gradlew.bat build --continue --console=plain
```

Result: **failed only in feature-module Java compilation**. Charmony core compiled, validated its widener, assembled, remapped, and completed its own `build` task. All access-widener validation tasks that ran succeeded.

Five feature modules compile and build through their available tasks:

- Azalea Wood
- Collection
- Decor
- Ebony Wood
- Glint Colors

Four modules expose **41 Java errors**:

| Module | Errors | Categories |
|---|---:|---|
| Brew and Stew | 6 | 2 client-side accessor calls; 4 changed block interaction/entity-inside callback signatures. |
| Mooblooms | 4 | Spawn egg construction; hand-to-equipment-slot helper; entity render-layer submit/render-state migration (2). |
| Totem of Preserving | 13 | 5 client-side accessor calls; 4 block construction/entity-inside changes; 4 block-entity renderer/render-state and item-rendering changes. |
| Tweaks | 18 | 10 client-side accessor calls; 4 keyboard input/category changes; 2 hand-to-equipment-slot helpers; 1 particle-provider signature; 1 renamed/moved chain block constant. |
| **Total** | **41** | Feature-module source was inventoried but not changed. |

The first failed feature compilation task in Gradle order is `:charmony-brew-and-stew:compileJava`.

Warnings requiring later review:

- Glint Colors compiles with three mixin target/descriptor warnings in its equipment/glint rendering injections.
- Tweaks reports two mixin target/descriptor warnings in item restocking and parrots-stay-on-shoulder code, plus existing deprecated API use.
- Decor reports existing deprecated API use.

Successful compilation does not establish runtime or mixin parity; these warnings must be audited during the corresponding feature-module phases.

## Files changed

Charmony core Java (13):

- `modules/charmony/src/main/java/svenhjol/charmony/core/client/ClientRegistry.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/client/CustomParticle.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/client/features/control_panel/FeatureConfigList.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/client/features/control_panel/FeaturesList.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/client/features/control_panel/Handlers.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/client/features/control_panel/ModsList.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/CommonRegistry.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/features/wood/blocks/CustomCeilingHangingSignBlock.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/features/wood/blocks/CustomSaplingBlock.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/features/wood/blocks/CustomStandingSignBlock.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/features/wood/blocks/CustomWallHangingSignBlock.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/features/wood/blocks/CustomWallSignBlock.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/features/wood/blocks/CustomWoodenPressurePlateBlock.java`

Access wideners (2):

- `modules/charmony/src/main/resources/charmony.accesswidener`
- `modules/charmony-tweaks/src/main/resources/charmony-tweaks.accesswidener`

Documentation:

- `PHASE2B_CORE_PORT.md`

## Remaining blockers

- Port the 41 errors in the four failing feature modules in small functional phases.
- Audit the five mixin warnings before treating the corresponding modules as runtime-compatible.
- Perform client runtime testing of Charmony's control panel and custom particle after the feature modules compile and the game can start.
- Compilation alone does not establish parity for Charmony's 168 Java files or its runtime mixins.
