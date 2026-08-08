package svenhjol.charm.common.features.coral_squids;

import net.minecraft.util.Mth;
import svenhjol.charm.common.features.coral_squids.common.Providers;
import svenhjol.charm.common.features.coral_squids.common.Registers;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Coral Squids spawn near coral in warm oceans.")
public final class CoralSquids extends SidedFeature {
    public final Registers registers;
    public final Providers providers;

    @Configurable(name = "Drop chance", description = "Chance (out of 1.0) of a coral squid dropping coral when killed.")
    private static double dropChance = 0.2d;

    public CoralSquids(Mod mod) {
        super(mod);
        registers = new Registers(this);
        providers = new Providers(this);
    }

    public double dropChance() {
        return Mth.clamp(dropChance, 0.0d, 1.0d);
    }

    public static CoralSquids feature() {
        return Mod.getSidedFeature(CoralSquids.class);
    }
}
