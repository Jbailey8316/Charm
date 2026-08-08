# Phase 7L.2A - Woodcutter Menu and Synchronization Architecture Proof

## Scope and verdict

This is an architecture-only phase. No Woodcutter production code was added.
The missing-feature count remains 24; Woodcutters and Lumberjacks remain
`MISSING`. The P0 Suspicious Block Falling Item Persistence issue remains open.

Verdict: **YELLOW**. An isolated menu and payload protocol can reproduce the
vanilla transaction model, but it requires an implementation/proof pass for
custom payload registration, client recipe display synchronization, and the
result-slot transaction before production code is authorized.

## Current 1.21.10 Stonecutter flow

The mapped 1.21.10 classes expose the following architecture:

* `StonecutterBlock` opens a `StonecutterMenu` through the normal server menu
  provider and `ContainerLevelAccess`.
* `StonecutterMenu` owns a one-slot input `Container`, a derived
  `ResultContainer`, a selected-recipe `DataSlot`, and the access context. It
  uses `stillValid(access, player, Blocks.STONECUTTER)` for identity and
  distance validation.
* On input changes, the menu obtains a `SelectableRecipe.SingleInputSet` from
  the level's `RecipeManager`, filters it by the current input, validates the
  selected index, and recomputes the derived result.
* `clickMenuButton` receives the client button index through the normal
  `ServerboundContainerButtonClickPacket` (`containerId`, `buttonId`). The
  server validates the index against the current visible list before changing
  selection.
* The result slot's `onTake` is the authoritative consumption point. It checks
  the selected recipe/current input, consumes the recipe's input amount, awards
  the result, updates the recipe-used state, and refreshes the result.
* `quickMoveStack` uses the normal `AbstractContainerMenu.moveItemStackTo`
  routing and invokes the result slot's take path; it does not create an
  independent output stack.
* `removed` uses the menu/container cleanup path to return the transient input
  to the player. The result is derived state and is not returned as stored
  inventory.

The mapping confirms private Stonecutter state (`recipesForInput`, input,
result, selected index, access) and the fixed `ClientboundUpdateRecipesPacket`
field `stonecutterRecipes`. This is the hardcoded limitation: the vanilla
recipe synchronization packet carries one synchronized Stonecutter set and
the menu is hardwired to consume that set. It does not provide a generic
custom-recipe-set channel.

## Existing project patterns

The repository has Fabric payload patterns (`PayloadTypeRegistry`,
`ServerPlayNetworking`, and `ClientPlayNetworking`) in Casks, Cooking Pots,
Item Frame Hiding, Shulker Box Transferring, and Crafting From Inventory. No
existing feature synchronizes a server-owned arbitrary recipe list into a
menu. Charmony's `CommonRegistry` can register common S2C and client C2S
payload types, but it does not itself provide recipe-list/menu transaction
semantics.

## Proposed isolated architecture

The production implementation should use these components without changing
vanilla Stonecutter synchronization:

* `WoodcutterBlock`: Charm block extending the Stonecutter block behavior only
  for properties/opening; it opens the Charm menu with a
  `ContainerLevelAccess` tied to its position.
* `WoodcutterMenu`: Charm `AbstractContainerMenu` with one transient input
  slot, one derived result slot, a selected recipe identifier, and a server
  recipe-list generation. Its validity checks the Woodcutter block at the
  access position and player distance.
* `WoodcutterScreen`: client selector and result display. It never constructs
  an authoritative output; it renders server-supplied display data and sends
  only a selection request.
* `S2CWoodcutterRecipes`: a Charm payload containing menu/container ID,
  generation token, and the currently input-matching recipe display entries
  (stable recipe IDs plus result `SlotDisplay` data). It is sent whenever the
  input or recipe-manager generation changes.
* `C2SWoodcutterSelect`: a Charm payload containing menu/container ID,
  generation token, and stable recipe ID. The server validates all three
  fields against the open menu, current input, current `RecipeManager`, and
  current generation before changing selection.
* `WoodcutterResultSlot`: a narrow result slot whose take operation delegates
  to one server-side `takeResult` method. This keeps normal click and quick-move
  on exactly one consumption path.

Stable recipe IDs are preferable to client-provided output stacks or indexes.
The display payload is limited to recipes matching the current input, not all
277 recipes. The server still recomputes the output from the current
`RecipeHolder` and never trusts display/result data from the client.

## Authoritative state model

| State | Owner | Client representation |
|---|---|---|
| Input `ItemStack` | Server menu input container | Normal synchronized slot |
| Available recipes | Server `RecipeManager` filtered by input | Display-only payload |
| Selected recipe | Server stable recipe ID/generation | Mirrored selection index/ID |
| Result `ItemStack` | Server-derived result slot | Normal synchronized slot |
| Recipe generation | Server menu, incremented after reload/list refresh | Payload token |
| Workstation validity | Server `ContainerLevelAccess` + block identity/distance | Menu close from server |

The client may request a recipe but cannot provide an output, recipe object,
ingredient count, or authoritative input mutation.

## State machine

| Event | Server mutation | Client synchronization |
|---|---|---|
| Open | Create menu/access; input empty; generation captured | Open screen; empty list/result |
| Insert input | Re-filter current RecipeManager; reset invalid selection | S2C matching displays + result slot |
| Select recipe | Validate generation, ID, input, and menu; update selected ID | Normal result-slot update |
| Input changed/removed | Re-filter; clear invalid selection/result | New list and result/empty result |
| Take result | Atomically revalidate menu/input/recipe; move output; consume exact input; refresh | Slot changes and new list/result |
| Shift-click result | Use same result-slot take path after destination acceptance | Same slot/data synchronization |
| Recipe reload | Increment generation; re-resolve current input; clear invalid selection | New S2C list; stale requests rejected |
| Workstation removed | `stillValid` fails; close menu; return input once | Server close packet |
| Close/disconnect | `removed` clears transient input through vanilla helper | No result return; derived result discarded |

## Stale selections and abuse

The server does not trust an index alone. A request is accepted only when:

1. the packet container ID equals the player's currently open Woodcutter menu;
2. the generation equals the menu's current generation;
3. the stable recipe ID resolves in the current RecipeManager;
4. the recipe is a `WoodcuttingRecipe` and matches the current input;
5. the workstation remains valid.

Negative/huge indexes, unknown IDs, Stonecutting/Kiln IDs, Oak selection after
switching to Birch, repeated selections, and selections for a closed menu are
ignored without mutation. A reload increments the generation and forces a
fresh list; an old request cannot craft against a stale holder.

## Result and quick-move proof obligations

`takeResult` is the only production operation allowed to consume input. It
must perform validation and destination acceptance before mutation, then in one
server tick:

* resolve the current holder from the current manager;
* verify the current input and selected recipe;
* copy/insert exactly the assembled result using the active slot-transfer
  semantics;
* consume the recipe's declared input amount exactly once;
* refresh the input-matching list, selection, and result.

Normal clicks and quick-move must call this same operation. If a destination is
full, no input is consumed. Repeated shift-click can only succeed after each
prior operation has consumed input and regenerated a valid result. A result
slot is never treated as persistent storage, so input cannot yield two results
or leave a second copy on close.

## Close, disconnect, removal, and reload

The menu should use `AbstractContainerMenu.clearContainer`/the current vanilla
Stonecutter cleanup pattern for the one input slot. The result is discarded as
derived state. `stillValid` must use `ContainerLevelAccess` and the Woodcutter
block identity, not a distance-only check. A removed/replaced Woodcutter closes
the menu and returns the input once. A reload invalidates the generation and
re-resolves the current input; no old `RecipeHolder` remains authoritative.

## Vanilla isolation and client requirements

No mixin or redirect should modify `RecipeManager.stonecutterRecipes`,
`ClientboundUpdateRecipesPacket`, `StonecutterMenu`, or vanilla Stonecutter
recipes. The Charm payloads and Charm menu own the custom recipe set. The
Kiln/firing and crafting recipe types remain untouched.

Charm is required on both client and server for the Woodcutter screen and
payloads, consistent with the existing Charm modules. A missing or incompatible
client must be rejected/treated as an unsupported installation rather than
falling back to a vanilla Stonecutter that cannot represent the custom recipe
set.

## Planned implementation files

Common: `Woodcutters.java`, `WoodcutterMenu.java`, `WoodcutterResultSlot.java`,
`WoodcutterNetworking.java`, payload record/codecs, and registration wiring.
Client: `WoodcuttersClient.java`, `WoodcutterScreen.java`, and the client
payload receiver. Resources: block/item models, blockstate, recipe for the
workstation, loot, translations, and creative-tab placement.

No files in this plan are implemented by Phase 7L.2A.

## 7L.2C implementation findings

The approved isolated design was implemented in `WoodcutterMenu`,
`WoodcutterNetworking`, and the client Woodcutter screen. The S2C recipe list
contains only stable matching recipe IDs plus menu/generation metadata; C2S
selection contains no result data. Server validation rechecks the current
recipe manager and input before selection or consumption. The result slot and
quick-move share one `onTake` consumption path. Static review passed; GUI and
multiplayer transaction cases remain untested.

## Required proof before 7L.2B

The implementation phase must compile a focused menu test path and perform
static review of every input/result mutation, then exercise normal take,
quick-move, full inventory, close, removal, reload, stale selection, and
component-bearing stack cases. Until those proofs exist, the verdict remains
YELLOW and Woodcutters stays `MISSING`.
