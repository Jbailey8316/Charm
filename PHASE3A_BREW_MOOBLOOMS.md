# Phase 3A: Brew and Stew and Mooblooms

## Scope and outcome

Brew and Stew and Mooblooms now compile, validate, remap, and build against Minecraft 1.21.10. Changes are limited to three Java files in each module plus this report. No Totem of Preserving or Tweaks file was changed.

No registrations, recipes, effects, entities, AI, rendering paths, interactions, loot, world generation, configuration, networking, data, or resources were removed, stubbed, disabled, or simplified.

## Brew and Stew migrations

### Comparator output callback

Minecraft 1.21.10 adds the queried `Direction` to `Block.getAnalogOutputSignal`. The Cask and Cooking Pot overrides now accept that direction while retaining their original output calculations:

- Casks still interpolate bottle count across comparator strengths 0–15.
- Cooking pots still return zero until cooking is complete, then return the current portion count.

The direction does not affect either container's intentionally direction-independent comparator output.

### Entity-inside callback

`BlockBehaviour.entityInside` now receives an additional boolean movement/collision context. `CookingPotBlock` accepts the new argument and forwards it unchanged when delegating to the superclass. Its existing behavior remains intact: when its block entity is present, the cooking-pot handler processes the entity and the method returns as before.

### Level-side accessor

Two remaining direct reads of the private `Level.isClientSide` field in `CookingPotBlockEntity` now call the public `isClientSide()` accessor. The conditions remain server-only:

- broadcasting the item-added sound and packet;
- playing the portion-taken sound.

Food aggregation, hunger/saturation values, cooking-state transitions, water filling, sounds, packets, synchronization, and item output are otherwise unchanged.

### Brew and Stew API mappings

| Recovered API | Minecraft 1.21.10 API |
|---|---|
| `getAnalogOutputSignal(state, level, pos)` | `getAnalogOutputSignal(state, level, pos, direction)` |
| `entityInside(state, level, pos, entity, applier)` | `entityInside(state, level, pos, entity, applier, movementFlag)` |
| `level.isClientSide` | `level.isClientSide()` |

## Mooblooms migrations

### Spawn egg registration

The spawn egg entity type moved from the `SpawnEggItem` constructor into item properties. Registration changed from the removed two-argument constructor to:

```text
new SpawnEggItem(new Item.Properties().spawnEgg(moobloom.get()).setId(key))
```

The same Moobloom entity type and item registry key are retained. Vanilla's modern spawn-egg implementation reads the typed entity data installed by `Item.Properties.spawnEgg`, preserving spawning and spawner interaction behavior.

### Shears durability slot

The removed static hand-to-slot helper was replaced with the explicit equivalent:

- `InteractionHand.MAIN_HAND` maps to `EquipmentSlot.MAINHAND`.
- `InteractionHand.OFF_HAND` maps to `EquipmentSlot.OFFHAND`.

Shearing still damages the held shears by one in the hand used for the interaction. Pollination changes, flower drops, sounds, game events, and advancement triggering are unchanged.

### Flower render layer

Entity feature layers now submit render nodes rather than drawing immediately through `MultiBufferSource`. `FlowerLayer.render` was migrated to `RenderLayer.submit`, and each transformed flower block is submitted through `SubmitNodeCollector.submitBlock`.

The original rendering behavior is preserved:

- the same flower block states are selected;
- pink petals and sunflower upper halves retain their special handling;
- all body and head flower pose translations, rotations, and scales are unchanged;
- the head flower still appears only when pollinated;
- flowers remain hidden for babies and invisible entities;
- light, overlay coordinates, and render-state outline color are supplied to each submitted block node.

### Mooblooms API mappings

| Recovered API | Minecraft 1.21.10 API |
|---|---|
| `new SpawnEggItem(entityType, properties)` | `new SpawnEggItem(properties.spawnEgg(entityType))` |
| `Moobloom.getSlotForHand(hand)` | explicit `EquipmentSlot.MAINHAND` / `OFFHAND` mapping |
| `RenderLayer.render(..., MultiBufferSource, ...)` | `RenderLayer.submit(..., SubmitNodeCollector, ...)` |
| `BlockRenderDispatcher.renderSingleBlock(...)` | `SubmitNodeCollector.submitBlock(...)` |

## Validation

### Brew and Stew

Commands:

```text
gradlew.bat :charmony-brew-and-stew:compileJava --console=plain
gradlew.bat :charmony-brew-and-stew:build --console=plain
```

Results: **both successful**. Compilation, resources, tests (none present), access-widener validation, remapped JAR, sources JAR, assembly, and build complete. No Brew and Stew compiler or mixin warning was emitted.

### Mooblooms

Commands:

```text
gradlew.bat :charmony-mooblooms:compileJava --console=plain
gradlew.bat :charmony-mooblooms:build --console=plain
```

Results: **both successful**. Compilation, resources, tests (none present), access-widener validation, remapped JAR, sources JAR, assembly, and build complete. No Mooblooms compiler or mixin warning was emitted.

### Aggregate build

Command:

```text
gradlew.bat build --continue --console=plain
```

Result: **failed only in the two intentionally untouched modules**. Brew and Stew and Mooblooms both complete their `build` tasks in the aggregate run.

| Remaining module | Errors | Categories |
|---|---:|---|
| Totem of Preserving | 13 | 5 client-side accessor calls; 4 block construction/entity-inside changes; 4 block-entity renderer/render-state and item-rendering changes. |
| Tweaks | 18 | 10 client-side accessor calls; 4 keyboard input/category changes; 2 hand-to-equipment-slot helpers; 1 particle-provider signature; 1 renamed/moved chain block constant. |
| **Total** | **31** | No remaining Brew and Stew or Mooblooms compile errors. |

The aggregate run reports two existing Tweaks mixin warnings:

- item-restocking `AnimalMixin`: injection target descriptor could not be determined;
- parrots-stay-on-shoulder `PlayerMixin`: shadow field target is missing on `Player`.

They are outside Phase 3A and require correction during the Tweaks port. No warning was emitted from Brew and Stew's potion-brewing mixin or from Mooblooms.

## Files changed

Brew and Stew Java (3):

- `modules/charmony-brew-and-stew/src/main/java/svenhjol/charmony/brew_and_stew/common/features/casks/CaskBlock.java`
- `modules/charmony-brew-and-stew/src/main/java/svenhjol/charmony/brew_and_stew/common/features/cooking_pots/CookingPotBlock.java`
- `modules/charmony-brew-and-stew/src/main/java/svenhjol/charmony/brew_and_stew/common/features/cooking_pots/CookingPotBlockEntity.java`

Mooblooms Java (3):

- `modules/charmony-mooblooms/src/main/java/svenhjol/charmony/mooblooms/client/features/mooblooms/FlowerLayer.java`
- `modules/charmony-mooblooms/src/main/java/svenhjol/charmony/mooblooms/common/features/mooblooms/Handlers.java`
- `modules/charmony-mooblooms/src/main/java/svenhjol/charmony/mooblooms/common/features/mooblooms/Registers.java`

Documentation:

- `PHASE3A_BREW_MOOBLOOMS.md`

No access widener, mixin configuration, data file, resource, or binary asset changed.

## Runtime risks and next checks

Compilation cannot verify visual or gameplay parity. Runtime testing should cover:

- cask and cooking-pot comparator output from every queried side;
- cooking-pot entity contact, sounds, packets, food addition, cooking completion, portion taking, and persistence;
- Moobloom spawn eggs in the world and on spawners;
- shearing from both hands and durability loss;
- body/head flower placement for each Moobloom type, including pollinated, baby, invisible, and outlined states;
- spawning, AI, bee interaction, milking, shearing drops, and save/reload behavior.

The next compilation phase should port Totem of Preserving and Tweaks without revisiting the now-building Phase 3A modules unless runtime evidence requires it.
