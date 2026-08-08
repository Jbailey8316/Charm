package svenhjol.charm.common.features.copper_pistons;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charm.common.features.copper_pistons.common.Registers;

@FeatureDefinition(side = Side.Common, description = "Copper Pistons do not have quasi-connectivity.")
public final class CopperPistons extends SidedFeature {
    public final Registers registers;

    public CopperPistons(Mod mod) {
        super(mod);
        registers = new Registers(this);
    }

    public static CopperPistons feature() {
        return Mod.getSidedFeature(CopperPistons.class);
    }
}
