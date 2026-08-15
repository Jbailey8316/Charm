package svenhjol.charm.common.features.potion_of_radiance;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.CommonRegistry;

@FeatureDefinition(side = Side.Common, description = "Adds Potion of Radiance, which grants the Glowing effect.")
public final class PotionOfRadiance extends SidedFeature {
    public final Registerable<Holder<Potion>> potion;
    public final Registerable<Holder<Potion>> longPotion;

    public PotionOfRadiance(Mod mod) {
        super(mod);
        var registry = CommonRegistry.forFeature(this);
        potion = registry.potion("radiance", () -> new Potion("radiance", new MobEffectInstance(MobEffects.GLOWING, 3600)));
        longPotion = registry.potion("long_radiance", () -> new Potion("long_radiance", new MobEffectInstance(MobEffects.GLOWING, 9600)));
        registry.potionRecipe(Potions.AWKWARD, () -> Items.TORCHFLOWER, potion.get());
        registry.potionRecipe(potion.get(), () -> Items.REDSTONE, longPotion.get());
    }

    public static PotionOfRadiance feature() {
        return Mod.getSidedFeature(PotionOfRadiance.class);
    }
}
