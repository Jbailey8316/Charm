package svenhjol.charm.common.features.suspicious_effect_improvements;

import svenhjol.charm.common.features.suspicious_effect_improvements.suspicious_big_plants.SuspiciousBigPlants;
import svenhjol.charm.common.features.suspicious_effect_improvements.suspicious_effects_last_longer.SuspiciousEffectsLastLonger;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.Side;
import net.minecraft.util.Mth;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Adds more functionality to suspicious effects, flowers and stews.")
public final class SuspiciousEffectImprovements extends SidedFeature {
    private static SuspiciousEffectImprovements INSTANCE;
    @Configurable(name = "Pitcher plant effect duration", description = "Number of seconds of strength and regeneration from a pitcher plant.")
    private static int pitcherPlantEffectDuration = 8;
    @Configurable(name = "Sunflower effect duration", description = "Number of seconds of health boost from a sunflower.")
    private static int sunflowerEffectDuration = 8;
    @Configurable(name = "Beneficial effect multiplier", description = "The duration of beneficial suspicious effects will be multiplied by this number.")
    private static int beneficialMultiplier = 4;
    @Configurable(name = "Detrimental effect multiplier", description = "The duration of detrimental suspicious effects will be multiplied by this number.")
    private static int detrimentalMultiplier = 2;
    public final SuspiciousBigPlants bigPlants;
    public final SuspiciousEffectsLastLonger effectsLastLonger;

    public SuspiciousEffectImprovements(Mod mod) {
        super(mod);
        INSTANCE = this;
        bigPlants = new SuspiciousBigPlants(this);
        effectsLastLonger = new SuspiciousEffectsLastLonger(this);
    }

    public static SuspiciousEffectImprovements feature() {
        return INSTANCE;
    }

    public int pitcherPlantEffectDuration() { return enabled() ? Mth.clamp(pitcherPlantEffectDuration, 0, 1000) * beneficialMultiplier() : 0; }
    public int sunflowerEffectDuration() { return enabled() ? Mth.clamp(sunflowerEffectDuration, 0, 1000) * beneficialMultiplier() : 0; }
    public int beneficialMultiplier() { return enabled() ? Mth.clamp(beneficialMultiplier, 1, 100) : 1; }
    public int detrimentalMultiplier() { return enabled() ? Mth.clamp(detrimentalMultiplier, 1, 100) : 1; }
}
