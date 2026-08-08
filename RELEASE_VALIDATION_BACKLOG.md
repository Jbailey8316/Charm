# Release Validation Backlog

Interactive chest tests remain outstanding because GUI automation was unavailable during Phases 5B.2 and 5B.3.

- Normal chests: Oak, Pale Oak, Crimson/Warped, Azalea, and Ebony placement, all facings, opening/lid/sounds, inventory and custom-name save/reload, break/drop, hopper input/output, comparator output.
- Normal double chests: Oak, Pale Oak, Azalea, Ebony matching pairing, 54-slot inventory, both lids, persistence, and breaking one half.
- Normal cross-pairing: Oak + Birch and Azalea + Ebony remain separate.
- Trapped chests: Oak, Pale Oak, Crimson/Warped, Azalea, and Ebony placement, rendering, storage, persistence, hoppers, and comparators.
- Trapped double/cross pairing: matching double behavior; Oak + Birch; Azalea + Ebony; normal + trapped; all remain correctly separated where required.
- Trapped redstone: closed/open/close signal, neighbor response, double trapped behavior, and comparator signal distinction.
- Crafting From Inventory: no-table unavailable; hotbar and main-inventory table access; table removal closes/invalidates the menu; unchanged table count; shaped/shapeless/shift-click/recipe-book crafting; save/reload; config disable/re-enable.
- Crafting exploit checks: drop or container-move the table while open, disconnect/reconnect with the menu open, rapid crafting, and dedicated-server synchronization.
