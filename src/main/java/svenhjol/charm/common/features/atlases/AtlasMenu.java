package svenhjol.charm.common.features.atlases;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

/** Atlas menu's first three slots are the replenishable empty-map supply. */
public final class AtlasMenu extends AbstractContainerMenu {
    private final Inventory playerInventory;
    private final SupplyContainer emptyMapSlots;
    private final MapContainer filledMapSlots;
    private boolean initializing = true;
    private boolean syncing;

    private static final int SUPPLY_SLOTS = AtlasData.INITIAL_EMPTY_MAPS;
    private static final int FILLED_SLOTS = 9;

    public AtlasMenu(int containerId, Inventory inventory) {
        super(Atlases.feature().menu.get(), containerId);
        this.playerInventory = inventory;
        this.emptyMapSlots = new SupplyContainer(this);
        this.filledMapSlots = new MapContainer(this);
        var atlas = findAtlas();
        int available = atlas.isEmpty() ? 0 : atlas.get(Atlases.feature().data.get()).emptyMaps();
        for (int i = 0; i < AtlasData.INITIAL_EMPTY_MAPS; i++) {
            emptyMapSlots.setItem(i, i < available ? new ItemStack(Items.MAP) : ItemStack.EMPTY);
            addSlot(new EmptyMapSlot(emptyMapSlots, i, 8, 18 + i * 18));
        }
        var maps = atlas.isEmpty() ? java.util.List.<AtlasMapEntry>of() : atlas.get(Atlases.feature().data.get()).maps();
        for (int i = 0; i < FILLED_SLOTS; i++) {
            if (i < maps.size()) filledMapSlots.setItem(i, maps.get(i).map().copy());
            addSlot(new FilledMapSlot(filledMapSlots, i, 68 + (i % 3) * 18, 18 + (i / 3) * 18, this));
        }
        // Expose the normal player inventory so maps can be transferred through
        // vanilla click/drag and shift-click handling.
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, 9 + row * 9 + column,
                    8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
        initializing = false;
    }

    public Inventory playerInventory() { return playerInventory; }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < SUPPLY_SLOTS) {
            var slot = getSlot(index);
            if (!slot.hasItem()) return ItemStack.EMPTY;
            var moved = slot.getItem().copy();
            if (!moveItemStackTo(slot.getItem(), SUPPLY_SLOTS + FILLED_SLOTS, slots.size(), true)) return ItemStack.EMPTY;
            slot.setChanged();
            slot.onTake(player, moved);
            return moved;
        }
        if (index < SUPPLY_SLOTS + FILLED_SLOTS) {
            var slot = getSlot(index);
            if (!slot.hasItem()) return ItemStack.EMPTY;
            var moved = slot.getItem().copy();
            if (!moveItemStackTo(slot.getItem(), SUPPLY_SLOTS + FILLED_SLOTS, slots.size(), true)) return ItemStack.EMPTY;
            slot.setChanged();
            slot.onTake(player, moved);
            return moved;
        }
        var slot = getSlot(index);
        if (!slot.hasItem() || !slot.getItem().is(Items.MAP)) return ItemStack.EMPTY;
        var moved = slot.getItem().copyWithCount(1);
        if (slot.getItem().is(Items.MAP) && moveItemStackTo(moved, 0, SUPPLY_SLOTS, false)) {
            slot.remove(1);
            slot.onTake(player, moved);
            return moved;
        }
        if (!slot.getItem().is(Items.FILLED_MAP) || !moveItemStackTo(moved, SUPPLY_SLOTS, SUPPLY_SLOTS + FILLED_SLOTS, false)) return ItemStack.EMPTY;
        slot.remove(1);
        slot.onTake(player, moved);
        return moved;
    }

    @Override public boolean stillValid(Player player) {
        return player.isAlive() && playerInventory.player == player && !findAtlas().isEmpty();
    }

    void supplyChanged() {
        if (initializing || syncing || playerInventory.player.level().isClientSide()) return;
        var atlas = findAtlas();
        if (atlas.isEmpty()) return;
        var data = atlas.get(Atlases.feature().data.get());
        if (data == null) return;
        int count = 0;
        for (int i = 0; i < AtlasData.INITIAL_EMPTY_MAPS; i++) {
            var stack = emptyMapSlots.getItem(i);
            if (!stack.isEmpty()) {
                if (!stack.is(Items.MAP)) emptyMapSlots.setItem(i, ItemStack.EMPTY);
                else { stack.setCount(1); count++; }
            }
        }
        if (count != data.emptyMaps()) {
            atlas.set(Atlases.feature().data.get(), data.withEmptyMaps(count));
            broadcastChanges();
        }
    }

    void mapsChanged() {
        if (initializing || syncing || playerInventory.player.level().isClientSide()) return;
        var atlas = findAtlas();
        if (atlas.isEmpty()) return;
        var data = atlas.get(Atlases.feature().data.get());
        if (data == null) return;
        var original = data.maps();
        var result = new java.util.ArrayList<>(original);
        var removed = new java.util.ArrayList<Integer>();
        for (int i = 0; i < FILLED_SLOTS; i++) {
            var stack = filledMapSlots.getItem(i);
            if (stack.isEmpty()) {
                if (i < original.size()) removed.add(i);
                continue;
            }
            var entry = importedEntry(stack, original, i);
            if (entry == null) {
                playerInventory.placeItemBackInInventory(stack.copy());
                filledMapSlots.setItem(i, ItemStack.EMPTY);
                continue;
            }
            if (i < result.size()) result.set(i, entry);
            else result.add(entry);
        }
        removed.sort(java.util.Comparator.reverseOrder());
        for (int index : removed) if (index < result.size()) result.remove(index);
        var next = new AtlasData(data.scale(), data.activeMap(), data.emptyMaps(), java.util.List.copyOf(result));
        if (!next.equals(data)) {
            atlas.set(Atlases.feature().data.get(), next);
            syncMapSlots(next);
            broadcastChanges();
        }
    }

    private AtlasMapEntry importedEntry(ItemStack stack, java.util.List<AtlasMapEntry> existing, int index) {
        if (!stack.is(Items.FILLED_MAP)) return null;
        MapId id = stack.get(DataComponents.MAP_ID);
        if (id == null || !(playerInventory.player.level() instanceof ServerLevel level)) return null;
        var saved = MapItem.getSavedData(id, level);
        if (saved == null) return null;
        for (int i = 0; i < existing.size(); i++) {
            if (i != index && existing.get(i).mapId().equals(id)) return null;
            if (i != index && existing.get(i).dimension().equals(level.dimension().location())
                && existing.get(i).centerX() == saved.centerX && existing.get(i).centerZ() == saved.centerZ) return null;
        }
        return new AtlasMapEntry(stack.copyWithCount(1), id, saved.centerX, saved.centerZ, level.dimension().location());
    }

    private boolean canImport(ItemStack stack, int index) {
        if (playerInventory.player.level().isClientSide()) return true;
        var atlas = findAtlas();
        var data = atlas.isEmpty() ? null : atlas.get(Atlases.feature().data.get());
        return data != null && importedEntry(stack, data.maps(), index) != null;
    }

    private void syncMapSlots(AtlasData data) {
        syncing = true;
        try {
            for (int i = 0; i < FILLED_SLOTS; i++) filledMapSlots.setItem(i, i < data.maps().size() ? data.maps().get(i).map().copy() : ItemStack.EMPTY);
        } finally { syncing = false; }
    }

    private ItemStack findAtlas() {
        var item = Atlases.feature().item.get();
        if (playerInventory.getSelectedItem().is(item)) return playerInventory.getSelectedItem();
        if (playerInventory.getItem(Inventory.SLOT_OFFHAND).is(item)) return playerInventory.getItem(Inventory.SLOT_OFFHAND);
        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            var stack = playerInventory.getItem(i);
            if (stack.is(item)) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static final class EmptyMapSlot extends Slot {
        EmptyMapSlot(Container container, int index, int x, int y) { super(container, index, x, y); }
        @Override public boolean mayPlace(ItemStack stack) { return stack.is(Items.MAP); }
        @Override public int getMaxStackSize() { return 1; }
        @Override public int getMaxStackSize(ItemStack stack) { return 1; }
    }

    private static final class FilledMapSlot extends Slot {
        private final AtlasMenu menu;
        FilledMapSlot(Container container, int index, int x, int y, AtlasMenu menu) { super(container, index, x, y); this.menu = menu; }
        @Override public boolean mayPlace(ItemStack stack) { return stack.is(Items.FILLED_MAP) && menu.canImport(stack, getContainerSlot()); }
        @Override public int getMaxStackSize() { return 1; }
        @Override public int getMaxStackSize(ItemStack stack) { return 1; }
    }

    private static final class MapContainer extends SimpleContainer {
        private final AtlasMenu menu;
        MapContainer(AtlasMenu menu) { super(FILLED_SLOTS); this.menu = menu; }
        @Override public void setChanged() { super.setChanged(); menu.mapsChanged(); }
    }

    private static final class SupplyContainer extends SimpleContainer {
        private final AtlasMenu menu;
        SupplyContainer(AtlasMenu menu) { super(AtlasData.INITIAL_EMPTY_MAPS); this.menu = menu; }
        @Override public void setChanged() { super.setChanged(); menu.supplyChanged(); }
    }
}
