# Phase 7M — Configuration Parity Report

This is a source/config audit only. Defaults and behavior are taken from
historical source and current configuration declarations.

| Area | Historical/current option | Default/status |
|---|---|---|
| Item Stacking | selected books, potions, stews | INTENTIONALLY OMITTED; vanilla limits retained |
| Totem of Preserving | Grave Mode behavior | MYTHAS DIVERGENCE; no prerequisite Totem or Echo repair |
| Aerial Affinity | feature toggle | Present; runtime untested |
| Animal Armor Enchanting | feature toggle | Present; runtime untested |
| Anvils Last Longer | damage chance | Present; runtime/statistical tests pending |
| Recipe Improvements | individual recipe toggles and unlock option | Present; runtime recipe matrix pending |
| Kilns | feature toggle | Present; processing/automation pending |
| Copper Pistons | feature toggle | Present; movement/redstone pending |
| Woodcutting/Woodcutter | feature toggles/dependency | Present; transaction tests pending |
| Lumberjack | custom ladders, barrels, bookshelves | Present; defaults ON, OFF uses historical vanilla fallbacks |
| Storage Blocks | conversion/TNT options | Present; gameplay validation pending |
| Suspicious Block Creating | feature/config | Present; P0 falling persistence remains open |

No dead configuration was proven in the restored systems. The 21 missing
features necessarily have missing historical configuration declarations, but
those are not production dead options. Full option-by-option comparison for
the remaining Tweaks and module features is recorded as a pre-Production
Candidate task because several declarations are spread across nested modules.

Changed defaults/decisions are limited to the explicitly approved Item
Stacking omission and Totem of Preserving Mythas divergence.
