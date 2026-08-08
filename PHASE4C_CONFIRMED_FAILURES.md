# Phase 4C: Confirmed Gameplay Failure Repairs

## Scope and outcome

This phase repaired only the three failures confirmed by Phase 4B:

1. Glint Colors smithing produced no output.
2. Totem Emergency Swap left a non-held totem in inventory.
3. Item Frame Hiding changed server state but its client payload/particle failed.

All three now pass focused Minecraft 1.21.10 runtime tests. No blocked Phase 4B feature was changed or tested as part of this work.

## Glint Colors smithing

### Original behavior

The recovered 1.21.6 workflow uses the normal smithing table with:

- template: `charmony:glint_color_template`;
- base: an enchanted item or enchanted book (or a configured allowed unenchanted item);
- addition: an item in `charmony:colored_dyes`;
- result: a copy of the base item with the persistent `charmony:glint_color` data component.

The existing smithing-transform JSON supplies vanilla recipe/property-set discovery, while Charmony's `SmithingTableEvents.CALCULATE_OUTPUT` constructs the component-bearing result. The integrated server loaded the recipe and accepted the template, Sharpness sword, and red dye into the correct property-set slots.

### Root cause

`SmithingMenuMixin` created the correct event instance at menu construction, but later callbacks discarded that menu-local reference and looked it up through the static player-UUID instance maps. Under the 1.21.10 client/server menu lifecycle, the lookup could return a stale menu instance. Targeted diagnostics reproduced the bug: the visible active menu contained all three inputs while the event instance reported three air stacks. No custom output could therefore be calculated.

The null-instance path in the wrapped `createResult` also returned without invoking vanilla behavior, which could suppress unrelated smithing results if an instance was unavailable.

### Fix

`SmithingMenuMixin` now retains the `SmithingTableInstance` created for that exact menu and uses it for calculate, placement, pickup, and take callbacks. If construction has not provided an instance, `createResult` delegates to vanilla rather than returning an empty result.

Old behavior/API:

```text
menu callback -> player UUID lookup -> possibly stale SmithingTableInstance
```

New 1.21.10 behavior:

```text
menu callback -> instance captured by that SmithingMenu at construction
```

### Runtime validation: PASS

1. Opened the existing development world and a normal smithing table.
2. Inserted the Glint Color Smithing Template, a Sharpness I diamond sword, and red dye through normal inventory interactions.
3. Verified that the output slot produced the copied diamond sword.
4. Took the result normally.
5. Queried its data and verified both `minecraft:enchantments={minecraft:sharpness:1}` and `charmony:glint_color={color:"red"}`.
6. Saved, exited, reloaded the same world, and verified the same component and color remained on the same sword.

The workflow was not replaced with another recipe, and the existing recipe/tag/component design remains intact.

## Totem Emergency Swap

### Original behavior

Pressing Z with a Totem of Undying in non-held player inventory performs the equivalent of three inventory pickup clicks: source slot, offhand slot, source slot. If either hand already contains a totem, the feature does nothing. It must neither duplicate nor delete the item.

### Root cause

The recovered path opened a hidden `InventoryScreen` and drove `InventoryScreen.slotClicked` using `Slot.getContainerSlot()`. In 1.21.10, the screen/menu setup is not a reliable synchronous transport for this off-screen operation, and click packets require menu slot indices rather than the backing inventory's container indices. The attempted clicks therefore left the stack in its original inventory slot.

### Fix

The feature now operates on the player's always-present `inventoryMenu` and sends the same three pickup actions through Minecraft's supported `MultiPlayerGameMode.handleInventoryMouseClick` path. It uses each `Slot.index` and the actual menu container ID. The existing explicit main-hand/offhand precondition remains unchanged.

Old behavior/API:

```text
temporary hidden InventoryScreen + container slot numbers + screen helper
```

New 1.21.10 behavior:

```text
player.inventoryMenu + menu slot indices + game-mode inventory click transport
```

### Runtime validation: PASS

- Non-held case: placed one totem in inventory slot 9, pressed Z, and verified normal inventory was empty while the total matching-item count remained exactly one (the stack moved to offhand).
- Repeated-key case: pressed Z again and verified the total remained exactly one.
- Main-hand case: placed one totem in hotbar/main hand, pressed Z, and verified it remained in slot 0 with total count one.
- Offhand case: placed one totem directly in offhand, pressed Z, and verified normal inventory remained empty with total count one.

No stale stack, duplication, or deletion occurred.

## Item Frame Hiding payload and particle

### Original behavior

Using amethyst on a nonempty item frame makes the frame invisible on the server, sends an S2C notification to nearby players, and creates the custom purple amethyst particle on each client. Attacking the hidden frame reverses the state and sends the removal notification/particle.

### Root cause

The common feature correctly registered both S2C payload types/codecs through `PayloadTypeRegistry.playS2C()` and sent them through `ServerPlayNetworking`. The client feature, however, registered its particle provider and global receivers only when `Environment.usesCharmonyServer()` was already true.

In Fabric Networking API for 1.21.10, global play receivers must be registered during client initialization, before a play connection establishes and before Charmony's server-identification payload can set that environment flag. The conditional therefore skipped receiver registration, producing:

```text
Unknown custom packet payload: charmony:add_amethyst_to_item_frame
```

### Fix

The particle provider and the two `ClientPlayNetworking.registerGlobalReceiver` calls are now registered unconditionally during the enabled client feature's initialization. Server payload registration, payload types, codecs, send paths, packet contents, hidden-state logic, and handlers are unchanged.

### Runtime validation: PASS

1. Used amethyst on a nonempty item frame.
2. Verified server/entity data changed to `Invisible: 1b`.
3. Visually observed the custom purple particle at the frame.
4. Attacked and reapplied amethyst to exercise both removal and addition notification paths.
5. Verified no unknown-payload warning appeared in the clean final runtime logs.
6. Saved and reloaded the world; the frame remained `Invisible: 1b` after reconnect.

## Files changed

Charmony core:

- `modules/charmony/src/main/java/svenhjol/charmony/core/common/mixins/smithing_table/SmithingMenuMixin.java`

Tweaks:

- `modules/charmony-tweaks/src/main/java/svenhjol/charmony/tweaks/client/features/totem_emergency_swap/Handlers.java`
- `modules/charmony-tweaks/src/main/java/svenhjol/charmony/tweaks/client/features/item_frame_hiding/Registers.java`

Documentation:

- `PHASE4C_CONFIRMED_FAILURES.md`

No Glint Colors implementation/data file, payload definition, codec, common item-frame handler, resource, configuration, build file, or unrelated module file changed.

## Build validation

Module-specific build command:

```text
./gradlew :charmony:build :charmony-glint-colors:build :charmony-tweaks:build
```

Result: **BUILD SUCCESSFUL** (29 tasks; compilation, resources, remapping, access-widener validation, checks, and assembly completed).

Aggregate command:

```text
./gradlew build
```

Result: **BUILD SUCCESSFUL** (92 tasks).

## Remaining warnings and follow-up

No error or warning attributable to the three repaired features remained in the final logs. The known development-environment/cosmetic warnings remain unchanged:

- Mojang authentication TLS/PKIX failures;
- missing offline Services/chat public key;
- untranslated convention tags;
- the intentionally disabled Telemetry and Respawn Anchors features;
- the aggregate root's no-source classpath entry.

The command-summoned item-frame fixture emitted `Block-attached entity at invalid position: null` before attachment. This was caused by the command fixture, not Charm; the entity subsequently attached and the feature paths completed. A later manual placement test should avoid that fixture-only warning, but it does not block the confirmed packet/particle/state results.

The remaining Phase 4B blocked tests are intentionally deferred to later phases.
