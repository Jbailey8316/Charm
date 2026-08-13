package svenhjol.charm.common.features.arcane_purpur.common;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charm.common.features.arcane_purpur.ArcanePurpur;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public final class Advancements extends Setup<ArcanePurpur> {
    public Advancements(ArcanePurpur feature) {
        super(feature);
    }

    public void teleportedToBlock(ServerPlayer player) {
        AdvancementHelper.trigger("teleported_to_block", player);
    }
}
