package svenhjol.charm.common.mixins.kilns;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charm.common.features.kilns.Kilns;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @ModifyExpressionValue(
        method = "serverTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;getBurnDuration(Lnet/minecraft/world/level/block/entity/FuelValues;Lnet/minecraft/world/item/ItemStack;)I")
    )
    private static int charm$halveKilnFuel(int duration, @Local AbstractFurnaceBlockEntity furnace) {
        return furnace instanceof Kilns.KilnBlockEntity ? Math.max(1, duration / 2) : duration;
    }
}
