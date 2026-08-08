# Phase 3B: Totem of Preserving

## Scope and outcome

Totem of Preserving now compiles, validates, remaps, and builds against Minecraft 1.21.10. Changes are limited to three Java files in the Totem of Preserving module plus this report. No Charmony Tweaks file, resource, data file, access widener, build file, or other module was changed.

The existing death interception, inventory capture and recovery, totem item state, ownership checks, block entity persistence, networking, recipes, loot, configuration, and resources were retained. No feature was stubbed, disabled, commented out, removed, or replaced with temporary runtime-only storage.

## API migrations

### Level side accessor

Five direct reads of the now-private `Level.isClientSide` field in `Handlers` use the public `isClientSide()` accessor. This is the direct 1.21.10 equivalent and preserves the original side boundaries for:

- emergency item drops when the protected holder block is overwritten;
- server-only player inventory death-drop handling;
- server-only spawning of recovered item entities;
- client/server dispatch while consuming a Totem of Preserving;
- server-only Totem-use sound playback.

### Block properties and entity-inside callback

The corrected `BlockBehaviour.Properties.noCollision()` method replaces `noCollission()`. All other holder-block properties remain unchanged: copied glass properties, unbreakable strength/resistance, no occlusion, no loot table, registry key, invisible block rendering, empty collision/visual shapes, and the original selection shape.

`BlockBehaviour.entityInside` now includes a boolean movement/collision context. `TotemBlock` accepts and forwards that argument while continuing to invoke `playerEnteredBlock` first. Owner recovery, item return, holder destruction, and contact behavior remain in the same order.

### Block-entity rendering and item model submission

Minecraft 1.21.10 block-entity rendering separates entity-data extraction from render-node submission. `TotemRenderer` now implements `BlockEntityRenderer<TotemBlockEntity, RenderState>` and uses a dedicated render state containing:

- the resolved `ItemStackRenderState` for the Totem of Preserving;
- the current Y-axis rotation angle;
- inherited block position, type, light, and break-overlay state.

The renderer obtains `ItemModelResolver` from the renderer context. During extraction it advances the original rotation by `0.25` degrees, wraps at `360`, retains the block entity hash as the item-model seed, and resolves the same glinting Totem stack in `ItemDisplayContext.FIXED`. During submission it applies the same centering, half scale, Y rotation, full-bright light value (`0xf000f0`), and no-overlay value before submitting the resolved item state.

This preserves the original visible size, position, spin rate, glint, item model context, brightness, and model-variant seed while conforming to the render-state/thread separation required by 1.21.10.

## Old API to new API mappings

| Recovered 1.21.6 API | Minecraft 1.21.10 API |
|---|---|
| `level.isClientSide` | `level.isClientSide()` |
| `Properties.noCollission()` | `Properties.noCollision()` |
| `entityInside(..., InsideBlockEffectApplier)` | `entityInside(..., InsideBlockEffectApplier, boolean)` |
| `BlockEntityRenderer<T>` | `BlockEntityRenderer<T, S extends BlockEntityRenderState>` |
| immediate `render(...)` with `MultiBufferSource` | `extractRenderState(...)` followed by `submit(...)` with `SubmitNodeCollector` |
| `ItemRenderer.renderStatic(...)` | `ItemModelResolver.updateForTopItem(...)` and `ItemStackRenderState.submit(...)` |

## Serialization and data preservation

No serialization or stored-data migration was necessary. `TotemBlockEntity` continues to persist:

- all captured `ItemStack` values through `ContainerHelper` and the modern `ValueInput`/`ValueOutput` APIs;
- the explicit item count;
- owner UUID through `UUIDUtil.CODEC`;
- death message;
- damage value.

The rotation value remains intentionally client runtime rendering state and was not made persistent, matching the original behavior. Item contents, owner association, message, and damage remain persistent across save/reload. No item component, codec, inventory, block-entity synchronization, or network implementation changed in this phase.

## Files changed

Totem of Preserving Java (3):

- `modules/charmony-totem-of-preserving/src/main/java/svenhjol/charmony/totem_of_preserving/client/features/totem_of_preserving/TotemRenderer.java`
- `modules/charmony-totem-of-preserving/src/main/java/svenhjol/charmony/totem_of_preserving/common/features/totem_of_preserving/Handlers.java`
- `modules/charmony-totem-of-preserving/src/main/java/svenhjol/charmony/totem_of_preserving/common/features/totem_of_preserving/TotemBlock.java`

Documentation:

- `PHASE3B_TOTEM.md`

## Validation

### Module compilation

Command:

```text
gradlew.bat :charmony-totem-of-preserving:compileJava --console=plain
```

Result: **BUILD SUCCESSFUL**. Totem of Preserving compiles against Minecraft 1.21.10 with no compiler or mixin warning.

### Module build

Command:

```text
gradlew.bat :charmony-totem-of-preserving:build --console=plain
```

Result: **BUILD SUCCESSFUL**. Compilation, resources, tests (none present), access-widener validation, sources JAR, remapped JAR, assembly, and build all complete.

### Aggregate build

Command:

```text
gradlew.bat build --continue --console=plain
```

Result: **fails only at the intentionally untouched `:charmony-tweaks:compileJava` task**. Totem of Preserving completes its build lifecycle in the aggregate run.

Tweaks retains **18 Java errors**:

| Category | Errors | Summary |
|---|---:|---|
| Level side access | 10 | Direct reads of the private `Level.isClientSide` field. |
| Keyboard input/category APIs | 4 | Three key mappings now require `KeyMapping.Category`; one key match now requires `KeyEvent`. |
| Hand-to-equipment-slot mapping | 2 | Removed `Player.getSlotForHand(InteractionHand)`. |
| Particle provider signature | 1 | Provider creation now includes `RandomSource`. |
| Chain block reference | 1 | Removed or renamed `Blocks.CHAIN` constant. |
| **Total** | **18** | No Tweaks Java source was changed. |

The aggregate compile also retains two existing Tweaks mixin warnings:

- item-restocking `AnimalMixin`: injection target descriptor cannot be determined;
- parrots-stay-on-shoulder `PlayerMixin`: shadow field target is missing on `Player`.

These errors and warnings are outside Phase 3B and require correction in the Tweaks port.

## Runtime and persistence risks

Compilation does not verify gameplay or visual parity. Runtime testing must cover:

- death with normal, empty, full, damaged, enchanted, and component-bearing inventories;
- Totem creation, consumption, item capture, holder placement, ownership enforcement, and recovery;
- emergency drops when another operation tries to replace the holder block;
- persistence of items, owner UUID, message, and damage across server restart and chunk unload/reload;
- client/server synchronization and multiplayer ownership behavior;
- interaction/contact with the invisible holder block;
- rotating Totem position, scale, speed, full-bright appearance, glint, and model variants;
- loot acquisition, recipes, wandering-trader acquisition, configuration, and death-flow compatibility.

The principal new runtime risk is the 1.21.10 render-state extraction path: visual parity and render-thread behavior require in-game client testing. Persistent gameplay data was not structurally changed, but save/reload and component-rich ItemStacks still require runtime verification before parity can be claimed.

## Next phase

Port Charmony Tweaks as a separate phase, including both compile errors and the two definite mixin target warnings. Do not claim aggregate or runtime parity until Tweaks builds and all modules have passed startup and gameplay testing.
