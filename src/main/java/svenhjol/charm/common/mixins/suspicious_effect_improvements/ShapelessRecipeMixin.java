package svenhjol.charm.common.mixins.suspicious_effect_improvements;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charm.common.features.suspicious_effect_improvements.SuspiciousEffectImprovements;

@Mixin(ShapelessRecipe.class)
public class ShapelessRecipeMixin {
    @ModifyReturnValue(method = "assemble", at = @At("RETURN"))
    private ItemStack charm$applyFlowerEffects(ItemStack result, CraftingInput input) {
        if (result.is(net.minecraft.world.item.Items.SUSPICIOUS_STEW)) {
            var feature = SuspiciousEffectImprovements.feature();
            if (feature != null && feature.enabled()) {
                for (var stack : input.items()) {
                    if (Block.byItem(stack.getItem()) instanceof SuspiciousEffectHolder holder) {
                        var effects = new svenhjol.charm.common.features.suspicious_effect_improvements.suspicious_effects_last_longer.SuspiciousEffectsHandler(feature.effectsLastLonger).modify(holder.getSuspiciousEffects());
                        if (!effects.effects().isEmpty()) {
                            result.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, effects);
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
}
