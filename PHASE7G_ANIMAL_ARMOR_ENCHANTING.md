# Phase 7G — Animal Armor Enchanting

## Historical behavior

Charm's feature description explicitly covered both horse and wolf armor. It
used the configurable `on_horse_armor` and `on_wolf_armor` enchantment tags to
define the accepted set. Historical source listed Protection, Fire
Protection, Blast Protection, Projectile Protection, Thorns, Frost Walker,
Feather Falling, Respiration, and Soul Speed. The feature made those
enchantments accept animal armor and exposed the animal's body armor through
the normal equipment-slot lookup so vanilla armor/durability systems could
see it. It did not add Mending, Unbreaking, Binding, or Vanishing. A custom
advancement was awarded when enchanted armor was equipped.

## Vanilla 1.21.10 and parity decision

Minecraft 1.21.10 has data-driven equipment and adds copper Horse Armor and
Wolf Armor. The historical body-type design maps directly to both equestrian
and canine equipment, so Wolf Armor remains in scope and copper Horse Armor
is included as the newest member of the existing horse-armor family.

| Item family | Historical Charm | Vanilla 1.21.10 | Parity gap | Action |
|---|---|---|---|---|
| Leather/Iron/Golden/Diamond Horse Armor | Accepted the nine tagged enchantments | Horse armor is not normally enchantable | Full gap | Restore |
| Copper Horse Armor | Not present historically | Current equestrian armor family | Semantic extension of body-type rule | Include |
| Wolf Armor | Accepted the nine tagged enchantments | Wolf Armor is not normally enchantable | Full gap | Restore |

Vanilla does not provide the historical enchanting behavior for these items.
The current implementation therefore adds Charm item tags for horse and wolf
armor and the two historical enchantment tags. Because 1.21.10's
`Enchantment#canEnchant` receives the enchantment object rather than its
registry holder, the narrow compatibility hook identifies the stable vanilla
translation keys for the nine historical enchantments while the data tags
remain the authoritative documented configuration surface.

## 1.21.10 implementation

`AnimalArmorEnchanting` is a native common Charm feature with the established
feature toggle. `EnchantmentMixin` allows only the nine historical
enchantments on the matching armor tags. `MobMixin` targets
`LivingEntity#getItemBySlot` and, when enabled, exposes Horse/Wolf body armor
through `EquipmentSlot.BODY`; this preserves vanilla damage and durability
code paths instead of creating duplicate armor logic. The root Charm mixin
configuration is used, and the feature returns vanilla behavior when disabled.

The data-driven tag resources contain all historical enchantments and the
five current horse-armor items (including copper) plus Wolf Armor. No custom
Enchantment subclass or enchanting GUI was introduced. The normal enchanting
table, enchanted-book, and anvil flows remain the acquisition paths once
runtime data is loaded. Mending, Unbreaking, curses, and unsupported
enchantments are intentionally not added.

## Runtime and resource status

All new JSON resources parse successfully. `:compileJava` passes. Runtime
enchanting-table, anvil, equipped-animal effect, durability, rendering,
config-disabled, advancement, and multiplayer tests remain UNTESTED and are
listed in `RELEASE_VALIDATION_BACKLOG.md`; no gameplay result is claimed from
static validation. The P0 Suspicious Block Falling Item Persistence blocker
remains OPEN.

## Files changed

- `src/main/java/svenhjol/charm/common/CommonInitializer.java`
- `src/main/java/svenhjol/charm/common/features/animal_armor_enchanting/AnimalArmorEnchanting.java`
- `src/main/java/svenhjol/charm/common/features/animal_armor_enchanting/Tags.java`
- `src/main/java/svenhjol/charm/common/mixins/animal_armor_enchanting/EnchantmentMixin.java`
- `src/main/java/svenhjol/charm/common/mixins/animal_armor_enchanting/MobMixin.java`
- `src/main/resources/charm.common.mixins.json`
- `src/main/resources/data/charm/tags/item/horse_armor.json`
- `src/main/resources/data/charm/tags/item/wolf_armor.json`
- `src/main/resources/data/charm/tags/enchantment/on_horse_armor.json`
- `src/main/resources/data/charm/tags/enchantment/on_wolf_armor.json`
- `src/main/resources/data/charmony/advancement/animal_armor_enchanting/added_enchantment_to_animal_armor.json`
- `src/main/resources/assets/charmony/lang/en_us.json`
- `PHASE7B_MASTER_PARITY_AUDIT.md`
- `RELEASE_VALIDATION_BACKLOG.md`

## Validation result

The completely missing-feature count is reduced from 30 to 29. Aggregate build
and resource processing are required before commit; interactive behavior is
currently `PRESENT / NEEDS VALIDATION`. Existing unrelated authentication and
asset warnings are not Animal Armor Enchanting failures.
