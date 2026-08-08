# Phase 3C: Charmony Tweaks

## Scope and outcome

Charmony Tweaks now compiles, validates, remaps, and builds against Minecraft 1.21.10. The aggregate Charm multi-project build also succeeds. This is a compile-level milestone, not proof of runtime or feature parity.

Changes are confined to Tweaks source/resources and this report. No Charmony API, core, other feature module, root build file, or external dependency was changed. No feature or mixin was disabled, stubbed, commented out, or removed.

## API migrations

### Level side access

Ten direct reads of the private `Level.isClientSide` field now use the public `isClientSide()` accessor. The accessor returns the same side flag and preserves the original client/server gates in:

- animal revival creation and application;
- crop harvesting/replanting;
- adult non-jockey chicken feather drops;
- path-to-dirt and dirt-to-path conversion;
- spawner item drops;
- suspicious-block advancement and client particles;
- villager attraction ticks.

### Keyboard input

Minecraft 1.21.10 key mappings use typed categories and screen key callbacks receive `KeyEvent`.

Mappings:

| Recovered API | Minecraft 1.21.10 API | Preserved behavior |
|---|---|---|
| category string `"key.categories.misc"` | `KeyMapping.Category.MISC` | Crafting-table-nearby remains in the Miscellaneous category with the same V default. |
| category string `"key.categories.inventory"` | `KeyMapping.Category.INVENTORY` | Item Tidying and Totem Emergency Swap remain inventory bindings with apostrophe and Z defaults. |
| `AbstractContainerScreen.keyPressed(int, int, int)` | `keyPressed(KeyEvent)` | The complete modern event is forwarded from the same HEAD injection. |
| `KeyMapping.matches(int, int)` | `KeyMapping.matches(KeyEvent)` | Item Tidying detects the configured key, scan code, and modifier state through the modern input object. |

All handlers remain client-only through their existing client mixin/feature registration. Sorting still occurs only while a container screen is tracked, with the same single/multiple-inventory selection logic.

### Hand to equipment slot

The removed `Player.getSlotForHand` helper was replaced explicitly in crop replanting and path conversion:

- `InteractionHand.MAIN_HAND` maps to `EquipmentSlot.MAINHAND`;
- `InteractionHand.OFF_HAND` maps to `EquipmentSlot.OFFHAND`.

The tool used for the interaction still loses exactly one durability unless the player has instant-build ability. Both hands retain their original behavior.

### Particle provider

`ParticleProvider.createParticle` now receives a `RandomSource`. The item-frame-hiding provider accepts the new argument while retaining its original shared random source and all visual behavior: randomized velocity, 10–19 tick lifetime, supplied RGB color, alpha from 0.8–1.0, friction `0.8`, sprite set, and upward speed behavior when Y motion is blocked. Registration remains on Charmony's Fabric-backed deferred particle factory path; no vanilla placeholder was substituted.

### Chain rename

Minecraft 1.21.10 names the copper-era original chain block `IRON_CHAIN`; `Blocks.CHAIN` and the `minecraft:chain` ID no longer exist. Both coupled references were migrated:

- mineshaft placement logic compares against `Blocks.IRON_CHAIN` before hanging a lantern;
- the weighted ceiling-block loot pool uses `minecraft:iron_chain`.

This preserves the intended iron-chain ceiling decoration rather than substituting a different material.

### Mob texture renderer registration

The deprecated direct Fabric `EntityRendererRegistry.register` calls now use Charmony's maintained `ClientRegistry.entityRenderer` wrapper. The same conditional Snow Golem and Wandering Trader renderer providers are registered; texture choice and entity-load behavior are unchanged. This removes the Tweaks-specific deprecation note from a fresh compile.

## Mixin warning analysis and fixes

### Item restocking: `AnimalMixin`

Original target:

```text
Animal.usePlayerItem(Player, InteractionHand, ItemStack)
```

That method no longer exists in Minecraft 1.21.10, which caused Loom to report that the injection descriptor could not be determined. The modern feeding interaction is `Animal.mobInteract(Player, InteractionHand)`. The mixin now injects at its HEAD, obtains the held stack from the supplied hand, and shadows the existing public abstract `Animal.isFood(ItemStack)` contract.

The used-item statistic is recorded only when the held item is valid food for that animal. This retains automatic restocking for actual feeding while avoiding the broader incorrect behavior of counting unrelated animal interactions. The callback return type is now `CallbackInfoReturnable<InteractionResult>`, matching the exact target descriptor. The warning is resolved.

### Parrots stay on shoulder: `ServerPlayerMixin`

Minecraft 1.21.10 moved `timeEntitySatOnShoulder`, `removeEntitiesOnShoulder`, and the shoulder respawn/set operations from `Player` to `ServerPlayer`. The old `PlayerMixin` could not find its shadow field.

The mixin was renamed and retargeted to `ServerPlayer`, and the common mixin configuration now names `parrots_stay_on_shoulder.ServerPlayerMixin`. The `@WrapMethod` still suppresses `removeEntitiesOnShoulder` only when a parrot is present and the existing `shouldParrotStayMounted` policy passes; otherwise it invokes the original method. The existing precisely retargeted `ServerPlayer` access-widener entries from Phase 2B remain valid and unchanged. The warning is resolved.

## Files changed

Tweaks Java paths (18, including the mixin rename):

- `client/features/crafting_table_nearby/Registers.java`
- `client/features/item_frame_hiding/Particle.java`
- `client/features/item_tidying/Handlers.java`
- `client/features/item_tidying/Registers.java`
- `client/features/mob_textures/Registers.java`
- `client/features/totem_emergency_swap/Registers.java`
- `client/mixins/item_tidying/AbstractContainerScreenMixin.java`
- `common/features/animal_reviving/Handlers.java`
- `common/features/crop_replanting/Handlers.java`
- `common/features/mineshaft_improvements/Handlers.java`
- `common/features/mob_drops/mobs/ChickenDrops.java`
- `common/features/path_converting/Handlers.java`
- `common/features/spawners_drop_items/Handlers.java`
- `common/features/suspicious_block_creating/Handlers.java`
- `common/features/villager_attracting/Handlers.java`
- `common/mixins/item_restocking/AnimalMixin.java`
- `common/mixins/parrots_stay_on_shoulder/PlayerMixin.java` (renamed)
- `common/mixins/parrots_stay_on_shoulder/ServerPlayerMixin.java` (rename target)

All paths above are below `modules/charmony-tweaks/src/main/java/svenhjol/charmony/tweaks/`.

Tweaks resources (2):

- `modules/charmony-tweaks/src/main/resources/charmony-tweaks.common.mixins.json`
- `modules/charmony-tweaks/src/main/resources/data/charmony/loot_table/mineshaft_improvements/ceiling_blocks.json`

Documentation:

- `PHASE3C_TWEAKS.md`

Access wideners/accessors:

- no access-widener file changed;
- no accessor mixin, reflection, or new access-widening entry was added;
- the Phase 2B `ServerPlayer` shoulder entries remain necessary and validate successfully.

## Validation

### Tweaks compilation

Command:

```text
gradlew.bat :charmony-tweaks:compileJava --console=plain
```

Result: **BUILD SUCCESSFUL**. All 18 known Java errors are resolved. Neither of the two prior Tweaks mixin warnings is emitted.

### Tweaks full build

Command:

```text
gradlew.bat :charmony-tweaks:build --console=plain
```

Result: **BUILD SUCCESSFUL**. Compilation, resources, tests (none present), access-widener validation, sources JAR, remapped JAR, assembly, and build complete.

### Aggregate build

Command:

```text
gradlew.bat build --console=plain
```

Result: **BUILD SUCCESSFUL** across the root, Charmony API/core, and all nine feature modules. This is compile/build parity only and does not establish runtime parity.

### Compile-level parity audit

All 11 implementation `compileJava` tasks were forced with `--rerun-tasks`. They succeed. Remaining output outside Tweaks:

- Charmony core reports a general deprecated-API note;
- Decor reports deprecated API use in its chair renderer registration;
- Glint Colors reports three unresolved mixin warnings: one missing `EquipmentLayerRenderer.renderLayers` target and two indeterminate `LayerRenderStateMixin` injection descriptors.

After the renderer-registration migration, no Tweaks-specific compiler deprecation or mixin warning remains. The three Glint Colors warnings are definite runtime migration risks and require a separate targeted phase; they were not changed under the Tweaks-only scope.

## Runtime risks and required testing

Compilation cannot verify injection semantics, input behavior, or gameplay parity. Runtime testing must cover at minimum:

- all ten side-gated features on integrated and dedicated servers;
- each key binding with remapped keys, modifiers, keyboard layouts, container screens, and normal gameplay screens;
- Item Tidying on player and external inventories, including multiple-inventory mode;
- main-hand and offhand durability for crop replanting and both path conversions;
- item-frame-hiding particle color, opacity, motion, collision response, lifetime, and sprite animation;
- mineshaft generation with iron-chain ceilings and correctly attached hanging lanterns;
- animal feeding followed by automatic item restocking, including non-food animal interactions;
- parrot shoulder retention and normal dismount conditions on a dedicated server;
- custom Snow Golem and Wandering Trader renderer selection/textures;
- access-widener and all Tweaks mixin application during client and dedicated-server startup.

The aggregate build becoming green does not complete the port. Glint Colors mixins, remaining deprecations, game startup, world creation, registry loading, data-pack validation, and feature-by-feature runtime parity remain outstanding.
