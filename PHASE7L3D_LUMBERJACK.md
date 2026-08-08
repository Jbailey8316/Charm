# Phase 7L.3D - Lumberjack source recovery

## Pre-Implementation Source Recovery

This turn is audit-only. No production Lumberjack implementation is present.
The local `charm-1.21.1.zip` is authoritative for the recovered values below.

### Denied-patch review

The rejected draft contained these unverified or incorrect values:

| Draft value | Classification | Recovered result |
|---|---|---|
| POI registered as `lumberjack` | GUESSED / INCORRECT | Historical POI is `woodcutter`; Lumberjack references that POI. |
| POI owned by Lumberjack | GUESSED / INCORRECT | Historical Woodcutters owns/registers the POI. |
| POI `1,1` parameters | HISTORICALLY VERIFIED | `new PoiType(all Woodcutter states, 1, 1)`. |
| Profession secondary POI set empty | GUESSED / INCORRECT | Historical helper passes the Woodcutter block set as `secondaryPoi`. |
| `charmony:barrels` as an existing tag | GUESSED / UNVERIFIED | Historical tag is `charm:barrels`; current port has merged `c:block/barrels` content. A dedicated current `charmony:barrels` tag is a prerequisite if exact namespace parity is required. |
| `charmony:overworld_stripped_logs` as an existing tag | GUESSED / UNVERIFIED | Historical tag is `charm:overworld_stripped_logs`; current port has no equivalent yet. It must be restored before the two stripped-log trades. |
| Bookshelf fallback | HISTORICALLY VERIFIED | `minecraft:bookshelf`. |
| Ladder fallback | HISTORICALLY VERIFIED | `minecraft:ladder`. |
| Trade numbers and random ranges | HISTORICALLY VERIFIED | Directly recovered from `Registers.java`, `Trades.java`, and `GenericTrades.java`. |

The draft also used the wrong Java return type for the feature dependency check;
the current API requires `BooleanSupplier`.

### POI definition

Historical `woodcutters/common/Registers.java` registers POI ID `woodcutter`
with every state of the Woodcutter block and `PoiType(..., 1, 1)`. The
historical POI belongs to Woodcutters, not Lumberjacks. Historical
`CommonRegistry.villagerProfession` then uses the POI resource key for both
held-job-site and acquirable-job-site predicates and passes the Woodcutter
block set as `secondaryPoi`.

For 1.21.10, the direct equivalent is a registered `PoiType` holder, mapping
all Woodcutter states through `PoiTypes.TYPE_BY_STATE`, followed by a
`VillagerProfession` whose predicates test the Woodcutter POI holder/key.
The `PoiType` constructor and values `(1, 1)` are unchanged. Confidence: HIGH.

### Profession definition

* ID: `lumberjack`.
* Held job site: the Woodcutter POI only.
* Acquirable job site: the Woodcutter POI only.
* Requested items: empty immutable set in the historical helper.
* Secondary POI blocks: the Woodcutter block set.
* Work sound: registered sound event `charm:lumberjack` historically.
* Registration owner: Lumberjacks profession; Woodcutters owns the POI.

The current namespace is Charmony, so the migrated resource IDs will be
`charmony:lumberjack` and `entity.minecraft.villager.charmony.lumberjack`.
Historical artwork and four `lumberjack*.ogg` files are available in the
archive. Confidence: HIGH.

### Complete historical trade table

Generic listings use price multiplier `0.05F`; the two custom listings use
`0.2F` exactly as constructed in historical `Trades.java`.

| Level | Listing | Input | Second input | Output | Price/count range | Uses | XP | Multiplier |
|---:|---|---|---|---|---|---:|---:|---:|
| 1 | EmeraldsForTag | random `overworld_stripped_logs`, 8 | - | 1 Emerald | input 8, emerald 1 | 20 | 2 | .05 |
| 1 | EmeraldsForTag | random `minecraft:overworld_natural_logs`, 8 | - | 1 Emerald | input 8, emerald 1 | 20 | 2 | .05 |
| 1 | SaplingsForEmeralds | random Oak/Birch/Spruce sapling | - | 1 sapling | emerald 1 | 20 | 2 | .2 |
| 1 | TagForEmeralds / fallback | 1 Emerald | - | 1 custom ladder / vanilla Ladder | fixed | 20 | 2 | .05 |
| 2 | EmeraldsForItems | 23 Bone | - | 2 Emeralds | fixed | 5 | 1 | .05 |
| 2 | TagForEmeralds | 3 random `beds` | - | 2 Emeralds | fixed | 20 | 1 | .05 |
| 2 | TagForEmeralds | 2 random `wooden_fences` | - | 1 Emerald | fixed | 6 | 1 | .05 |
| 2 | TagForEmeralds | 2 random `fence_gates` | - | 1 Emerald | fixed | 6 | 1 | .05 |
| 3 | EmeraldsForTag | 7 random Warped Stem | - | 1 Emerald | fixed | 10 | 1 | .05 |
| 3 | EmeraldsForTag | 7 random Crimson Stem | - | 1 Emerald | fixed | 10 | 1 | .05 |
| 3 | SaplingsForEmeralds | random Acacia/Dark Oak sapling | - | 1 sapling | emerald 2-3 | 20 | 10 | .2 |
| 3 | BarkForLogs | 1 Emerald | 1 random log | 10-22 matching wood | second input fixed 1; output 10-22 | 10 | 10 | .2 |
| 3 | TagForEmeralds | 2 random `wooden_doors` | - | 1 Emerald | fixed | 10 | 1 | .05 |
| 4 | TagForEmeralds / fallback | 4 Emeralds | - | 1 custom barrel / vanilla Barrel | fixed | 15 | 1 | .05 |
| 4 | TagForEmeralds / fallback | 4 Emeralds | - | 1 custom chiseled bookshelf / vanilla Bookshelf | fixed | 15 | 1 | .05 |
| 4 | ItemsForEmeralds | 7 Emeralds | - | 1 Note Block | fixed | 15 | 1 | .05 |
| 5 | ItemsForEmeralds | 11 Emeralds | - | 3 Jukeboxes | fixed | 15 | 1 | .05 |
| 5 | ItemsForEmeralds | 5 Emeralds | - | 1 Cartography Table | fixed | 30 | 1 | .05 |
| 5 | ItemsForEmeralds | 4 Emeralds | - | 1 Loom | fixed | 30 | 1 | .05 |
| 5 | ItemsForEmeralds | 3 Emeralds | - | 1 Composter | fixed | 30 | 1 | .05 |

The historical bark listing is literal code behavior: it is not a bark item
or tag. It chooses one of seven log-to-wood pairs (Acacia, Birch, Dark Oak,
Jungle, Mangrove, Oak, Spruce) when the offer is generated, requires one
Emerald plus one matching log, and outputs 10 through 22 of the corresponding
wood. Azalea, Ebony, and Pale Oak are not in this explicit historical map.

The sapling listing chooses from hardcoded lists at offer generation. It always
outputs one sapling. The tier-1 price is exactly 1 Emerald; tier-3 is 2 or 3
Emeralds. The generated `MerchantOffer` owns these values, so subsequent GUI
opens, transactions, save/load, and restocking use normal vanilla persistence
and do not reroll an existing offer.

### Custom toggles and fallbacks

All three toggles default true and are evaluated when the trade listing is
registered:

* Ladders: custom block tag `charm:ladders`; OFF uses `minecraft:ladder`.
* Barrels: custom block tag `charm:barrels`; OFF uses `minecraft:barrel`.
* Bookshelves: custom block tag `charm:chiseled_bookshelves`; OFF uses
  `minecraft:bookshelf` (not a chiseled bookshelf).

The current restored content maps the ladder and chiseled-bookshelf tags to
`charmony:ladders` and `charmony:chiseled_bookshelves`. Barrel content is
currently merged through `c:block/barrels` (including Azalea and Ebony module
entries); exact `charmony:barrels` should be added before implementation if
namespace parity is required. The stripped-log tag must likewise be restored.

### Pale Oak and Charm-native families

Pale Oak is not historical. It is a safe semantic extension only for
categorical custom tag trades: ladders, barrels, and chiseled bookshelves.
It is not added to the explicit sapling lists or the explicit bark map.
Azalea and Ebony participate in the custom block tags where their historical
custom blocks exist, but do not participate in the explicit sapling or bark
lists unless a future approved semantic extension changes those lists.

### 1.21.10 API mapping

| Historical component | 1.21.10 equivalent | Confidence |
|---|---|---|
| Woodcutter POI `PoiType(states,1,1)` | `Registry.registerForHolder` + `PoiTypes.TYPE_BY_STATE` | HIGH |
| Profession POI predicates | `VillagerProfession` predicates matching POI holder/key | HIGH |
| Profession secondary POI | `ImmutableSet<Block>` containing Woodcutter block | HIGH |
| Profession registration | `BuiltInRegistries.VILLAGER_PROFESSION` | HIGH |
| Trade registration | existing `CommonRegistry.villagerTrade` + `VillagerTrades.TRADES` | HIGH |
| MerchantOffer | current `ItemCost`/`MerchantOffer` constructors | HIGH |
| Random source | server-side `RandomSource` supplied to `ItemListing#getOffer` | HIGH |
| Tag lookup | current `TagHelper`/registry access | HIGH |
| Custom listings | native `VillagerTrades.ItemListing` classes | HIGH |
| Work sound | registered `SoundEvent` supplied to profession | HIGH |

### Proposed implementation files

* Modify `Woodcutters.java` or its register setup to own/register the
  `woodcutter` POI and all block-state mappings.
* Create `Lumberjacks.java` for config, profession, dependency, and sound.
* Create `LumberjackRegisters.java` for the five-tier trade registration.
* Create `LumberjackTrades.java` for the two historical custom listings.
* Create `LumberjackTags.java` for verified tag keys only.
* Add the missing `charmony:barrels` and `charmony:overworld_stripped_logs`
  block tags with recovered contents (including approved Pale Oak only where
  categorical).
* Add Lumberjack profession translations, sounds metadata, and recovered
  villager/zombie-villager textures.

No files were changed by this recovery step before this document.

## POI Registration API/Lifecycle Verification

The interrupted production attempt was removed. The committed Woodcutter
source is unchanged.

### Existing helper

`CommonRegistry.pointOfInterestBlockStates` is declared as:

```java
Registerable<Void> pointOfInterestBlockStates(
    Supplier<Holder<PoiType>> poiType,
    Supplier<List<BlockState>> states)
```

It creates a `Registerable` whose supplier resolves the existing POI holder,
combines its matching states with the supplied states, and associates those
states with the holder through Charmony's existing POI registration path. It
does not register a new `PoiType`; the POI must already be registered.

The returned `Registerable` is evaluated during the feature registration
phase. Its constructor adds its `get()` operation to the owning feature's
registration list.

### Current call sites

There is one current call site: `core/common/features/wood/types/Barrel.java`.
It is called from the custom wood holder constructor, after the block supplier
exists, and targets the already-registered vanilla Fisherman POI. This is a
constructor-time registration pattern, not a feature `run()` override.

### Lifecycle

`Mod.run(Common)` constructs all features first, populates configuration,
evaluates dependency checks, runs `Setup.boot()` callbacks, then evaluates the
`Registerable` lists, calls `CommonRegistry.finishModRegistration`, and only
then invokes each enabled feature's `run()`. `Registerable` therefore provides
the supported registration ordering mechanism. A new `run()` override has not
been proven necessary or supported for POI registration and is not proposed.

### Historical pattern and safe current pattern

Historical `Woodcutters.common.Registers` used `registry.pointOfInterestType`
to register `woodcutter` with all Woodcutter states and `(1, 1)`. The current
port lacks a public `pointOfInterestType` helper, so the safe migration plan is:

1. Woodcutters creates/registers the `charmony:woodcutter` `PoiType` as a
   normal `Registerable`, with all Woodcutter states and `(1, 1)`.
2. In the same constructor-time registration setup, Woodcutters invokes the
   existing `pointOfInterestBlockStates` helper with that already-registered
   holder and the same state list. No private map is touched by feature code.
3. Lumberjacks registers `charmony:lumberjack` using predicates matching the
   Woodcutter POI holder, an empty requested-item set, and a secondary POI set
   containing the Woodcutter block.
4. Lumberjack trade registration runs only after the profession exists and
   uses the current `CommonRegistry.villagerTrade` path.

The helper's constructor-time `Registerable` ordering is the existing Barrel
pattern. The only remaining implementation work is exposing/using the current
POI registration mechanism for a newly registered POI without direct access to
Minecraft's private state map. Confidence in the historical semantics and
ordering is HIGH; the exact current helper exposure for step 1 must be
implemented through an existing supported Charmony path before production
changes resume.
