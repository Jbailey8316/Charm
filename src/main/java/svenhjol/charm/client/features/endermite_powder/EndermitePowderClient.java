package svenhjol.charm.client.features.endermite_powder;

import svenhjol.charm.common.features.endermite_powder.EndermitePowder;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Client)
public final class EndermitePowderClient extends SidedFeature {
    public final EndermitePowder common;
    public final Registers registers;

    public EndermitePowderClient(Mod mod) {
        super(mod);
        common = EndermitePowder.feature();
        registers = new Registers(this);
    }
}
