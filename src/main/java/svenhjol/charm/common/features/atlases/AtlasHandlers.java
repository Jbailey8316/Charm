package svenhjol.charm.common.features.atlases;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import svenhjol.charmony.api.events.PlayerTickCallback;

final class AtlasHandlers {
    static void register() { PlayerTickCallback.EVENT.register(AtlasHandlers::tick); }

    private static void tick(Player player) {
        if (!(player instanceof ServerPlayer server) || !(server.level() instanceof ServerLevel)) return;
        update(server, server.getMainHandItem());
        update(server, server.getOffhandItem());
    }

    private static void update(ServerPlayer player, ItemStack stack) {
        var feature = Atlases.feature();
        if (feature == null || !stack.is(feature.item.get())) return;
        var component = feature.data.get();
        var data = stack.getOrDefault(component, AtlasData.initial());
        int diameter = 128 * (1 << data.scale());
        int half = diameter / 2;
        int cellX = Math.floorDiv(player.blockPosition().getX() + half, diameter);
        int cellZ = Math.floorDiv(player.blockPosition().getZ() + half, diameter);
        var dimension = player.level().dimension().location();
        int found = -1;
        for (int i = 0; i < data.maps().size(); i++) {
            var entry = data.maps().get(i);
            if (entry.dimension().equals(dimension)
                // Stored centers are already aligned to the grid's half-diameter.
                // Do not apply the player-position offset a second time.
                && Math.floorDiv(entry.centerX(), diameter) == cellX
                && Math.floorDiv(entry.centerZ(), diameter) == cellZ) { found = i; break; }
        }
        if (found >= 0) {
            if (data.activeMap() != found) stack.set(component, data.withActive(found));
            updateVanillaMap(player, data.maps().get(found));
            return;
        }
        if (data.emptyMaps() <= 0) return;
        int centerX = cellX * diameter + half;
        int centerZ = cellZ * diameter + half;
        ItemStack map = MapItem.create((ServerLevel) player.level(), centerX, centerZ, (byte)data.scale(), false, false);
        MapId id = map.get(DataComponents.MAP_ID);
        if (id == null) return;
        var next = data.add(new AtlasMapEntry(map, id, centerX, centerZ, dimension));
        stack.set(component, next);
        updateVanillaMap(player, next.maps().get(next.activeMap()));
    }

    private static void updateVanillaMap(ServerPlayer player, AtlasMapEntry entry) {
        var saved = MapItem.getSavedData(entry.mapId(), player.level());
        if (saved != null) ((MapItem) net.minecraft.world.item.Items.FILLED_MAP).update(player.level(), player, saved);
    }
}
