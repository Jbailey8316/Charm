# Charm 1.21.10 Port Instructions

## Goal

Port this repository to Minecraft 1.21.10 using Fabric while preserving the original Charm mod's features and behavior as closely as practical.

Do not redesign, simplify, replace, or remove features merely to make the project compile.

## Source and target

- Repository: Charm
- Port branch: port/1.21.10
- Target Minecraft version: 1.21.10
- Mod loader: Fabric
- Java: 21
- Preserve the original source as the behavioral and content reference.

## General workflow

Work incrementally.

Do not attempt the entire port in one enormous change set.

Before changing code:
1. inspect the existing project structure
2. identify the original Minecraft/Fabric versions
3. inspect Gradle, Fabric Loader, Fabric API, mappings, Java, and dependency versions
4. create a written port plan
5. identify major subsystems and likely migration risks

Port the mod in functional phases.

Suggested order:
1. Build system and project bootstrap
2. Core registries and initialization
3. Items and materials
4. Blocks and construction families
5. Recipes, tags, loot tables, advancements, and data
6. World generation and biome features
7. Entities, AI, spawning, models, renderers, and animations
8. Block entities, menus, screens, and networking
9. Structures and processors
10. Client rendering and particles
11. Sounds, textures, models, translations, and other assets
12. Configuration and compatibility integrations
13. Full parity audit
14. Runtime testing and cleanup

## Build requirements

Run the appropriate Gradle build after every meaningful phase.

The expected validation command is:

./gradlew build

On Windows, use the repository's Gradle wrapper appropriately if required.

Do not proceed to the next major phase with compilation errors unless the reason is documented and the user explicitly approves it.

Fix warnings when they indicate incorrect migration or broken behavior.

## Git requirements

Work only on the current port branch unless instructed otherwise.

Make small, logical commits.

Commit each completed functional phase separately.

Before every commit:
- inspect git status
- inspect the staged diff
- verify unrelated files were not changed
- run the relevant build/tests

Do not rewrite existing Git history.

Do not force-push.

Do not merge branches unless explicitly instructed.

Do not push to GitHub unless explicitly instructed.

## Content preservation

Maintain feature parity with the original mod.

Do not remove content because an API changed.

When an old API no longer exists:
1. determine the intended original behavior
2. identify the correct modern 1.21.10 Fabric/Minecraft equivalent
3. implement that equivalent
4. document any unavoidable behavior difference

Preserve:
- blocks
- items
- entities
- block entities
- recipes
- tags
- loot tables
- advancements
- structures
- world generation
- biome modifications
- sounds
- textures
- models
- particles
- translations
- configuration
- commands
- networking
- integrations
- data generators
- resource/data pack content

## Binary assets

Treat binary assets as first-class repository content.

Preserve PNG, OGG, NBT, and other binary files.

Do not replace existing assets with placeholders.

Do not delete binary assets merely because they are temporarily unused.

## Parity tracking

Create and maintain a porting/parity document.

Track at minimum:
- Java source files
- registered blocks
- registered items
- entities
- block entities
- recipes
- tags
- loot tables
- advancements
- structures
- worldgen features
- sounds
- textures
- models
- translations
- configuration features

The parity document should distinguish:
- original total
- currently ported
- remaining
- intentionally changed
- blocked

Do not claim the port is complete based only on successful compilation.

## Testing

Compilation is necessary but not sufficient.

When practical, test:
- game startup
- single-player world creation
- registry loading
- recipes
- placed blocks
- item behavior
- entities
- spawning
- world generation
- structures
- rendering
- sounds
- persistence across save/reload

Record known runtime issues.

## Decision making

When uncertain about an original feature, inspect the original implementation rather than guessing.

Prefer modern Minecraft/Fabric APIs over hacks or compatibility shims when an official equivalent exists.

Avoid broad mechanical rewrites that obscure behavior.

Do not make cosmetic refactors unrelated to the port.

## Communication

At the end of each task, report:
- files changed
- major features migrated
- build/test results
- known problems
- parity progress
- commit hash if a commit was created
- recommended next phase

Do not report work as complete when meaningful original functionality is still absent.
