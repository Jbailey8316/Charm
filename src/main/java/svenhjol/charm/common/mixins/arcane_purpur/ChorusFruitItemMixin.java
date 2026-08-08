package svenhjol.charm.common.mixins.arcane_purpur;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.common.features.arcane_purpur.ArcanePurpur;
import svenhjol.charmony.core.base.Mod;

@Mixin(Item.class)
public final class ChorusFruitItemMixin {
    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void charm$finishUsing(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (!stack.is(Items.CHORUS_FRUIT)) return;
        var feature = Mod.tryGetSidedFeature(ArcanePurpur.class);
        if (feature.isPresent() && feature.get().enabled() && feature.get().handlers.tryChorusTeleport(entity, stack)) {
            cir.setReturnValue(stack);
        }
    }
}
