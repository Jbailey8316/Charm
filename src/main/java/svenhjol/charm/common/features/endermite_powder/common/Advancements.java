package svenhjol.charm.common.features.endermite_powder.common;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charm.common.features.endermite_powder.EndermitePowder;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public final class Advancements extends Setup<EndermitePowder> {
    public Advancements(EndermitePowder feature) {
        super(feature);
    }

    public void usedEndermitePowder(ServerPlayer player) {
        AdvancementHelper.trigger("used_endermite_powder", player);
    }
}
