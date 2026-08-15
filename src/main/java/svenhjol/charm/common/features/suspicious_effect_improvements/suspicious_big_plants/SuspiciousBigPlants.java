package svenhjol.charm.common.features.suspicious_effect_improvements.suspicious_big_plants;

import svenhjol.charm.common.features.suspicious_effect_improvements.SuspiciousEffectImprovements;

public final class SuspiciousBigPlants {
    private final SuspiciousEffectImprovements parent;

    public SuspiciousBigPlants(SuspiciousEffectImprovements parent) {
        this.parent = parent;
    }

    public int pitcherPlantEffectDuration() {
        return parent.pitcherPlantEffectDuration();
    }

    public int sunflowerEffectDuration() {
        return parent.sunflowerEffectDuration();
    }
}
