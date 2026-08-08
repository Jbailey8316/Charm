# Phase 1: Minecraft 1.21.10 Build Foundation

## Scope and result

This phase replaces the unavailable shared build infrastructure with repository-local Gradle configuration. It preserves the recovered Charm/Charmony implementation at the exact Phase 0 revisions and does not change Java source or resources.

The foundation meets the Phase 1 success criteria:

- Gradle configures successfully.
- All declared external dependencies resolve.
- Fabric Loom initializes for the root and all 11 implementation projects.
- Minecraft 1.21.10 and Java 21 are selected.
- All recovered main Java and resource source sets are part of the build.
- Java compilation begins against Minecraft 1.21.10.

The full build is not yet successful. This is expected: the first source and access-widener incompatibilities with Minecraft 1.21.10 are now exposed and recorded below.

## Selected toolchain and dependencies

| Component | Selected version | Notes |
|---|---:|---|
| Gradle wrapper | 8.14.2 | Existing wrapper retained. |
| Fabric Loom | 1.11.8 | Published stable Loom release compatible with Gradle 8.14 and Java 21; replaces `1.11-SNAPSHOT`. |
| Minecraft | 1.21.10 | Target version. |
| Mappings | Mojang official mappings for 1.21.10 | Selected through `loom.officialMojangMappings()`; no separate mapping artifact version. |
| Java | 21 | Toolchain and `--release` are both set to 21. |
| Fabric Loader | 0.19.3 | Published stable loader. |
| Fabric API | 0.138.4+1.21.10 | Published artifact explicitly targeting Minecraft 1.21.10. |
| MixinExtras Fabric/common | 0.5.4 | Fabric runtime dependency plus common annotation processor. |
| Mod Menu | 16.0.1 | Compile-only dependency for Charmony's retained Mod Menu entrypoint. |
| Night Config core/TOML | 3.9.0 | Retained for the existing Charmony configuration implementation. |
| toml4j | 0.7.2 | Retained because recovered source imports it. |
| Apache Commons Lang | 3.20.0 | Retained because recovered source imports it. |
| JSR-305 | 3.0.2 | Compile-only dependency for retained `javax.annotation.Nullable` declarations. |

No snapshot dependency is used.

Repositories are limited to Fabric Maven, Terraformers Maven (Mod Menu), Maven Central, and the Gradle Plugin Portal where appropriate. The build no longer resolves scripts or dependencies from `charmony.dev`.

## Build architecture

The root is now a Gradle multi-project build. `settings.gradle` declares the recovered Charmony API, Charmony core, and nine feature modules under `modules/`. Each repository is recorded as a Git submodule at the exact Phase 0 commit. This makes repository topology and revision selection reproducible while keeping the recovered repositories intact.

Two local scripts replace the missing shared build logic:

- `gradle/charmony-common.gradle` supplies repositories, published dependencies, Loom configuration, Java 21 configuration, resource placeholder expansion, access widener discovery, mixin refmap naming, and reproducible archives.
- `gradle/charmony-module.gradle` applies that common configuration to each recovered implementation project and declares the API/core project relationships.

The recovered per-repository build files are not edited or evaluated. `settings.gradle` assigns the repository-local module script as each subproject's build file. The root project depends on and nests the 11 module artifacts, preserving the original aggregate Charm distribution model and module identities.

The root build no longer relies on:

- `../../java/build.gradle`
- the `https://charmony.dev/files/main/build.gradle` fallback
- custom `embedMod` logic from that missing script
- unpublished `fabric-loom:1.11-SNAPSHOT`
- implicit sibling-repository dependency lookup

## Source and module layout

All recovered projects use `src/main/java` and `src/main/resources`. No separate test or client-only source directories were found. The build includes the following unchanged source sets:

| Gradle project | Recovered repository | Java files | Resource files |
|---|---|---:|---:|
| `:charmony-api` | CharmonyApi | 79 | 3 |
| `:charmony` | Charmony | 168 | 24 |
| `:charmony-azalea-wood` | CharmonyAzaleaWood | 9 | 188 |
| `:charmony-brew-and-stew` | CharmonyBrewAndStew | 45 | 64 |
| `:charmony-collection` | CharmonyCollection | 9 | 8 |
| `:charmony-decor` | CharmonyDecor | 13 | 6 |
| `:charmony-ebony-wood` | CharmonyEbonyWood | 9 | 203 |
| `:charmony-glint-colors` | CharmonyGlintColors | 28 | 52 |
| `:charmony-mooblooms` | CharmonyMooblooms | 20 | 28 |
| `:charmony-totem-of-preserving` | CharmonyTotemOfPreserving | 19 | 17 |
| `:charmony-tweaks` | CharmonyTweaks | 228 | 75 |
| **Total** | | **627** | **668** |

Existing package names, mod IDs, versions, entrypoints, resource trees, mixin configurations, access wideners, and module manifests remain in their recovered repositories. Resource expansion supplies the placeholders previously provided by the shared script. Minecraft 1.21.10 declares resource-pack format 69, which is used for the retained `pack.mcmeta` templates.

## Configuration and dependency-resolution validation

Command:

```text
gradlew.bat projects --console=plain
```

Result: **successful**. Loom 1.11.8 initialized for the root and every implementation project, and Gradle listed all 11 subprojects.

On this Windows host, the JDK trust store did not accept the Mojang certificate chain. Validation commands therefore used the standard Windows certificate store for that process:

```text
GRADLE_OPTS=-Djavax.net.ssl.trustStoreType=Windows-ROOT
```

This is a host trust-store workaround, not a repository setting; TLS verification was not disabled. With it, Minecraft, mappings, Loader, Fabric API, Loom, and all other dependencies resolved successfully.

## Build and first compilation result

Command:

```text
gradlew.bat build --continue --console=plain
```

Result: **failed after reaching Java compilation**, as expected for the untouched 1.21.6 source baseline compiled against 1.21.10.

The first blocking Java source set is `:charmony-api:compileJava`. It produces **6 unique Java compiler errors** (Gradle repeats the same diagnostics in its failure summary):

- 3 uses in `GrindstoneEvents` and 3 uses in `SmithingTableEvents` access `Level.isClientSide` as a field. In 1.21.10 it is private and must be migrated to the current accessor/behavior.

The build also reports **2 access-widener validation errors**:

- Charmony references the removed or renamed `ParticleEngine$SpriteParticleRegistration` class.
- Charmony Tweaks references the removed or changed `Particle.setAlpha(float)` method.

Thus the first build exposes **8 unique immediate migration diagnostics: 6 Java compilation errors and 2 access-widener errors**. Because all core and feature projects depend on `:charmony-api`, Gradle correctly skips their Java compilation after the API compilation failure. Their complete Java error counts cannot be measured until the API is migrated; no source was altered to bypass that dependency boundary.

An earlier diagnostic pass also confirmed that JSR-305 had been supplied implicitly by the vanished build infrastructure. Adding its published compile-only artifact removed 5 missing-package and 10 missing-symbol diagnostics, allowing compilation to reach the actual Minecraft API changes without modifying source.

## Blockers and next work

- Migrate the six API calls from direct `Level.isClientSide` field access to the correct 1.21.10 API.
- Update the two stale access-widener targets after checking their original behavioral intent and current particle APIs.
- Re-run the build to expose the Charmony core compilation set, then migrate core registries and initialization incrementally.
- Continue module-by-module only after API/core compile boundaries are restored; downstream Java error totals are not yet known.
- The aggregate jar cannot be produced until the dependent implementation projects compile.

No recovered source or resource file was deleted, stubbed, commented out, or changed in this phase.
