# Charm Minecraft 1.21.10 Fabric Port Audit and Plan

## Audit status

- Audit date: 2026-08-07
- Port branch: `port/1.21.10`
- Aggregate mod version: `8.8.23`
- Original Minecraft version: **1.21.6**
- Target Minecraft version: **1.21.10**
- Target loader: Fabric
- Target Java: 21
- Source code changed by this audit: none

This document is the initial parity baseline. Compilation alone will not establish parity.

## Scope and important repository-layout finding

This checkout is an **aggregate/bundling repository**, not a self-contained copy of the Charm implementation. It has no Java source tree and almost no content resources. Its `build.gradle`:

1. applies a convention script from `../../java/build.gradle` when present;
2. otherwise downloads `https://charmony.dev/files/main/build.gradle`; and
3. embeds nine separately published Charmony feature mods.

The local convention file is absent, and the remote convention URL was unreachable during this audit. Consequently, the exact interpolated values hidden in that script cannot all be independently reproduced from this checkout alone.

To obtain a useful behavioral baseline, this audit also inspected read-only temporary clones of the authoritative upstream Charmony core, API, and nine embedded feature repositories. Each clone was checked out to its latest commit at or before 2025-08-25 15:38 UTC, aligning it with this aggregate repository's 1.21.6/8.8.23 release period. Those external files were not copied into or added to this repository.

The port must decide early whether Charm will remain an aggregate of separately ported artifacts or whether the upstream sources will be brought into a reproducible multi-project build. Either approach must preserve all features; merely making the empty aggregate compile would not constitute a port.

## Evidence used

Local evidence:

- `CHANGELOG.md` states that 8.8.20 updated to Minecraft 1.21.6.
- `gradle.properties` sets aggregate version 8.8.23.
- `build.gradle` uses Fabric Loom `1.11-SNAPSHOT`.
- the wrapper uses Gradle `8.14.2`.
- `fabric.mod.json` requires Fabric Loader, Fabric API, Charmony, Charmony API, and Java through placeholders supplied by the shared convention.
- source imports in the historical upstream snapshot use Mojang's official class names, not Yarn names.

Upstream primary sources inspected:

- [Charm](https://github.com/svenhjol/Charm)
- [Charmony core](https://github.com/svenhjol/charmony)
- [Charmony API](https://github.com/svenhjol/charmony-api)
- the nine module repositories listed below
- [Fabric Maven metadata](https://maven.fabricmc.net/)

## Current build and platform versions

| Component | Current/original value | Confidence and notes |
| --- | --- | --- |
| Minecraft | **1.21.6** | Confirmed by the local changelog and upstream 1.21.6 port history. |
| Fabric Loader | **0.16.14, likely** | The exact value is hidden in the unavailable shared convention script. This was the current stable loader during the release window and must be confirmed from a preserved 8.8.23 build artifact or recovered convention script before changing the build. |
| Fabric API | **0.127.1+1.21.6, likely** | The last 1.21.6 Fabric API published in the release window. The exact value is hidden in the unavailable shared convention script and must be confirmed before changing the build. |
| Mappings | **Mojang official mappings for 1.21.6** | Strongly indicated by the source namespace and names such as `BuiltInRegistries`, `ResourceLocation`, and `GuiGraphics`. No Yarn coordinate is stored locally. |
| Fabric Loom | **1.11-SNAPSHOT** | Explicit in `build.gradle`; the previous commit used `1.10-SNAPSHOT`. |
| Gradle | **8.14.2** | Explicit in `gradle/wrapper/gradle-wrapper.properties`. |
| Java | **21** | Required by the port instructions and appropriate for Minecraft 1.21.x; the manifest value is injected by the missing shared convention. Confirm the original injected value from a release artifact. |
| Aggregate Charm | **8.8.23** | Explicit in `gradle.properties`. |

The two “likely” version values must not be silently treated as recovered facts. A phase-1 task is to obtain the original expanded `fabric.mod.json`, Gradle dependency report, or published 8.8.23 JAR and record the exact values.

For the target, official Fabric Maven metadata contains Fabric API releases through `0.138.4+1.21.10`, and the local dependency cache contains `1.21.10+build.2` Yarn mappings. Because this codebase uses Mojang names, the initial target build should retain official Mojang mappings unless a deliberate, separately reviewed mapping migration is approved.

## Local project structure

```text
.
|-- AGENTS.md
|-- CHANGELOG.md
|-- README.md
|-- build.gradle
|-- gradle.properties
|-- settings.gradle
|-- gradlew / gradlew.bat
|-- gradle/wrapper/
|   |-- gradle-wrapper.jar
|   `-- gradle-wrapper.properties
`-- src/main/resources/
    |-- fabric.mod.json
    |-- pack.mcmeta
    `-- assets/charmony/textures/gui/sprites/charm.png
```

There is no local `src/main/java`, `src/client/java`, data pack tree, mixin configuration, access widener, data generator, test tree, or game-test tree.

## External implementation structure

The historical implementation is split across:

| Component | Historical version | Role |
| --- | ---: | --- |
| Charmony core | 1.44.4 | registries, feature lifecycle, configuration, networking, screens, helpers, mixins, wood framework |
| Charmony API | 1.26.15 | annotations, callbacks, API contracts, events, config interfaces |
| charmony-azalea-wood | 1.2.0 | azalea wood family and vanilla azalea-tree modification |
| charmony-brew-and-stew | 1.6.3 | cooking pot, cask, recipes, block entities, sounds, UI/network behavior |
| charmony-collection | 1.7.0 | Collection enchantment/loot behavior |
| charmony-decor | 1.7.0 | chair entity and rendering |
| charmony-ebony-wood | 1.2.0 | ebony wood family, sapling/tree, loot and trades |
| charmony-glint-colors | 1.8.4 | glint templates and custom rendering |
| charmony-mooblooms | 1.7.0 | moobloom entity, spawning, model/renderer, worldgen links |
| charmony-totem-of-preserving | 1.8.1 | death-drop preservation item, holder block entity, networking/rendering |
| charmony-tweaks | 1.13.6 | large set of gameplay, AI, rendering, loot, trade, UI, and interaction tweaks |

## Content inventory

### Strictly local inventory

These counts describe only tracked files in this checkout before creation of this plan.

| Category | Original total in this checkout | Ported to 1.21.10 | Remaining | Intentionally changed | Blocked |
| --- | ---: | ---: | ---: | ---: | ---: |
| Java source files | 0 | 0 | 0 | 0 | 0 |
| Registered blocks | 0 | 0 | 0 | 0 | 0 |
| Registered items | 0 | 0 | 0 | 0 | 0 |
| Entities | 0 | 0 | 0 | 0 | 0 |
| Block entities | 0 | 0 | 0 | 0 | 0 |
| Recipes | 0 | 0 | 0 | 0 | 0 |
| Tag files | 0 | 0 | 0 | 0 | 0 |
| Loot-table files | 0 | 0 | 0 | 0 | 0 |
| Advancement files | 0 | 0 | 0 | 0 | 0 |
| Structures | 0 | 0 | 0 | 0 | 0 |
| Worldgen JSON files | 0 | 0 | 0 | 0 | 0 |
| Sound definitions/files | 0 | 0 | 0 | 0 | 0 |
| PNG textures | 1 | 0 | 1 | 0 | 0 |
| Model JSON files | 0 | 0 | 0 | 0 | 0 |
| Translation files/keys | 0 | 0 | 0 | 0 | 0 |
| Config/integration Java | 0 | 0 | 0 | 0 | 0 |

### Historical cross-module parity baseline

These are practical filesystem and registration counts from the aligned upstream snapshot. They are the meaningful minimum parity baseline for the bundled Charm product.

| Category | Original total | Currently ported in this branch | Remaining | Intentionally changed | Blocked/notes |
| --- | ---: | ---: | ---: | ---: | --- |
| Java source files | 627 | 0 | 627 | 0 | Source remains external. |
| Registered/resource-defined blocks | 39 | 0 | 39 | 0 | Counted from blockstate IDs: 36 wood-family blocks plus cooking pot, cask, and totem holder. |
| Registered/resource-defined items | 42 | 0 | 42 | 0 | Counted from modern item-definition IDs, including block items, boats, spawn egg, stew, template, and totem. |
| Entity types | 6 | 0 | 6 | 0 | Two boats and two chest boats, chair, and moobloom. |
| Block entity types | 3 | 0 | 3 | 0 | Cooking pot, cask, and totem holder. |
| Recipe JSON files | 35 | 0 | 35 | 0 | Includes conditional/custom recipe behavior in code. |
| Tag JSON files | 94 | 0 | 94 | 0 | File count, not unique values. |
| Loot-table JSON files | 40 | 0 | 40 | 0 | Additional runtime loot-table modification exists. |
| Advancement JSON files | 72 | 0 | 72 | 0 | Includes module/root advancements. |
| Structures | 0 | 0 | 0 | 0 | No structure JSON or NBT files found in the aligned modules. |
| Worldgen JSON files | 6 | 0 | 6 | 0 | Does not include code-driven configured-feature mutation, biome modification, or spawning. |
| Sound events | 16 | 0 | 16 | 0 | Four `sounds.json` files. |
| OGG sound files | 21 | 0 | 21 | 0 | Preserve all binaries. |
| PNG textures | 125 | 0 | 125 | 0 | Includes UI, entity, block, item, and icon textures. |
| Traditional model JSON files | 127 | 0 | 127 | 0 | Block/item/entity-support models under `models`. |
| Item-definition JSON files | 42 | 0 | 42 | 0 | Separate post-1.21 item model definitions under `items`. |
| Translation files | 10 | 0 | 10 | 0 | English only in the aligned snapshot. |
| Translation keys | 254 | 0 | 254 | 0 | Key count across those files. |
| Feature definitions | 83 | 0 | 83 | 0 | Classes annotated with `@FeatureDefinition`. |
| Configurable fields | 95 | 0 | 95 | 0 | Fields annotated with `@Configurable`. |
| Mixin implementation classes | 94 | 0 | 94 | 0 | A major compatibility and maintenance risk. |
| Explicit integration classes | 1 | 0 | 1 | 0 | Mod Menu plugin; integrations also occur through build/runtime detection. |

Counting limitations:

- dynamic registry helpers and conditional features make source-registration counts more authoritative than file counts in some areas;
- generated resources were not available, so any resources created only by data generation are not counted;
- runtime modifications to vanilla loot, configured features, trades, spawns, recipes, and renderers are tracked as code behavior rather than extra JSON files;
- the temporary upstream snapshots are an audit reference, not tracked input, so their commit hashes should be recorded in an implementation manifest if that source is imported.

## Dependency findings

### Required runtime/build dependencies

- Fabric Loader.
- Fabric API, including lifecycle, events, loot API v3, networking, object builder, particles, rendering, resource loading, tags, and biome APIs.
- Charmony core and Charmony API.
- Nine embedded Charmony feature artifacts listed above.
- Mod Menu is an optional integration exposed by Charmony's `ModMenuPlugin`.
- The shared source also imports NightConfig, toml4j, MixinExtras, and Apache Commons classes.
- FastUtil, Netty, JOML, LWJGL, ASM, Guava, DataFixerUpper, and logging APIs are used through Minecraft/loader transitive dependencies.

### Dependencies needing update, replacement, or explicit pinning

1. **Remote shared Gradle convention:** the build currently depends on a live URL that was unavailable during the audit. This is non-reproducible and the highest-priority bootstrap problem. Recover and pin the convention logic or replace it with reviewed local convention code.
2. **Charmony core/API:** all embedded modules compile against these contracts. They must be ported and published/pinned before feature modules can be validated.
3. **Nine feature artifacts:** an aggregate-only version bump is insufficient. Each artifact needs a 1.21.10-compatible build or its source must be included in the port workspace.
4. **Fabric API:** select and pin a 1.21.10 release after compiling the core/API. `0.138.4+1.21.10` exists, but choosing it should be validated against the chosen Loader and Loom versions.
5. **Fabric Loader:** select a stable 1.21.10-compatible release and set a real manifest minimum instead of relying on an opaque placeholder.
6. **Loom:** replace the snapshot plugin with a stable, pinned version that supports Minecraft 1.21.10 and Gradle 8.14.2, unless a verified snapshot-only capability is required.
7. **Mappings:** keep official Mojang mappings initially. Switching to Yarn would create a large unrelated name migration across 627 files.
8. **Config stack:** verify that NightConfig/toml4j and Charmony's annotation/reflection-based config system remain compatible with Java 21 and the target runtime. Do not replace the configuration format without a migration path.
9. **MixinExtras/Mixin:** pin compatible versions and audit every injection against target bytecode.
10. **Mod Menu:** update the optional integration API and verify control-panel navigation and restart-required state.

## APIs likely to have changed significantly by 1.21.10

The historical source and upstream 1.21.9 migration commits identify these migration fronts:

1. **Client GUI and rendering:** `GuiGraphics`, GUI render-state extraction, HUD rendering callbacks, screen/widget rendering, scissor/pose behavior, tooltip components, and background tint mixins.
2. **Particle rendering:** particle render types, render pipelines/buffers, deferred/custom particles, and client particle registration.
3. **Entity rendering and models:** renderer contexts, render states, model layers, texture selection, moobloom/chair renderers, and custom glint rendering.
4. **Item rendering/model definitions:** the post-1.21 item-definition format, glint layers, item model selection, and data-component-dependent visuals.
5. **Registry construction:** block/item settings now carry registry keys in many construction paths; entity/block-entity builders, sound registration, and creative-tab placement must be checked.
6. **Networking:** custom payload types/codecs, receiver registration, server/client context and thread handling, and block-entity/menu synchronization.
7. **Data components and serialization:** ItemStack component access, component mixins, StreamCodec/Codec signatures, HolderLookup-aware persistence, and custom item state.
8. **Block entities, menus, and screens:** save/load signatures, update packets/tags, menu factories, screen-handler registration, slot behavior, and sided synchronization.
9. **Recipes and recipe manager:** serializer codecs, recipe input types, recipe-book/category APIs, conditional recipe loading, and the RecipeManager mixin.
10. **Loot APIs:** Fabric loot API v3 callback signatures, loot contexts, codecs, holder lookup, built-in table keys, and custom loot functions.
11. **Tags and conventional tags:** Fabric conventional-tag constants, holder-based tag access, data-pack paths, and tag lookup timing.
12. **Worldgen and biome modification:** configured/placed feature codecs and registry bootstrap, biome modification phases, spawn injection, and the azalea feature's direct mutation of a vanilla `TreeConfiguration`.
13. **Entities and AI:** Brain/memory module APIs, goals, navigation, attributes, spawn restrictions, mob conversion/drop hooks, and renderer synchronization.
14. **Wood/block APIs:** `BlockSetType`, `WoodType`, sign/hanging-sign constructors, boats/chest boats, pressure plates, saplings, flammability, fuels, stripping, and dispenser behavior.
15. **Resource-pack/data-pack schemas:** pack format, singular data directories, model/item definitions, loot/advancement/worldgen schemas, and registry IDs.
16. **Mixins and access wideners:** target names/descriptors and field mutability. Ninety-four mixin classes plus access widener entries require individual target verification.

## High-risk porting areas

### Critical

- **Missing implementation source in this checkout.** Work cannot proceed beyond aggregate bootstrap without ported/pinned Charmony core, API, and module artifacts.
- **Unavailable live build convention.** Builds are not reproducible and exact original dependency values are obscured.
- **Charmony core/API contract migration.** Every module depends on lifecycle, registry, config, callbacks, networking, and UI abstractions defined there.
- **Mixin surface.** Ninety-four mixin classes touch menus, entities, AI, rendering, tooltips, interactions, loot, recipes, and client lifecycle. Compile success will not detect semantically shifted injection points.
- **Client rendering/glint/GUI.** The upstream 1.21.9 work already changed client registry, particles, GUI lists, render state, and access wideners; glint colors required a substantial dedicated migration.

### High

- cooking pot and cask block entities, recipes, inventory transfer, persistence, networking, and screens;
- totem death-drop interception, persistent storage, spawning/placement, block entity synchronization, and rendering;
- moobloom entity data, AI, spawning, model/renderer, textures, milking, and worldgen interaction;
- custom boats, chest boats, signs, hanging signs, saplings, and wood registration;
- direct modification of the vanilla azalea configured feature after server start;
- piglin memory/AI, wandering trader trades, biome spawns, mob drops, and other code-driven tweaks;
- custom/conditional recipes and recipe-manager mixins;
- loot callbacks and custom loot functions;
- configuration discovery, feature gating, mixin-disable behavior, and Mod Menu/control-panel integration.

### Medium

- JSON schema and pack-format updates across 35 recipes, 94 tag files, 40 loot tables, 72 advancements, and 6 worldgen files;
- creative-tab ordering, fuel/flammability/stripping/dispenser hooks;
- sounds and particles;
- translations and advancement trees;
- asset path validation and case sensitivity.

## Phased migration plan

Each phase ends with a clean `gradlew.bat build` on Windows (equivalent to `./gradlew build`), relevant focused tests, a parity update, and a small logical commit. Do not start the next major phase with unexplained compilation errors.

### Phase 0: Preserve and pin the source baseline

- Recover the exact 8.8.23 resolved manifest/dependency report or published JAR.
- Record exact commits/artifact checksums for Charmony core, API, and all nine feature modules.
- Decide the reproducible architecture: pinned external multi-repository artifacts or an imported composite/multi-project source build.
- Preserve every binary asset and configuration default.
- Expand this parity table with per-module and per-feature rows.

Exit criteria: every original feature has an identified source/resource owner and immutable baseline.

### Phase 1: Build system and project bootstrap

- Replace the unreachable convention dependency with reviewed, pinned build logic.
- Set Minecraft 1.21.10, Java 21, stable Loom, stable Loader, official Mojang mappings, and a pinned 1.21.10 Fabric API.
- Update manifest and pack-format placeholders without weakening dependency constraints.
- Establish reproducible repositories and artifact coordinates.
- Add CI/build commands only after the local build is deterministic.

Exit criteria: the aggregate and empty/core scaffolding resolve dependencies and build reproducibly.

### Phase 2: Charmony API and core initialization

- Port API annotations, callbacks, feature contracts, side/environment handling, and config interfaces.
- Port mod lifecycle, registry abstractions, feature discovery, common/client initializers, and logging.
- Port access wideners and the minimum core mixins.
- Add smoke checks for feature discovery and registry initialization.

Exit criteria: core/API compile and initialize with no feature modules.

### Phase 3: Configuration, networking, and core UI foundations

- Port TOML/config discovery, defaults, validation, feature enable/disable, and mixin-disable behavior.
- Port payload codecs, client/server receivers, synchronization helpers, and login/respawn hooks.
- Port control panel, Mod Menu integration, widgets, tooltips, toasts, and restart-required flow.

Exit criteria: configuration round-trips, packets register, and control-panel screens open without errors.

### Phase 4: Core registries, items, and wood framework

- Port block/item/entity/block-entity/menu/sound/particle registry helpers.
- Port data-component helpers and creative-tab placement.
- Port `BlockSetType`, `WoodType`, signs, hanging signs, boats, chest boats, stripping, fuel, flammability, and dispenser hooks.

Exit criteria: registry dump matches the phase baseline and a minimal test wood set loads.

### Phase 5: Items, blocks, and construction families

- Port azalea and ebony families with all 36 resource-defined blocks and their items.
- Port cooking pot, cask, totem holder, mixed stew, glint template, totem item, and moobloom spawn egg.
- Verify block states, placement, drops, creative visibility, tools, collision, and save/reload.

Exit criteria: 39 block IDs and 42 item IDs are accounted for and usable.

### Phase 6: Recipes, tags, loot, advancements, and data

- Migrate all 35 recipes, 94 tags, 40 loot tables, and 72 advancements.
- Port conditional recipes, custom recipe serializers, loot callbacks/functions, trades, and advancement triggers.
- Validate data packs with game startup/reload and targeted recipe/loot tests.

Exit criteria: all data files load without errors and parity counts match.

### Phase 7: World generation, biome features, and spawning

- Port six worldgen JSON files and code-driven biome/spawn changes.
- Replace unsafe direct configured-feature mutation with the correct modern equivalent while preserving azalea log behavior.
- Port ebony tree/sapling behavior, sniffer loot integration, and moobloom spawning.

Exit criteria: repeated new-world tests demonstrate expected trees, spawns, and no registry-freeze errors.

### Phase 8: Entities, AI, models, renderers, and animations

- Port custom boats/chest boats, chair, and moobloom entity types.
- Port AI/goals/brain-memory changes and all entity-related tweaks.
- Port models, model layers, render states, textures, animations, spawn attributes, and persistence.

Exit criteria: all six entity types spawn, render, behave, save, reload, and drop correctly.

### Phase 9: Block entities, menus, screens, and item transfer

- Port cooking pot, cask, and totem block entities.
- Port codecs/NBT, update packets, menus, screens, slots, hoppers, item transfer, and client synchronization.
- Test multiplayer-authoritative behavior and save/reload.

Exit criteria: all three block entities retain state and remain synchronized without duplication or loss.

### Phase 10: Gameplay tweaks, mixins, and integrations

- Port tweak features in small thematic batches: interactions, inventory/UI, trades, mobs/AI, drops/loot, tools, and quality-of-life behavior.
- Audit every mixin target and access-widener entry against 1.21.10 bytecode.
- Maintain a table of enabled, disabled, replaced, and blocked injections.

Exit criteria: all 83 features and 95 config fields have an explicit parity disposition.

### Phase 11: Client rendering, glint, particles, and sounds

- Port GUI render-state hooks, HUD renderers, tooltips, item containers, background tinting, and custom glint colors.
- Port custom/deferred particles and entity/item render layers.
- Validate all 16 sound events, 21 OGG files, and client-only class boundaries.

Exit criteria: client startup, resource reload, rendering, particles, and sounds pass targeted tests.

### Phase 12: Assets and resource parity

- Validate all 125 PNGs, 127 traditional models, 42 item definitions, 10 translation files/254 keys, and pack metadata.
- Check missing textures/models, atlas warnings, case-sensitive paths, animation metadata, and unused-but-preserved binaries.

Exit criteria: clean resource reload and a documented reason for every intentional asset difference.

### Phase 13: Full parity audit

- Recount every category from built sources/resources.
- Compare registry dumps and config defaults with the original artifact.
- Review every original feature and known compatibility integration.
- Record unavoidable behavior differences and blockers.

Exit criteria: no unclassified remaining items; compilation alone is not accepted as completion.

### Phase 14: Runtime testing and cleanup

- Test dedicated and integrated server startup.
- Create multiple single-player worlds and test reload.
- Exercise recipes, blocks, items, entities, spawning, worldgen, rendering, sounds, networking, and persistence.
- Test with and without Mod Menu and with representative compatible mods.
- Fix migration warnings and remove only temporary port scaffolding.

Exit criteria: documented runtime matrix passes, known issues are recorded, and the final parity table is accurate.

## Validation matrix

Minimum recurring checks:

- `gradlew.bat build`
- client startup and resource reload
- dedicated server startup
- registry loading and registry-count comparison
- single-player world creation
- data-pack reload
- config create/edit/reload
- disconnect/reconnect and save/reload
- representative recipe, loot, advancement, block, item, entity, worldgen, rendering, particle, and sound tests

## Known problems and open questions

1. The shared convention URL is unavailable, so the current checkout cannot resolve from a clean environment as designed.
2. Exact original Fabric Loader and Fabric API values remain to be recovered from an expanded 8.8.23 artifact or preserved build report.
3. Exact source/artifact checksums for the dependencies used in the published aggregate are not stored locally.
4. No Java, unit, game, or integration tests exist in this checkout.
5. The aggregate manifest accepts any Minecraft and Fabric API version at runtime after placeholder expansion; target constraints must be explicit and tested.
6. It is not yet decided whether the port will publish eleven coordinated artifacts or use a composite/monorepo build.
7. Dynamic and generated registrations require a runtime registry dump before final counts can be considered authoritative.

## Recommended next task

Begin Phase 0, not source migration: recover the resolved 8.8.23 artifact/build metadata, pin the exact eleven source baselines, and choose a reproducible multi-module build layout. Then perform Phase 1 as a build-only commit.
