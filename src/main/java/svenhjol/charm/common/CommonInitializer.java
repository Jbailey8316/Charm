package svenhjol.charm.common;

import net.fabricmc.api.ModInitializer;
import svenhjol.charm.CharmMod;
import svenhjol.charm.common.features.wood.VanillaWoodVariants;
import svenhjol.charm.common.features.coral_squids.CoralSquids;
import svenhjol.charm.common.features.recipe_improvements.RecipeImprovements;
import svenhjol.charm.common.features.aerial_affinity.AerialAffinity;
import svenhjol.charm.common.features.animal_armor_enchanting.AnimalArmorEnchanting;
import svenhjol.charm.common.features.anvils_last_longer.AnvilsLastLonger;
import svenhjol.charm.common.features.firing.Firing;
import svenhjol.charm.common.features.kilns.Kilns;
import svenhjol.charm.common.features.copper_pistons.CopperPistons;
import svenhjol.charmony.api.core.Side;

public final class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        svenhjol.charmony.core.common.CommonInitializer.init();

        var mod = CharmMod.instance();
        mod.addSidedFeature(VanillaWoodVariants.class);
        mod.addSidedFeature(CoralSquids.class);
        mod.addSidedFeature(RecipeImprovements.class);
        mod.addSidedFeature(AerialAffinity.class);
        mod.addSidedFeature(AnimalArmorEnchanting.class);
        mod.addSidedFeature(AnvilsLastLonger.class);
        mod.addSidedFeature(Firing.class);
        mod.addSidedFeature(Kilns.class);
        mod.addSidedFeature(CopperPistons.class);
        mod.run(Side.Common);
    }
}
