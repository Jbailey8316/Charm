package svenhjol.charm.common.features.suspicious_effect_improvements.suspicious_effects_last_longer;

import net.minecraft.world.item.component.SuspiciousStewEffects;

import java.util.ArrayList;

public final class SuspiciousEffectsHandler {
    private final SuspiciousEffectsLastLonger feature;

    public SuspiciousEffectsHandler(SuspiciousEffectsLastLonger feature) {
        this.feature = feature;
    }

    public SuspiciousStewEffects modify(SuspiciousStewEffects effects) {
        var entries = new ArrayList<SuspiciousStewEffects.Entry>();
        for (var entry : effects.effects()) {
            var effect = entry.effect().value();
            if (effect.isInstantenous()) {
                entries.add(entry);
            } else {
                var multiplier = effect.isBeneficial() ? feature.beneficialMultiplier() : feature.detrimentalMultiplier();
                entries.add(new SuspiciousStewEffects.Entry(entry.effect(), entry.duration() * multiplier));
            }
        }
        return new SuspiciousStewEffects(entries);
    }
}
