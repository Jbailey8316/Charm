# Phase 3D: Clean Pre-Runtime Baseline

## Scope and outcome

The three remaining Glint Colors mixin target warnings are resolved against the exact Minecraft 1.21.10 render pipeline. Charmony core and Decor deprecations with direct maintained replacements were also migrated. A forced aggregate build executes all 92 tasks successfully without compiler, mixin-target, access-widener, mapping, resource-processing, deprecation, or unresolved-reference warnings.

This is a clean compile/build baseline, not proof of runtime or feature parity. No Minecraft runtime testing was performed in this phase.

## Glint Colors mixin migrations

### Equipment layer rendering

The recovered mixin targeted the 1.21.6 immediate-render overload:

```text
EquipmentLayerRenderer.renderLayers(
    LayerType, ResourceKey<EquipmentAsset>, Model, ItemStack,
    PoseStack, MultiBufferSource, int, ResourceLocation)
```

Minecraft 1.21.10 uses render states and node submission. The exact mapped overload is now:

```text
EquipmentLayerRenderer.renderLayers(
    LayerType, ResourceKey<EquipmentAsset>, Model<? super S>, S, ItemStack,
    PoseStack, SubmitNodeCollector, int, ResourceLocation, int, int)
```

`EquipmentLayerRendererMixin` targets that descriptor and accepts the added generic entity/render-state object, outline color, and layer index. It still records the same equipment `ItemStack` at method HEAD. This remains the correct behavioral point because 1.21.10 calls `RenderType.armorEntityGlint()` inside this method before submitting the equipment model, so the selected per-item or configured override color is active when the glint render type is chosen.

### Item layer submission

The two recovered `LayerRenderStateMixin` injections targeted:

```text
ItemStackRenderState.LayerRenderState.render(
    PoseStack, MultiBufferSource, int, int)
```

That method no longer exists. Its exact 1.21.10 equivalent is:

```text
ItemStackRenderState.LayerRenderState.submit(
    PoseStack, SubmitNodeCollector, int light, int overlay, int outlineColor)
```

The HEAD and TAIL injections now target `submit` with the complete modern signature. They continue to establish and clear the layer's dye color around submission.

### Deferred item-render state handoff

A descriptor-only retarget would compile but lose colored item glints: in 1.21.10, `LayerRenderState.submit` queues an `ItemSubmit`, while `ItemFeatureRenderer.render` chooses the glint `RenderType` later. Clearing the global color immediately after submission would leave the deferred renderer without per-item state.

Two narrowly scoped mixins preserve that state across the queue:

- `ItemSubmitMixin` captures the active dye color when the immutable 1.21.10 `SubmitNodeStorage.ItemSubmit` record is constructed and exposes it through the existing `FoilColorHolder` contract.
- `ItemFeatureRendererMixin` restores that captured color immediately before the first `ItemRenderer.renderItem` call for each queued item and clears it immediately afterward.

`FoilColorHolder` now exposes its nullable dye color, and `LayerRenderStateMixin` implements the getter for its existing stored value. The new handoff preserves standard and special foil types rather than replacing Minecraft's foil enum or excluding shields/tridents.

This path covers deferred world and GUI item submissions while retaining the existing GUI render-state identity mixin that prevents differently colored GUI items from being batched under one identity. The existing `RenderTypeMixin` continues selecting colored item, translucent, entity, and armor glint render types from the active color.

## Old target to new target mappings

| Recovered target/API | Minecraft 1.21.10 target/API |
|---|---|
| `EquipmentLayerRenderer.renderLayers(..., ItemStack, PoseStack, MultiBufferSource, int, ResourceLocation)` | `renderLayers(..., Object renderState, ItemStack, PoseStack, SubmitNodeCollector, int, ResourceLocation, int, int)` |
| `LayerRenderState.render(PoseStack, MultiBufferSource, int, int)` HEAD | `LayerRenderState.submit(PoseStack, SubmitNodeCollector, int, int, int)` HEAD |
| `LayerRenderState.render(...)` TAIL | `LayerRenderState.submit(...)` TAIL |
| immediate item glint selection | capture color in `ItemSubmit`, restore around deferred `ItemFeatureRenderer.render` |

## Deprecation migrations

### Entity renderer registration

Fabric Rendering v1 deprecates `EntityRendererRegistry.register` and explicitly directs callers to vanilla `EntityRenderers.register`, exposed through Fabric's transitive access widener.

The replacement was applied in:

- Charmony `ClientRegistry.entityRenderer`;
- Decor chair renderer registration.

Entity types, providers, registration timing, feature conditions, and renderer behavior are unchanged.

### Conditional recipe resource loader

Fabric Resource Loader v0's `IdentifiableResourceReloadListener` and registry-aware `ResourceManagerHelper.registerReloadListener` are deprecated in favor of Resource Loader v1.

Charmony now registers the same `ConditionalRecipeManager.ID` and the same registry-aware constructor factory through:

```text
DataResourceLoader.get().registerReloader(id, factory)
```

`ConditionalRecipeManager` no longer implements the obsolete identifiable interface or duplicates the ID through `getFabricId()`. Its recipe codec, registry serialization context, conditional recipe generation, recipe-map replacement, and reload behavior are otherwise unchanged.

## Warnings resolved

- Glint Colors `EquipmentLayerRendererMixin`: missing `renderLayers` target resolved.
- Glint Colors `LayerRenderStateMixin`: HEAD `render` descriptor warning resolved.
- Glint Colors `LayerRenderStateMixin`: TAIL `render` descriptor warning resolved.
- Charmony core: deprecated Fabric entity-renderer helper resolved.
- Decor: deprecated Fabric entity-renderer helper resolved.
- Charmony core: deprecated Fabric v0 identifiable/resource-manager reload APIs resolved.

## Warnings intentionally deferred

None are emitted by the final forced aggregate build. This does not imply that every runtime mixin injection is behaviorally validated; annotation processing and remapping cannot prove runtime semantics.

## Files changed

Glint Colors Java (5):

- `modules/charmony-glint-colors/src/main/java/svenhjol/charmony/glint_colors/client/features/glint_color_templates/FoilColorHolder.java`
- `modules/charmony-glint-colors/src/main/java/svenhjol/charmony/glint_colors/client/mixins/glint_colors/EquipmentLayerRendererMixin.java`
- `modules/charmony-glint-colors/src/main/java/svenhjol/charmony/glint_colors/client/mixins/glint_colors/LayerRenderStateMixin.java`
- `modules/charmony-glint-colors/src/main/java/svenhjol/charmony/glint_colors/client/mixins/glint_colors/ItemSubmitMixin.java`
- `modules/charmony-glint-colors/src/main/java/svenhjol/charmony/glint_colors/client/mixins/glint_colors/ItemFeatureRendererMixin.java`

Glint Colors resources (1):

- `modules/charmony-glint-colors/src/main/resources/charmony-glint-colors.client.mixins.json`

Charmony core Java (3):

- `modules/charmony/src/main/java/svenhjol/charmony/core/client/ClientRegistry.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/features/conditional_recipes/ConditionalRecipeManager.java`
- `modules/charmony/src/main/java/svenhjol/charmony/core/common/features/conditional_recipes/Registers.java`

Decor Java (1):

- `modules/charmony-decor/src/main/java/svenhjol/charmony/decor/client/features/chairs/Registers.java`

Documentation:

- `PHASE3D_PRE_RUNTIME.md`

No access widener, accessor, build file, dependency, data file, texture, model, translation, sound, or unrelated module file changed.

## Validation

### Glint Colors compilation

Command:

```text
gradlew.bat :charmony-glint-colors:compileJava --rerun-tasks --console=plain
```

Result: **BUILD SUCCESSFUL**. The three original mixin warnings are absent, and no new warning is emitted.

### Glint Colors build

Command:

```text
gradlew.bat :charmony-glint-colors:build --console=plain
```

Result: **BUILD SUCCESSFUL**. Compilation, resource processing, access-widener validation, remapping, sources JAR, assembly, and build complete.

### Aggregate clean-warning build

Command:

```text
gradlew.bat build --rerun-tasks --console=plain
```

Result: **BUILD SUCCESSFUL**. All 92 tasks execute. The complete output contains no:

- Java compilation error or warning;
- mixin target/descriptor warning;
- access-widener warning;
- mapping warning;
- resource-processing error;
- deprecated API note;
- unresolved reference.

## Known runtime risks

Runtime testing is still required for:

- every dye color and the configured global override on GUI, held, dropped, framed, armor, and other world-rendered items;
- multiple differently colored items rendered in the same GUI or world frame, verifying no color leaks between queued submissions;
- standard, special, translucent, entity, and armor foil paths;
- shields, tridents, enchanted books, armor trims, custom models, and special model renderers;
- resource reloads and builder recreation for all custom glint render types;
- conditional recipe reload ordering, parsing, data-pack reloads, and recipe-manager replacement;
- chair, Moobloom, and custom mob renderer registration during client startup;
- dedicated-server startup to ensure client-only mixins/classes remain isolated;
- all remaining mixins at runtime, since compile-time validation cannot prove injection semantics.

The next phase should start the game and perform registry/resource/mixin smoke testing before any broader parity claim.
