# Phase 7C — Suspicious Falling Persistence

## Result

The 1.21.10 vanilla lifecycle has a real payload-transfer gap. `BrushableBlock`
creates a `FallingBlockEntity` with `FallingBlockEntity.fall(level, pos, state)`
but does not copy the source `BrushableBlockEntity`. `FallingBlockEntity` already
has a `blockData` field, serializes it as `TileEntityData`, and on landing merges
that data into the newly created block entity. The missing step was therefore
the source-side copy, not a need for a second storage system.

A narrowly scoped redirect was added to the `BrushableBlock.tick` fall call. It
serializes the existing BrushableBlockEntity with the vanilla
`saveWithoutMetadata(level.registryAccess())` path, invokes the normal fall
method, and assigns the resulting `CompoundTag` to `FallingBlockEntity.blockData`.
This carries both Charm-created explicit items and normal archaeology loot-table
state through the existing vanilla landing path.

The P0 release blocker remains **OPEN** because this environment cannot perform
the required interactive fall/land/brush test. Static bytecode evidence and
startup/build validation support the fix, but they are not a gameplay proof.

## Vanilla 1.21.10 lifecycle trace

1. `BrushableBlock.tick` checks the block below and calls
   `FallingBlockEntity.fall(...)` when the block is unsupported.
2. `FallingBlockEntity.fall` removes the source block and creates the entity.
   Vanilla did not read the source block entity at this point.
3. `FallingBlockEntity.blockData` is the established vanilla payload field.
   Its save/load methods use the `TileEntityData` codec.
4. On normal landing, `FallingBlockEntity` places the block, invokes the
   `Fallable.onLand` hook, and, when `blockData` is present and the block has a
   block entity, merges the payload into the newly placed entity.
5. `BrushableBlockEntity` serializes its `item`, loot-table state, seed, hit
   direction, and brush progress through the normal ValueInput/ValueOutput path.

There is no separate Sand-versus-Gravel lifecycle. Both use the same
`BrushableBlock` implementation and therefore receive the same payload fix.

## Implementation

Changed files:

- `modules/charmony-tweaks/src/main/java/svenhjol/charmony/tweaks/common/mixins/suspicious_block_creating/BrushableBlockMixin.java`
- `modules/charmony-tweaks/src/main/resources/charmony-tweaks.common.mixins.json`

The mixin is limited to `BrushableBlock.tick` and does not alter ordinary
falling sand/gravel, `FallingBlockEntity` globally, or brushable loot logic.
Naturally generated suspicious blocks retain their ordinary loot table because
their complete vanilla block-entity payload is copied unchanged. Charm-created
cache blocks retain the explicit ItemStack and their cleared loot table.

No access widener, custom entity, custom serialization format, or second item
storage system was added.

## Item integrity and duplication invariants

The copied payload is the vanilla block-entity serialization, so it retains the
complete ItemStack representation: item type, count, damage, enchantments,
custom name, lore, and modern component/modded component data supported by
Minecraft's codecs. The source ItemEntity remains removed exactly once by the
existing Charm piston conversion path. Falling transfers a copy of stored block
data; it does not create an ItemEntity, so pre-fall and post-fall states do not
represent two independently recoverable source stacks.

The expected deterministic checks are:

* 32-item stack before fall equals 32-item stack after landing and brushing;
* component-rich pickaxe fields are unchanged;
* repeated fall/save/reload/brush returns one stack once;
* breaking or destroying the falling entity follows vanilla destruction rules;
* naturally generated suspicious sand/gravel still resolves its archaeology
  loot table rather than Charm's explicit cache path.

These checks require an actual server/world interaction and are therefore
recorded as UNTESTED below and retained in the release backlog.

## Validation

| Check | Result | Evidence/notes |
|---|---|---|
| Charmony Tweaks compile | PASS | `./gradlew :charmony-tweaks:compileJava` |
| Charmony Tweaks build | PASS | `./gradlew :charmony-tweaks:build` |
| Aggregate build | PASS | `./gradlew build` |
| Mixin startup/application | PASS | `runClient` reached resource initialization; no new mixin-apply error was logged |
| Suspicious Sand normal fall + brush | UNTESTED | Gameplay automation unavailable |
| Suspicious Gravel normal fall + brush | UNTESTED | Gameplay automation unavailable |
| Component-rich stack | UNTESTED | Requires interactive item setup and brushing |
| Stack count | UNTESTED | Requires interactive item setup |
| Fall → save/reload → brush | UNTESTED | Requires interactive world save/reload |
| Multiple falling transitions | UNTESTED | Requires interactive support removal |
| Falling destruction/void/invalid landing | UNTESTED | Destruction semantics remain vanilla |
| Natural archaeology regression | STATIC PASS / RUNTIME UNTESTED | Payload copies loot-table state; gameplay regression still required |
| Feature toggle OFF | STATIC PASS / RUNTIME UNTESTED | Mixin only carries existing brushable payload; Charm conversion remains toggle-gated |

The client log still contains pre-existing development-environment Realms TLS
errors and unrelated Moobloom texture warnings; no new suspicious-block,
FallingBlockEntity, or mixin exception was observed.

## Release status

**P0 blocker: OPEN — Suspicious Block Falling Item Persistence.**

Close it only after both manually created Suspicious Sand and Suspicious Gravel
have been allowed to fall, land, save/reload, and brush with exact component-rich
ItemStack verification. The required cases remain in
`RELEASE_VALIDATION_BACKLOG.md`.

