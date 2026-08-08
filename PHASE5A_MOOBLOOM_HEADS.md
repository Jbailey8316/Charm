# Phase 5A: Moobloom Head Drops

## Scope and baseline finding

This phase adds Moobloom heads without changing Moobloom spawning, AI, breeding, pollination, shearing, flower planting, rendering, or normal entity loot.

The recovered Charm/Charmony baseline does not contain Cow or Mooshroom head providers, head loot tables, or head-drop percentages. The Tweaks `MobDrops` feature is the existing Vanilla Tweaks-derived extra-drop system: it receives `EntityKilledDropCallback`, iterates registered `DropProvider` implementations, and spawns any returned extra stack independently of the entity's normal loot. Phase 5A extends that system rather than adding death logic to the Moobloom entity.

## Implementation

`MoobloomHeadDrops` is registered in the existing `MobDrops.Registers.drops` list. Consequently, it is active only when the existing common `MobDrops` feature is enabled; no standalone configuration option was added.

The provider:

- recognizes the registered `charmony:moobloom` entity type without introducing a build dependency from Tweaks to the Mooblooms module;
- requires `DamageSource.getEntity()` to be a `ServerPlayer`, matching the player-kill eligibility of Vanilla Tweaks More Mob Heads;
- calculates the drop probability as `0.01 + EnchantmentsHelper.lootingLevel(source) * 0.001`;
- adds a vanilla `minecraft:player_head` as an extra drop, leaving the Moobloom's normal drops untouched;
- sets a stable resolved `minecraft:profile` component with a Moobloom texture hosted by Mojang's texture service;
- sets the display name to `Moobloom Head`.

The probability is therefore:

| Looting level | Drop chance |
| ---: | ---: |
| 0 | 1.0% |
| 1 | 1.1% |
| 2 | 1.2% |
| 3 | 1.3% |

This matches the requested Vanilla Tweaks Cow/normal Mooshroom rate of one percent plus 0.1 percentage point per Looting level.

## Files changed

Tweaks:

- `modules/charmony-tweaks/src/main/java/svenhjol/charmony/tweaks/common/features/mob_drops/Registers.java`
- `modules/charmony-tweaks/src/main/java/svenhjol/charmony/tweaks/common/features/mob_drops/mobs/MoobloomHeadDrops.java`

Documentation:

- `PHASE5A_MOOBLOOM_HEADS.md`

## Validation

`./gradlew :charmony-tweaks:compileJava`

- Result: **BUILD SUCCESSFUL**.

For deterministic runtime validation without hundreds of kills, set a debugger breakpoint immediately before the random comparison in `MoobloomHeadDrops.dropWhenKilled`, evaluate the calculated chance for Looting levels 0 through 3, and force the sampled random value once below and once equal to the boundary. Verify that only the below-boundary case produces one named, textured player head, while the normal Moobloom loot is still present. Repeat once with a non-player damage source to verify no head is produced. This exercises the production code without adding a test-only gameplay path or configuration.

Runtime validation remains required for the displayed head texture, placed-head appearance, player-kill restriction, Looting behavior, and coexistence with every Moobloom variant's normal drops.

## Build results

`./gradlew :charmony-tweaks:build`

- Result: **BUILD SUCCESSFUL** (18 tasks; compilation, resources, remapping, access-widener validation, checks, and assembly completed).

`./gradlew build`

- Result: **BUILD SUCCESSFUL** (92 tasks).

## Phase 5A.1: Variant-specific local heads

Phase 5A.1 supersedes the generic player-profile head described above. The external `minecraft:profile`, remote texture URL, and generic display name were removed. The final implementation is fully local and covers all fourteen Charm Moobloom variants.

### Architecture

The Mooblooms module registers fourteen block/item pairs from `MoobloomType.values()`. Every pair uses the shared `MoobloomHeadBlock` implementation and a shared cow-head JSON model. Each small variant model binds that parent model to its existing Charm texture at `charmony:textures/entity/moobloom/<type>.png`; no PNG was copied or changed.

Using a distinct registered block/item for each variant makes the registry ID the persistent variant state. It requires no block entity or custom component: inventory stacks, dropped items, placed blocks, save/reload, and block loot all retain the exact variant naturally. `MoobloomHeadBlock` stores horizontal orientation in its normal block state and uses a cow-head-sized selection/collision shape. The shared model renders an eight-by-eight-by-six cow head plus two horns, with UVs taken from the head and horn regions of the existing 64-by-64 entity texture.

The fourteen items are inserted after the vanilla player head in the Functional Blocks creative tab. Their localized block-item names are:

| Stable type | Registry ID | Display name | Local texture |
| --- | --- | --- | --- |
| `allium` | `charmony:allium_moobloom_head` | Allium Moobloom Head | `charmony:entity/moobloom/allium` |
| `azure_bluet` | `charmony:azure_bluet_moobloom_head` | Azure Bluet Moobloom Head | `charmony:entity/moobloom/azure_bluet` |
| `blue_orchid` | `charmony:blue_orchid_moobloom_head` | Blue Orchid Moobloom Head | `charmony:entity/moobloom/blue_orchid` |
| `cherry_blossom` | `charmony:cherry_blossom_moobloom_head` | Cherry Blossom Moobloom Head | `charmony:entity/moobloom/cherry_blossom` |
| `cornflower` | `charmony:cornflower_moobloom_head` | Cornflower Moobloom Head | `charmony:entity/moobloom/cornflower` |
| `dandelion` | `charmony:dandelion_moobloom_head` | Dandelion Moobloom Head | `charmony:entity/moobloom/dandelion` |
| `lily_of_the_valley` | `charmony:lily_of_the_valley_moobloom_head` | Lily of the Valley Moobloom Head | `charmony:entity/moobloom/lily_of_the_valley` |
| `orange_tulip` | `charmony:orange_tulip_moobloom_head` | Orange Tulip Moobloom Head | `charmony:entity/moobloom/orange_tulip` |
| `oxeye_daisy` | `charmony:oxeye_daisy_moobloom_head` | Oxeye Daisy Moobloom Head | `charmony:entity/moobloom/oxeye_daisy` |
| `pink_tulip` | `charmony:pink_tulip_moobloom_head` | Pink Tulip Moobloom Head | `charmony:entity/moobloom/pink_tulip` |
| `poppy` | `charmony:poppy_moobloom_head` | Poppy Moobloom Head | `charmony:entity/moobloom/poppy` |
| `red_tulip` | `charmony:red_tulip_moobloom_head` | Red Tulip Moobloom Head | `charmony:entity/moobloom/red_tulip` |
| `sunflower` | `charmony:sunflower_moobloom_head` | Sunflower Moobloom Head | `charmony:entity/moobloom/sunflower` |
| `white_tulip` | `charmony:white_tulip_moobloom_head` | White Tulip Moobloom Head | `charmony:entity/moobloom/white_tulip` |

Tweaks now declares a one-way compile/runtime dependency on the Mooblooms module so `MoobloomHeadDrops` can use the supported `Moobloom.getMoobloomType()` accessor directly. After the single successful probability roll, the provider resolves `<type>_moobloom_head` from the item registry. This mapping is derived from the same stable type name used for entity synchronization and persistence, so every known enum value is covered automatically. No reflection, NBT inspection, duplicated death handler, or silent fallback is used.

### Preserved drop rules

- The existing Tweaks `MobDrops` feature toggle remains the sole drop toggle.
- `DamageSource.getEntity()` must be a `ServerPlayer`.
- The probability is rolled exactly once per eligible death.
- The shared formula remains `0.01 + Looting level * 0.001` (1.0%, 1.1%, 1.2%, and 1.3% at Looting 0 through III).
- The head remains an additional callback drop; normal Moobloom loot and entity behavior are untouched.

### Phase 5A.1 validation

Mechanical coverage validation confirmed exactly fourteen enum identifiers, textures, registrations/translations, blockstates, child block models, item models, item definitions, and loot tables. Every JSON resource parsed successfully, every child model referenced its matching local entity texture, and every loot table returned its matching registered item. Hashing the UV source region used by the head model also produced fourteen distinct results, confirming that the rendered head region is not silently shared between variants.

Commands run:

```text
./gradlew :charmony-mooblooms:compileJava :charmony-tweaks:compileJava
./gradlew :charmony-mooblooms:build :charmony-tweaks:build
./gradlew build
```

Results: **BUILD SUCCESSFUL** for focused compilation, both affected module builds (26 tasks), and the aggregate build (92 tasks).

### Practical runtime checklist

No permanent debug code is required. Use the creative search tab or `/give @s charmony:<type>_moobloom_head` for each registry ID in the table:

1. Inspect all fourteen names and inventory icons side-by-side, confirming each texture is distinct and matches the corresponding Moobloom.
2. Hold each head in both first- and third-person views, then drop each stack and inspect its world-item rendering.
3. Place each head while facing north, east, south, and west; verify the face points toward the placer and the selection shape follows its orientation.
4. Break each placed head in Survival and verify it returns the same registry ID and display name.
5. Place one of every variant, save and exit, reload the world, and confirm texture, orientation, and break drop persist.
6. Spawn a Moobloom, set its synchronized `Type` value to each stable identifier using the existing entity-data test fixture/debugger, and compare the live entity with its corresponding head.
7. For deterministic drop-path testing, break at the random comparison in `MoobloomHeadDrops.dropWhenKilled` and force the sampled value below the calculated boundary. Kill each typed Moobloom as a player and verify exactly one matching head is added alongside normal loot. Force the value equal to the boundary to verify no head.
8. Repeat with Looting 0 through III and inspect the calculated boundaries `0.010`, `0.011`, `0.012`, and `0.013`; repeat once with non-player damage and verify the callback returns no head.

Visual runtime validation of all inventory/held/dropped/placed transforms and the complete fourteen-variant death matrix remains required.

### Phase 5A.1 files

- `gradle/charmony-module.gradle`
- Mooblooms `Registers.java` on common and client sides
- new Mooblooms `MoobloomHeadBlock.java`
- Tweaks `MoobloomHeadDrops.java`
- Mooblooms `en_us.json`
- one shared block model and fourteen variant block models
- fourteen blockstates, item models, item definitions, and block loot tables
- `PHASE5A_MOOBLOOM_HEADS.md`
