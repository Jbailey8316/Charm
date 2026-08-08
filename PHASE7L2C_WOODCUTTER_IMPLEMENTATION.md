# Phase 7L.2C - Woodcutter Implementation

## Architecture

The Woodcutter is a native Charm block extending the vanilla Stonecutter block
for orientation and workstation interaction. It uses a dedicated `MenuType`,
`WoodcutterMenu`, transient one-slot input, and derived result slot. There is
no block entity, persistent machine inventory, hopper automation, or
comparator support.

## Payloads and authority

`woodcutter_recipes` (S2C) carries only menu ID, generation, and stable IDs for
the current matching `charm:woodcutting` recipes. `woodcutter_select` (C2S)
carries menu ID, generation, and one requested recipe ID. The server resolves
the current `RecipeManager` and never accepts a client result stack.

Selection validation checks the open menu/container ID, block validity,
generation, current recipe type, current input, and membership in the current
matching recipe set. Input changes and recipe-manager changes regenerate the
set and increment generation; stale requests are ignored without mutation.

## Transactions

The result is derived from input plus the selected current recipe. The result
slot's single `onTake` path revalidates state, removes exactly one input, and
refreshes the recipe/result state. Shift-click first checks complete player
inventory capacity, performs the vanilla menu transfer, then invokes the same
`onTake`; it cannot create an output without its corresponding consumption.
Input quick-move accepts only stacks that have a current Woodcutting recipe.
Closing/disconnecting clears the transient input through vanilla container
cleanup; the derived result is never separately returned. `stillValid` checks
the Woodcutter block through `ContainerLevelAccess`, so removal invalidates
selection and result taking.

| Operation | Source | Destination | Consumption | Authority |
|---|---|---|---|---|
| Manual input | player slot | transient input | none | server menu |
| Quick-move input | player slot | transient input | none | server menu |
| Result take | derived result | player cursor/inventory | one input in `onTake` | server menu |
| Quick-move result | derived result | player inventory | same `onTake` | server menu |
| Close/disconnect | transient input | player/drop cleanup | none | server container |
| Reload/block removal | derived state | cleared | none | server menu |

## Isolation and validation

No vanilla Stonecutter packet/list, menu, recipe type, or Kiln firing path is
modified. JSON resources parse, `:compileJava` passes, aggregate build passes,
and client startup registered both features without Woodcutter payload or
recipe errors. GUI, quick-move, disconnect, reload, block-removal, and
duplication gameplay cases remain UNTESTED and are release backlog items.

| Check | Result |
|---|---|
| Compilation | PASS |
| Aggregate build | PASS |
| Dev-client initialization | PASS |
| Payload registration/startup | PASS |
| Static transaction/mutation review | PASS |
| Interactive normal take | UNTESTED |
| Interactive quick-move/full inventory | UNTESTED |
| Interactive close/disconnect/block removal | UNTESTED |
| Interactive stale selection/reload | UNTESTED |
| Interactive duplication/item-loss | UNTESTED |
