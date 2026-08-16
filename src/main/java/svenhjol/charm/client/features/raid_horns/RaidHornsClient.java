package svenhjol.charm.client.features.raid_horns;

import svenhjol.charm.common.features.raid_horns.RaidHorns;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Client)
public final class RaidHornsClient extends SidedFeature {
    public final RaidHorns common;
    public final Registers registers;

    public RaidHornsClient(Mod mod) {
        super(mod);
        common = RaidHorns.feature();
        registers = new Registers(this);
    }
}
