package svenhjol.charm.common.features.anvils_last_longer;

import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Makes anvils less likely to degrade when successfully used.")
public final class AnvilsLastLonger extends SidedFeature {
    @Configurable(name = "Damage chance", description = "Chance that a successful anvil operation applies vanilla anvil damage.")
    private static double chanceToDamage = 0.5d;

    public AnvilsLastLonger(Mod mod) {
        super(mod);
    }

    public double chanceToDamage() {
        return Mth.clamp(chanceToDamage, 0.0d, 1.0d);
    }

    public static AnvilsLastLonger feature() {
        return Mod.getSidedFeature(AnvilsLastLonger.class);
    }
}
