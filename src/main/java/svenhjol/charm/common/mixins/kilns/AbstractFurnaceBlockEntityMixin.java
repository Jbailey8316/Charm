package svenhjol.charm.common.mixins.kilns;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import svenhjol.charm.common.features.kilns.Kilns;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @ModifyArg(
        method = "method_17761",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;createExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;IF)V"),
        index = 3
    )
    private static float charm$halveKilnExperience(float experience, @Local(argsOnly = true) RecipeHolder<?> recipe) {
        return recipe.value() instanceof svenhjol.charm.common.features.firing.FiringRecipe ? experience / 2.0f : experience;
    }

    @ModifyExpressionValue(
        method = "serverTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;getBurnDuration(Lnet/minecraft/world/level/block/entity/FuelValues;Lnet/minecraft/world/item/ItemStack;)I")
    )
    private static int charm$halveKilnFuel(int duration, @Local AbstractFurnaceBlockEntity furnace) {
        return furnace instanceof Kilns.KilnBlockEntity ? Math.max(1, duration / 2) : duration;
    }
}
