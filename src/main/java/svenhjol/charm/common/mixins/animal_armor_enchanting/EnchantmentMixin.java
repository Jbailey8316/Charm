package svenhjol.charm.common.mixins.animal_armor_enchanting;

import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.common.features.animal_armor_enchanting.AnimalArmorEnchanting;
import svenhjol.charm.common.features.animal_armor_enchanting.Tags;
import svenhjol.charmony.core.base.Mod;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Inject(method = "canEnchant", at = @At("RETURN"), cancellable = true)
    private void charm$allowAnimalArmor(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var feature = Mod.tryGetSidedFeature(AnimalArmorEnchanting.class).orElse(null);
        if (feature == null || !feature.enabled()) return;

        if (isAllowedAnimalArmor((Enchantment)(Object)this, stack)) cir.setReturnValue(true);
    }

    @Inject(method = "isSupportedItem", at = @At("RETURN"), cancellable = true)
    private void charm$allowAnimalArmorAsSupportedItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var feature = Mod.tryGetSidedFeature(AnimalArmorEnchanting.class).orElse(null);
        if (feature == null || !feature.enabled()) return;

        if (isAllowedAnimalArmor((Enchantment)(Object)this, stack)) cir.setReturnValue(true);
    }

    @Inject(method = "isPrimaryItem", at = @At("RETURN"), cancellable = true)
    private void charm$allowAnimalArmorAsPrimaryItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var feature = Mod.tryGetSidedFeature(AnimalArmorEnchanting.class).orElse(null);
        if (feature == null || !feature.enabled()) return;

        if (isAllowedAnimalArmor((Enchantment)(Object)this, stack)) cir.setReturnValue(true);
    }

    private static boolean isAllowedAnimalArmor(Enchantment enchantment, ItemStack stack) {
        var contents = enchantment.description().getContents();
        var key = contents instanceof TranslatableContents translated ? translated.getKey() : "";
        return (stack.is(Tags.HORSE_ARMOR) || stack.is(Tags.WOLF_ARMOR)) && isAllowed(key);
    }

    private static boolean isAllowed(String key) {
        return key.equals("enchantment.minecraft.protection")
            || key.equals("enchantment.minecraft.fire_protection")
            || key.equals("enchantment.minecraft.blast_protection")
            || key.equals("enchantment.minecraft.projectile_protection")
            || key.equals("enchantment.minecraft.thorns")
            || key.equals("enchantment.minecraft.frost_walker")
            || key.equals("enchantment.minecraft.feather_falling")
            || key.equals("enchantment.minecraft.respiration")
            || key.equals("enchantment.minecraft.soul_speed");
    }
}
