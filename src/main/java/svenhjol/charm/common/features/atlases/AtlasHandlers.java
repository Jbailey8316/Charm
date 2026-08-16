package svenhjol.charm.common.features.atlases;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import svenhjol.charmony.api.events.PlayerTickCallback;

import java.util.function.Predicate;

public final class AtlasHandlers {
    static void register() { PlayerTickCallback.EVENT.register(AtlasHandlers::tick); }

    public static boolean atlasContainsMap(Player player, Predicate<ItemStack> matcher) {
        return containsMap(player.getMainHandItem(), matcher) || containsMap(player.getOffhandItem(), matcher);
    }

    private static boolean containsMap(ItemStack stack, Predicate<ItemStack> matcher) {
        var feature = Atlases.feature();
        if (feature == null || !stack.is(feature.item.get())) return false;
        var data = stack.get(feature.data.get());
        return data != null && data.maps().stream().anyMatch(entry -> matcher.test(entry.map()));
    }

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
        if (data.emptyMaps() <= 0) {
            // The Atlas is full, but the last active map must continue to
            // receive vanilla tracking/decorations updates. Do not allocate
            // or retarget a map for this unmapped grid cell.
            if (data.activeMap() >= 0 && data.activeMap() < data.maps().size()) {
                updateVanillaMap(player, data.maps().get(data.activeMap()));
            }
            return;
        }
        // Historical Charm did not allocate a map while the Atlas menu was open;
        // replenished supplies take effect on the next normal held-item tick.
        if (player.containerMenu instanceof AtlasMenu) return;
        // MapItem.create expects the player's world position and performs the
        // vanilla grid-centering calculation itself. Passing our already
        // centered coordinate would apply that calculation a second time and
        // shift the saved map center, which also shifts PLAYER decorations.
        ItemStack map = MapItem.create((ServerLevel) player.level(),
            player.blockPosition().getX(), player.blockPosition().getZ(),
            (byte)data.scale(), true, true);
        MapId id = map.get(DataComponents.MAP_ID);
        if (id == null) return;
        var saved = MapItem.getSavedData(id, (ServerLevel) player.level());
        if (saved == null) return;
        var next = data.add(new AtlasMapEntry(map, id, saved.centerX, saved.centerZ, dimension));
        stack.set(component, next);
        updateVanillaMap(player, next.maps().get(next.activeMap()));
    }

    private static void updateVanillaMap(ServerPlayer player, AtlasMapEntry entry) {
        var saved = MapItem.getSavedData(entry.mapId(), player.level());
        if (saved != null) {
            saved.tickCarriedBy(player, entry.map());
            ((MapItem) net.minecraft.world.item.Items.FILLED_MAP).update(player.level(), player, saved);
            var packet = saved.getUpdatePacket(entry.mapId(), player);
            if (packet != null) player.connection.send(packet);
        }
    }
}
