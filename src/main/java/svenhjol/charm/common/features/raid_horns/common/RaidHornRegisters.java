package svenhjol.charm.common.features.raid_horns.common;

import net.minecraft.world.InteractionResult;
import svenhjol.charm.common.features.raid_horns.RaidHorns;
import svenhjol.charmony.api.events.EntityKilledDropCallback;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

public final class RaidHornRegisters extends Setup<RaidHorns> {
    public RaidHornRegisters(RaidHorns feature) {
        super(feature);
        EntityKilledDropCallback.EVENT.register(feature().handlers::entityDrop);
        CommonRegistry.forFeature(feature).wandererTrade(() -> new RaidHornHandlers.RaidHornTrade(feature), true);
    }
}
