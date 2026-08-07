# Phase 0: Charm 1.21.6 Repository and Dependency Baseline

## Status

- Audit date: 2026-08-07
- Port branch: `port/1.21.10`
- Baseline Minecraft version: **1.21.6**
- Baseline aggregate version: **Charm 8.8.23**
- Baseline source commit: `3c5a087ef5d4fbdebb636d46d74002b96e07b989`
- Existing source/build/resource files modified: none
- Original build result: **failed during plugin resolution**
- Phase 0 exit criteria: **not fully met**

All implementation repositories have been acquired locally at immutable commits in a temporary workspace. The original build is not yet reproducible because the shared Gradle convention source is missing, its server is offline, and the declared Loom snapshot is no longer published.

## Root baseline selection

The immutable pre-port source baseline is:

```text
Repository: https://github.com/svenhjol/Charm
Branch lineage: main
Commit: 3c5a087ef5d4fbdebb636d46d74002b96e07b989
Commit time: 2025-08-24T21:34:55Z
Subject: Update gradle for Loom 11
Charm version: 8.8.23
```

The current port branch adds only documentation commits on top of this commit. Commit `3c5a087` is therefore the exact original root revision, not an arbitrary date-based approximation.

## Repository topology

### What is not used

- No `.gitmodules` file exists.
- `git submodule status` returns no entries.
- No `includeBuild` declaration exists.
- No Gradle composite build exists.
- No Gradle multi-project `include(...)` declarations exist.
- No project dependencies such as `implementation project(...)` exist.
- No source repositories are initialized or linked by the root build.

### What is used

The root is a single-project aggregate build with two separate external mechanisms:

1. **Shared build convention selection**
   - Preferred local path: `../../java/build.gradle`.
   - From the expected checkout location `<workspace>/git/charm`, this resolves to `<workspace>/java/build.gradle`.
   - If that file is absent, Gradle applies `https://charmony.dev/files/main/build.gradle`.
   - This is a sibling-directory convention file or a downloaded Gradle script, not an included build, plugin project, or Git submodule.

2. **Published implementation artifacts**
   - The root calls a custom `embedMod(moduleName, shortId)` method supplied by the shared convention.
   - The nine feature modules are therefore downloaded/embedded dependencies declared through custom Gradle logic.
   - The exact Maven group, repository URL, configuration wiring, and artifact classifier are defined only in the missing convention.
   - `include_charmony=true`, `reference_api=true`, and `reference_toml=true` instruct that same convention to include/reference Charmony core, Charmony API, and the TOML configuration stack.
   - The implementation repositories are not consumed as sibling source projects by this root build.

### Root Gradle inputs

| File | Relevant baseline behavior |
| --- | --- |
| `settings.gradle` | Configures plugin repositories: Fabric Maven, Terraformers Maven, Maven Central, and Gradle Plugin Portal. It declares no projects or included builds. |
| `build.gradle` | Applies Fabric Loom `1.11-SNAPSHOT`, selects the sibling/downloaded convention, and declares nine `embedMod` calls. |
| `gradle.properties` | Sets Charm `8.8.23` and the flags `include_charmony=true`, `reference_api=true`, and `reference_toml=true`. |
| wrapper properties | Pins Gradle `8.14.2`. |
| `fabric.mod.json` | Uses placeholders for Loader, Java, Charmony, and Charmony API versions; the convention expands them. |
| `pack.mcmeta` | Uses a convention-expanded pack-format placeholder. |

No other Gradle scripts, version catalogs, dependency locks, Maven POMs, or repository declarations are tracked locally.

## Required repositories and exact source revisions

The commits below are the last commits on the historical `main` lineage at or before the root baseline, with the exception that the root revision itself is known directly from ancestry. No matching release tags exist, so the full commit hashes are the reproducible selectors.

All are temporarily available under:

```text
C:\Users\jbail\AppData\Local\Temp\charm-phase0-workspace\git\
```

They are detached at the recorded commits and are not linked into the current root build.

| Module | GitHub repository | Expected branch lineage | Exact commit | Version | Local availability | Relationship to Charm |
| --- | --- | --- | --- | ---: | --- | --- |
| Charm aggregate | https://github.com/svenhjol/Charm | `main` | `3c5a087ef5d4fbdebb636d46d74002b96e07b989` | 8.8.23 | Current checkout plus temporary pristine clone | Root aggregate |
| Charmony core | https://github.com/svenhjol/charmony | `main` | `63f5f86a1ce9a05c0d5e0073b8ebdbea1d05bccc` | 1.44.4 | Temporary exact checkout | Published dependency selected by `include_charmony` |
| Charmony API | https://github.com/svenhjol/charmony-api | `main` | `13cd87ab2034cae58c31fc751474f073d49a1a62` | 1.26.15 | Temporary exact checkout | Published compile/runtime dependency selected by convention flags |
| Azalea Wood | https://github.com/svenhjol/charmony-azalea-wood | `main` | `2c921a061995f675170204f9e7fadc5a93cb4ee8` | 1.2.0 | Temporary exact checkout | Embedded artifact: `embedMod "charmony-azalea-wood", "azalea_wood"` |
| Brew and Stew | https://github.com/svenhjol/charmony-brew-and-stew | `main` | `da22f8df975a220c1b1255cbe36195f8ec5bb4d8` | 1.6.3 | Temporary exact checkout | Embedded artifact: `embedMod "charmony-brew-and-stew", "brew_and_stew"` |
| Collection | https://github.com/svenhjol/charmony-collection | `main` | `bde1e4fa2712751a30ee8a2bbe980e4323196163` | 1.7.0 | Temporary exact checkout | Embedded artifact: `embedMod "charmony-collection", "collection"` |
| Decor | https://github.com/svenhjol/charmony-decor | historical `main`; commit also remains in `next` | `147b85a58ae95084cce9a29c38f6d39ff0ab418b` | 1.7.0 | Temporary exact checkout | Embedded artifact: `embedMod "charmony-decor", "decor"` |
| Ebony Wood | https://github.com/svenhjol/charmony-ebony-wood | `main` | `09699c57ec8772e2ff3e9031be072b007396cfa0` | 1.2.0 | Temporary exact checkout | Embedded artifact: `embedMod "charmony-ebony-wood", "ebony_wood"` |
| Glint Colors | https://github.com/svenhjol/charmony-glint-colors | `main` | `870822bee76d1a100b3715c565dd53c926584ebf` | 1.8.4 | Temporary exact checkout | Embedded artifact: `embedMod "charmony-glint-colors", "glint_colors"` |
| Mooblooms | https://github.com/svenhjol/charmony-mooblooms | `main` | `543723cf27f3a04b72871a283db7ccba1ea354d3` | 1.7.0 | Temporary exact checkout | Embedded artifact: `embedMod "charmony-mooblooms", "mooblooms"` |
| Totem of Preserving | https://github.com/svenhjol/charmony-totem-of-preserving | `main` | `00ab29d44a307874e175f88454c554153f230bb1` | 1.8.1 | Temporary exact checkout | Embedded artifact: `embedMod "charmony-totem-of-preserving", "totem_of_preserving"` |
| Tweaks | https://github.com/svenhjol/charmony-tweaks | `main` | `5db3495e1c540689040cad56a9967d0800d798e9` | 1.13.6 | Temporary exact checkout | Embedded artifact: `embedMod "charmony-tweaks", "tweaks"` |

### Revision-selection method

- The root commit is its last source commit before the port documentation.
- Charmony core commit `63f5f86` is the Loom 11 update committed 71 minutes before the aggregate update.
- Glint Colors commit `870822b` is its Loom 11 update committed two minutes after Charmony core and before the aggregate update.
- Other modules did not change between their listed commits and the aggregate commit.
- Later mass Loom commits on 2025-08-25 were deliberately excluded because they postdate the root commit.
- Version numbers were read from `gradle.properties` at each exact commit.
- Full hashes, not moving branches, are the baseline selectors.

## Shared convention investigation

Every exact repository above contains the same convention lookup:

```groovy
def javaDir = "${projectDir}/../../java"
def buildFile = new File("${javaDir}/build.gradle")
def buildUrl = "https://charmony.dev/files/main/build.gradle"

apply from: buildFile.exists() ? buildFile : buildUrl
```

The shared convention is unavailable for two independent reasons:

1. **No local sibling convention**
   - `<workspace>/java/build.gradle` is absent.
   - No Git repository URL, submodule declaration, commit hash, or artifact coordinate for that directory exists in any inspected repository.
   - A search of the local Git workspaces and the upstream author's public repositories/gists found references to the script but no source copy.

2. **The fallback service is offline**
   - `charmony.dev` resolves to `209.38.236.173`.
   - ICMP ping succeeds.
   - TCP connections to ports 443 and 80 are refused.
   - Requests fail before HTTP or TLS negotiation; this is not a certificate, Gradle, or repository-sandbox error.
   - No 2025 Internet Archive snapshot of the script was found.

The convention probably defined all normal project metadata, Minecraft/Loader/API/mapping dependencies, Maven repositories, resource expansion, artifact publication, `embedMod`, and Charmony/TOML dependency flags. Recreating those behaviors without the original file would be new build implementation, not Phase 0 recovery, so it was not attempted.

## Original dependency baseline

### Exactly recovered

| Dependency/tool | Exact baseline |
| --- | --- |
| Minecraft | 1.21.6 |
| Charm | 8.8.23 |
| Charmony core | 1.44.4 at `63f5f86a1ce9a05c0d5e0073b8ebdbea1d05bccc` |
| Charmony API | 1.26.15 at `13cd87ab2034cae58c31fc751474f073d49a1a62` |
| Nine feature modules | Versions and commits in the table above |
| Fabric Loom declaration | `1.11-SNAPSHOT` |
| Gradle wrapper | 8.14.2 |
| Mapping namespace | Official Mojang names |
| Java runtime used for reconstruction | Oracle Java 21.0.2 |

### Not exactly recoverable from available inputs

| Dependency/detail | Status |
| --- | --- |
| Fabric Loader version | Placeholder supplied by missing convention; no resolved manifest or lock file available. |
| Fabric API version | Placeholder/dependency supplied by missing convention; no resolved dependency report or lock file available. |
| Exact official Mojang mapping coordinate | Convention missing; source namespace confirms mapping family but not the resolved coordinate expression. |
| Java source/target and expanded manifest minimum | Convention missing; Java 21 is strongly indicated but the original expanded value was not recovered. |
| Charmony artifact Maven group/repository/classifier/checksum | Defined by missing convention. Source versions are exact; published binary identity is not. |
| NightConfig versions | Defined by missing convention through `include_toml`/`reference_toml`. |
| toml4j version | Defined by missing convention. |
| MixinExtras version | Defined by missing convention or Loom transitive configuration. |
| Mod Menu version | Defined by missing convention; integration source is present. |
| Fabric module transitive versions | Would be fixed by the missing Fabric API dependency. |

Previously inferred values such as Loader `0.16.14` and Fabric API `0.127.1+1.21.6` remain plausible release-window candidates, but they are **not** promoted to exact baseline facts here.

## Development-workspace reconstruction

A temporary workspace was created with the expected directory shape:

```text
C:\Users\jbail\AppData\Local\Temp\charm-phase0-workspace\
|-- git\
|   |-- charm\
|   |-- charmony\
|   |-- charmony-api\
|   |-- charmony-azalea-wood\
|   |-- charmony-brew-and-stew\
|   |-- charmony-collection\
|   |-- charmony-decor\
|   |-- charmony-ebony-wood\
|   |-- charmony-glint-colors\
|   |-- charmony-mooblooms\
|   |-- charmony-totem-of-preserving\
|   `-- charmony-tweaks\
`-- java\
    `-- build.gradle  (missing)
```

The feature implementation source is fully present at exact commits. The complete original development workspace is not present because `java/build.gradle` and any companion convention files were never identified.

The source clones are reference checkouts only. The root build does not automatically consume them, and no settings/build files were altered to make it do so.

## Original build attempt

Command, run from a pristine temporary clone at root commit `3c5a087`:

```powershell
.\gradlew.bat build --stacktrace
```

Environment:

- Gradle wrapper 8.14.2
- Oracle Java 21.0.2
- network access allowed to all declared repositories
- no local `../../java/build.gradle`
- no source or build-file modifications

Result:

```text
BUILD FAILED in 13s
Plugin [id: 'fabric-loom', version: '1.11-SNAPSHOT'] was not found.
Searched:
- https://maven.fabricmc.net
- https://maven.terraformersmc.com/
- Maven Central
- Gradle Plugin Portal
```

The failure occurs at `build.gradle` line 2, before the fallback convention script is applied. The original build therefore does **not** build today.

This failure has two diagnosed layers:

1. `fabric-loom:1.11-SNAPSHOT` is no longer resolvable from the declared plugin repositories.
2. Even if that snapshot were restored from cache/artifact storage, configuration would subsequently require either the missing sibling convention or the offline Charmony URL.

No attempt was made to substitute Loom `1.11.8`, restore a guessed convention, change repository URLs, or update any dependency, because each would change the untouched 1.21.6 baseline.

## Local availability conclusion

- Root aggregate source: available locally.
- Charmony core/API and all nine module sources: available locally in the temporary reconstruction at exact commits.
- Original shared build convention: missing.
- Exact resolved published dependency graph: missing.
- Complete reproducible 1.21.6 development workspace: **not yet available**.

## Remaining blockers

1. Recover `java/build.gradle` and any files it imports from the original developer workspace, backup, build machine, or artifact archive.
2. Recover the exact `fabric-loom:1.11-SNAPSHOT` plugin artifact used in August 2025, or obtain an original Gradle cache/build archive.
3. Recover a published Charm 8.8.23 JAR or resolved `fabric.mod.json` to confirm Loader, API, Java, Charmony, and API minimums.
4. Recover an original Gradle dependency report or cache to identify artifact repositories, Maven coordinates, checksums, Fabric API, NightConfig, toml4j, MixinExtras, and Mod Menu versions.
5. Only after recovery, rerun the untouched build and record dependency checksums/output artifacts.

Phase 1 migration must not begin by guessing these values. If original artifacts cannot be recovered, any replacement convention and stable Loom substitution must be explicitly approved and documented as a reproducibility reconstruction before version migration.
