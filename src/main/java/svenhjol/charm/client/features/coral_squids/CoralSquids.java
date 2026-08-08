package svenhjol.charm.client.features.coral_squids;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charm.common.features.coral_squids.common.CoralSquid;

@FeatureDefinition(side = Side.Client)
public final class CoralSquids extends SidedFeature {
    public final Registers registers;
    public final svenhjol.charm.common.features.coral_squids.CoralSquids common;

    public CoralSquids(Mod mod) {
        super(mod);
        common = svenhjol.charm.common.features.coral_squids.CoralSquids.feature();
        registers = new Registers(this);
    }

    public static CoralSquids feature() { return Mod.getSidedFeature(CoralSquids.class); }
}
