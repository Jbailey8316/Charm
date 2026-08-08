package svenhjol.charm.common.features.animal_armor_enchanting;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Allows the historical animal-armor enchantment set on horse and wolf armor.")
public final class AnimalArmorEnchanting extends SidedFeature {
    public AnimalArmorEnchanting(Mod mod) {
        super(mod);
    }

    public static AnimalArmorEnchanting feature() {
        return Mod.getSidedFeature(AnimalArmorEnchanting.class);
    }
}
