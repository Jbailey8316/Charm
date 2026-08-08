package svenhjol.charm.common.mixins.animal_armor_enchanting;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.common.features.animal_armor_enchanting.AnimalArmorEnchanting;
import svenhjol.charmony.core.base.Mod;

@Mixin(LivingEntity.class)
public abstract class MobMixin {
    @org.spongepowered.asm.mixin.Shadow
    @Final
    protected EntityEquipment equipment;

    @Inject(method = "getItemBySlot", at = @At("RETURN"), cancellable = true)
    private void charm$exposeBodyArmor(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        var feature = Mod.tryGetSidedFeature(AnimalArmorEnchanting.class).orElse(null);
        if (feature == null || !feature.enabled() || slot != EquipmentSlot.BODY) return;

        var entity = (LivingEntity)(Object)this;
        if (entity instanceof Horse horse) {
            cir.setReturnValue(equipment.get(EquipmentSlot.BODY));
        } else if (entity instanceof Wolf wolf) {
            cir.setReturnValue(equipment.get(EquipmentSlot.BODY));
        }
    }
}
