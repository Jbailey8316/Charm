package svenhjol.charm.common.mixins.suspicious_effect_improvements;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.block.TallFlowerBlock;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charm.common.features.suspicious_effect_improvements.SuspiciousEffectImprovements;

import java.util.List;

@Mixin(TallFlowerBlock.class)
public class TallFlowerBlockMixin implements SuspiciousEffectHolder {
    @Override
    public SuspiciousStewEffects getSuspiciousEffects() {
        var feature = SuspiciousEffectImprovements.feature();
        if (feature != null && (Object)this == Blocks.SUNFLOWER) {
            var duration = feature.bigPlants.sunflowerEffectDuration();
            if (duration > 0) {
                return new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(MobEffects.HEALTH_BOOST, duration)));
            }
        }
        return SuspiciousStewEffects.EMPTY;
    }
}
