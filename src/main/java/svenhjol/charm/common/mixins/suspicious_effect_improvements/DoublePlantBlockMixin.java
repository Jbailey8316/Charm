package svenhjol.charm.common.mixins.suspicious_effect_improvements;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charm.common.features.suspicious_effect_improvements.SuspiciousEffectImprovements;

import java.util.List;

@Mixin(DoublePlantBlock.class)
public class DoublePlantBlockMixin implements SuspiciousEffectHolder {
    @Override
    public SuspiciousStewEffects getSuspiciousEffects() {
        var feature = SuspiciousEffectImprovements.feature();
        if (feature != null && (Object)this == Blocks.PITCHER_PLANT) {
            var duration = feature.bigPlants.pitcherPlantEffectDuration();
            if (duration > 0) {
                return new SuspiciousStewEffects(List.of(
                    new SuspiciousStewEffects.Entry(MobEffects.STRENGTH, duration),
                    new SuspiciousStewEffects.Entry(MobEffects.REGENERATION, duration)));
            }
        }
        return SuspiciousStewEffects.EMPTY;
    }
}
