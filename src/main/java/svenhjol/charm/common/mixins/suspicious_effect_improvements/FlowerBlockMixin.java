package svenhjol.charm.common.mixins.suspicious_effect_improvements;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.FlowerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charm.common.features.suspicious_effect_improvements.SuspiciousEffectImprovements;

@Mixin(FlowerBlock.class)
public abstract class FlowerBlockMixin {
    @ModifyReturnValue(method = "getSuspiciousEffects", at = @At("RETURN"))
    private SuspiciousStewEffects charm$extendEffects(SuspiciousStewEffects original) {
        var feature = SuspiciousEffectImprovements.feature();
        return feature == null ? original : new svenhjol.charm.common.features.suspicious_effect_improvements.suspicious_effects_last_longer.SuspiciousEffectsHandler(feature.effectsLastLonger).modify(original);
    }
}
