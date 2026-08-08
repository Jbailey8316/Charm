package svenhjol.charm.common;

import net.fabricmc.api.ModInitializer;
import svenhjol.charm.CharmMod;
import svenhjol.charm.common.features.wood.VanillaWoodVariants;
import svenhjol.charm.common.features.coral_squids.CoralSquids;
import svenhjol.charm.common.features.recipe_improvements.RecipeImprovements;
import svenhjol.charmony.api.core.Side;

public final class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        svenhjol.charmony.core.common.CommonInitializer.init();

        var mod = CharmMod.instance();
        mod.addSidedFeature(VanillaWoodVariants.class);
        mod.addSidedFeature(CoralSquids.class);
        mod.addSidedFeature(RecipeImprovements.class);
        mod.run(Side.Common);
    }
}
