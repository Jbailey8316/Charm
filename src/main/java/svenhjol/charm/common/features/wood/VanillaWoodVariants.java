package svenhjol.charm.common.features.wood;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(
    side = Side.Common,
    description = "Barrels in all vanilla wood types.")
public final class VanillaWoodVariants extends SidedFeature {
    public final Registers registers;

    public VanillaWoodVariants(Mod mod) {
        super(mod);
        registers = new Registers(this);
    }
}
