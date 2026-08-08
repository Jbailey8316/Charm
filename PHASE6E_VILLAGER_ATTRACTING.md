# Phase 6E — Villager Attracting Validation

## Scope and decision

Villager Attracting was audited against the historical Charm 1.21.1 implementation and the archived Charm behavior description. The current 1.21.10 port is behaviorally equivalent and uses maintained 1.21.10 APIs. No production code changes were required; this phase is documentation-only.

## Historical and current implementation

Both versions register a server-side `ServerEntityEvents.ENTITY_LOAD` handler. For each loaded `Villager`, the handler adds one vanilla `TemptGoal` at priority **3**, with movement speed **0.6**, a tag-backed item predicate, and `canScare=false`. The goal is added only when that villager does not already have a `TemptGoal`.

The current port uses `ServerEntityEvents.ENTITY_LOAD`, `Villager.goalSelector`, and the 1.21.10 `TemptGoal` constructor without compatibility shims or reflection. `TemptGoal` supplies normal server-side navigation, look control, obstacle handling, and target reevaluation; Charm does not teleport or directly manipulate villager positions.

The feature is registered as a common, enabled-by-default Charmony Tweaks feature. Its setup callback is booted only while the feature is enabled, so disabling the feature prevents the Charm goal registration. The attribute mixin remains the historical support code and adds `Attributes.TEMPT_RANGE` with value **4.0** to villagers.

## Loved-item tag

The current identifier is `charmony:villager_loved` (`TagKey<Item>` in `villager_attracting.Tags`). The historical Charm identifier was `charm:villager_loved`; the namespace change is required by the recovered Charmony module identity. The current resource is `data/charmony/tags/item/villager_loved.json` and contains exactly:

```json
{
  "values": ["minecraft:emerald_block"]
}
```

The tag is data-driven and uses normal item-tag loading, so server data packs can extend or replace it according to standard tag rules. Emerald Block attracts by default. Ordinary Emerald and unrelated items do not attract unless a server explicitly adds them to this tag.

## Behavior matrix

| Behavior | Result |
| --- | --- |
| Attraction range | `TEMPT_RANGE` = 4.0 blocks for target selection |
| Movement speed | 0.6 |
| Goal priority | 3 |
| Main hand | Counts when the held stack is in `charmony:villager_loved` |
| Offhand | Counts; 1.21.10 `TemptGoal` checks both main and offhand |
| Other inventory slots | Do not count; `TemptGoal` does not scan inventory |
| Eligible entities | All `Villager` instances, including employed, unemployed, nitwit, and baby villagers, matching the historical `instanceof Villager` hook |
| Multiple players | Vanilla `TemptGoal` target selection/reevaluation chooses a nearby valid player; no Charm shared target state is used |
| Obstacles | Normal villager path navigation is used; no teleportation or direct position manipulation |
| Danger/work/sleep behavior | Goal competes through the vanilla priority/flag system; it does not remove panic, work, sleep, POI, trade, or raid goals |
| Feature disabled | The feature boot callback is not run, so the Charm attraction goal is not installed; vanilla villager AI remains |

The periodic player tick handler is separate from attraction: it only awards the existing `attracted_villager` advancement when a server-side player holds a loved item in the **main hand** and a villager is within an 8-block AABB. Its main-hand-only check does not restrict the attraction goal itself.

## 1.21.10 API/parity audit

The historical implementation used the same goal architecture. The port’s required mapping change is the already-migrated `Level.isClientSide()` accessor in the advancement tick path; no Villager Attracting-specific API workaround is needed. Decompiled 1.21.10 `TemptGoal` confirms:

- target selection is server-side and uses `TEMPT_RANGE`;
- the item predicate is evaluated against `Player.getMainHandItem()` and `Player.getOffhandItem()`;
- continuation reevaluates the target and stops navigation when conditions fail;
- movement uses `PathNavigation.moveTo(Entity, speed)`.

No client-only state, custom networking, mixin descriptor failure, or inventory scanning was found. The implementation therefore preserves the original hand-only semantics and data-driven tag design.

## Runtime validation

The available environment supports client startup/mixin smoke validation but not reliable GUI/gameplay automation. Static validation and decompilation passed; the following interactive cases remain **UNTESTED** and are retained in `RELEASE_VALIDATION_BACKLOG.md`:

- main-hand Emerald Block attraction and follow/stop behavior;
- offhand attraction;
- inventory-only non-attraction;
- range boundary and obstacle pathfinding;
- multiple villagers and multiple players;
- work/sleep/panic/hostile-mob priority interactions;
- baby, nitwit, employed, and unemployed villager behavior;
- feature-disabled vanilla behavior;
- dedicated-server synchronization.

The client startup/mixin smoke reached the normal running client state; the log shows the Villager mixin applied and `Running common feature VillagerAttracting`. No Villager Attracting AI, navigation, or mixin errors were observed. Authentication/Realms PKIX failures and Netty reflective-access diagnostics are development-environment/network noise and are unrelated to this feature. World creation and interactive villager tests were not automated.

## Files changed and build

Changed files:

- `PHASE6E_VILLAGER_ATTRACTING.md`
- `RELEASE_VALIDATION_BACKLOG.md`

No Java, mixin, resource, or configuration implementation files were changed. `./gradlew build` completed successfully (93 actionable tasks, all successful/up-to-date).
