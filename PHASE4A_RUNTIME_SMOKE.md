# Phase 4A Runtime Smoke Test

## Scope and environment

- Date: 2026-08-07
- Minecraft: 1.21.10
- Fabric Loader: 0.19.3
- Fabric API: 0.138.4+1.21.10
- Java: 21.0.2
- Launch task: `./gradlew runClient`
- Validation build: `./gradlew build`

## Launch result

The initial development launch failed before the title screen because the aggregate runtime classpath selected the remapped (`intermediary`) module artifacts. Loom's development runtime is `named`, so module access wideners could not be read. The aggregate dependency declaration now selects each subproject's `namedElements` artifact for development while retaining `include project(path)` for the assembled mod.

After correcting the development classpath, three runtime-only mixin descriptor failures were exposed and fixed:

- Glint Colors `ItemModelsMixin`: the 1.21.10 `BlockModelWrapper.update` owner parameter is `ItemOwner`, not `LivingEntity`.
- Tweaks `ItemInHandRendererMixin`: first-person item rendering now receives `SubmitNodeCollector`, and the `renderItem` invocation descriptor was retargeted accordingly.
- Tweaks `ScreenEffectRendererMixin`: `renderFire` now includes a `TextureAtlasSprite` parameter.

Following these fixes, the client reached the title screen. No fatal mixin failure, access-widener failure, registry failure, missing dependency, or renderer exception remained in the final launch.

## Modules loaded

Fabric Loader reported all recovered Charm/Charmony modules with their expected identities and versions:

- Charm 8.8.23
- Charmony 1.44.4
- Charmony API 1.26.15
- Azalea Wood 1.2.0
- Brew and Stew 1.6.3
- Collection 1.7.0
- Decor 1.7.0
- Ebony Wood 1.2.0
- Glint Colors 1.8.4
- Mooblooms 1.7.0
- Totem of Preserving 1.8.1
- Tweaks 1.13.6

Client and common feature initialization logs were present for all nine feature modules. Charmony also confirmed that the client connected to a Charmony server and that enabled features were active.

## World creation and persistence

A new Creative single-player world named `New World1` was created with default generation settings.

- Initial chunk generation completed in 5.688 seconds.
- The player joined successfully at `(10.5, 64.0, 3.5)`.
- No crash occurred during initial chunk generation.
- The overworld, Nether, and End were saved.
- Save and Quit disconnected the player and shut down the integrated server normally.
- `level.dat` was written successfully.
- The same world was selected and entered again twice.
- Both reloads started the integrated server, loaded spawn chunks, and allowed the player to join successfully.

## Registry and representative-content smoke test

Registry bootstrap completed without duplicate-registration or missing-registry errors. The Creative inventory opened successfully. Its search tab returned rendered item entries for:

- Azalea Planks (Azalea Wood)
- Cask (Brew and Stew)
- Ebony Planks (Ebony Wood)
- Glint Color Smithing Template (Glint Colors)
- Moobloom Spawn Egg (Mooblooms, also confirming the entity's representative item registration)
- Totem of Preserving (Totem of Preserving)

Collection, Decor, and Tweaks do not expose an equivalent representative standalone item for every feature, so their Phase 4A verification is module/feature initialization and successful world join rather than an item-search result. Decor's Chairs client feature and Tweaks' enabled client/common feature sets initialized. Full entity spawning, chair interaction, enchantment behavior, and individual tweak behavior remain Phase 4B work.

## Resource and data loading

All module resource packs are discovered and included in the client `ResourceManager` reload. All module datapacks are also discovered by the integrated server.

The exact metadata failure was caused by every physical mod-root `pack.mcmeta`: the root Charm file plus Charmony core/API and all nine feature-module files. Minecraft 1.21.10 uses different current formats for the two pack types:

- client resources use format 69, whose last legacy/pre-minor format is 64;
- server data uses format 88, whose last legacy/pre-minor format is 81.

The same physical file is parsed using `PackMetadataSection.CLIENT_TYPE` and `SERVER_TYPE`. A range beginning at 69 is valid modern resource metadata, but under the server-data codec it crosses the legacy 17–81 interval and therefore requires legacy `pack_format`/`supported_formats`. Adding those fields makes the server parser happy but is explicitly rejected by the modern client-resource parser. Widening one shared range cannot represent the two declarations correctly.

Fabric Resource Loader 0.138.4's installed `ModResourcePackUtil.openDefault` is the intended solution for mod roots: when a physical `pack.mcmeta` is absent, it synthesizes metadata using Minecraft's current format for the requested `PackType`. The twelve obsolete shared templates were therefore removed. Built resources and remapped JARs were inspected to confirm that they no longer contain physical `pack.mcmeta` files.

Runtime verification after removal shows:

- client resource reload includes Charm, Charmony, API, and all nine modules without metadata rejection;
- integrated-server datapack discovery produces no metadata rejection;
- recipes and advancements load;
- world join, save, shutdown, and re-entry succeed.

Clean datapack loading exposed two previously hidden recipe errors. Minecraft 1.21.10 renamed the chain item ID from `minecraft:chain` to `minecraft:iron_chain`. The Azalea and Ebony hanging-sign recipes were updated to the verified `Items.IRON_CHAIN` registry ID. A fresh client run and two integrated-server loads contain no recipe parse errors.

No missing model, missing texture, loot parse, networking, renderer, fatal resource-reload, mixin-application, or injection errors were observed in the final logs.

## Other runtime warnings

| Warning | Classification | Finding |
|---|---|---|
| Mojang authentication/profile/key requests fail with `PKIX path building failed` | Harmless external/network issue | The local Oracle JDK does not trust the certificate chain presented for Mojang service endpoints. Charm does not initiate these requests. Offline development login and the integrated server continue to work. |
| Missing offline chat Services public key | Development-environment-only | It follows from the offline development identity and failed Mojang key request. Minecraft ignores that profile's signed-chat session; Charm gameplay is unaffected. |
| Untranslated convention item tags | Cosmetic warning | Verbose validation identifies nine Charmony tags: `enchantables`, `colored_candles`, `piglin_barters_for_fortresses`, `villager_loved`, `colored_bundles`, `piglin_barters_for_directions`, `colored_dyes`, `repairable_using_scrap`, and `piglin_barters_for_bastions`. Recipe-viewer display names may be less friendly, but tag contents and gameplay load. Translation keys should be added during the data/translation parity phase. |
| Tweaks Telemetry feature/mixins not running | Cosmetic warning | The recovered original declares `Telemetry` with `enabledByDefault = false` because of its intentionally controversial behavior. The generated configuration preserves `Enabled = false`; the mixin plugin correctly omits its mixins. This is not a port regression. |
| Tweaks `RespawnAnchorsWorkEverywhere` feature/mixin not running | Cosmetic warning | The feature was introduced and shipped in the original with `enabledByDefault = false`, explicitly because it changes core gameplay. The recovered configuration preserves `Enabled = false`. Its 1.21.10 mixin still targets the existing `RespawnAnchorBlock.canSetSpawn(Level)` method and conditionally returns true only when enabled. It must remain available for opt-in parity, but it must not be enabled by default merely to silence the lifecycle log. |
| Missing root `build/classes/java/main` classpath entry | Development-environment-only | The aggregate root intentionally has no Java sources, while Loom still contributes its conventional main-classes path to the development launch. Modules and resources load from their named development outputs. This does not occur as a missing class in the assembled artifact and caused no runtime failure. |

## Build result

`./gradlew build --rerun-tasks` succeeds with all 92 tasks executed after the metadata correction. The required final `./gradlew build` also succeeds.

## Files changed

- `build.gradle`
- Removed the obsolete root and eleven module `pack.mcmeta` files so Fabric can synthesize type-specific metadata
- `modules/charmony-azalea-wood/src/main/resources/data/charmony/recipe/azalea_wood/azalea_hanging_sign.json`
- `modules/charmony-ebony-wood/src/main/resources/data/charmony/recipe/ebony_wood/ebony_hanging_sign.json`
- `modules/charmony-glint-colors/.../ItemModelsMixin.java`
- `modules/charmony-tweaks/.../ItemInHandRendererMixin.java`
- `modules/charmony-tweaks/.../ScreenEffectRendererMixin.java`
- `PHASE4A_RUNTIME_SMOKE.md`

No unrelated gameplay implementation, recipes, loot, models, textures, registrations, configuration, or data files were changed.

## Recommended next runtime-test phase

Phase 4A is ready to commit. The next phase should test placed blocks and block entities, recipes, Moobloom spawning and interaction, Totem persistence through death/save/reload, Decor chairs, world generation, Glint Colors in GUI/world rendering, and representative Tweaks behavior. The nine cosmetic missing tag translations should be handled with the translation/data parity work rather than mixed into this runtime-unblocking commit.
