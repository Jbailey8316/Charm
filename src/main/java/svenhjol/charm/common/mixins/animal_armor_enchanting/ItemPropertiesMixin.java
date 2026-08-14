package svenhjol.charm.common.mixins.animal_armor_enchanting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Horse and wolf armor use dedicated item-property helpers which do not add
 * the modern enchantable component that the enchanting table requires.
 */
@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin {
    @Inject(method = "horseArmor", at = @At("RETURN"))
    private void charm$makeHorseArmorEnchantable(ArmorMaterial material,
                                                  CallbackInfoReturnable<Item.Properties> cir) {
        cir.getReturnValue().enchantable(material.enchantmentValue());
    }

    @Inject(method = "wolfArmor", at = @At("RETURN"))
    private void charm$makeWolfArmorEnchantable(ArmorMaterial material,
                                                 CallbackInfoReturnable<Item.Properties> cir) {
        cir.getReturnValue().enchantable(material.enchantmentValue());
    }
}
