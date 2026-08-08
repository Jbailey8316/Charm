package svenhjol.charm.common.features.woodcutting;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.base.Setup;

public final class WoodcutterHandlers extends Setup<Woodcutters> {
    public WoodcutterHandlers(Woodcutters feature) { super(feature); }
    public void select(net.minecraft.world.entity.player.Player player, WoodcutterNetworking.C2SSelect packet) {
        if (player instanceof ServerPlayer server && server.containerMenu instanceof WoodcutterMenu menu && menu.containerId == packet.menuId()) menu.select(packet.generation(), packet.recipe());
    }
}
