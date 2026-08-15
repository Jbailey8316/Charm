package svenhjol.charm.common.features.suspicious_effect_improvements.suspicious_effects_last_longer;

import svenhjol.charm.common.features.suspicious_effect_improvements.SuspiciousEffectImprovements;

public final class SuspiciousEffectsLastLonger {
    private final SuspiciousEffectImprovements parent;

    public SuspiciousEffectsLastLonger(SuspiciousEffectImprovements parent) {
        this.parent = parent;
    }

    public int beneficialMultiplier() {
        return parent.beneficialMultiplier();
    }

    public int detrimentalMultiplier() {
        return parent.detrimentalMultiplier();
    }
}
