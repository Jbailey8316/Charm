package svenhjol.charm.common.mixins.copper_pistons;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charm.common.features.copper_pistons.CopperPistons;
import svenhjol.charmony.core.base.Mod;

@Mixin(PistonHeadBlock.class)
public abstract class PistonHeadBlockMixin {
    @ModifyReturnValue(method = "getCloneItemStack", at = @At("RETURN"))
    private ItemStack charm$useCopperItem(ItemStack original, @Local(argsOnly = true) BlockState state) {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        if (feature == null || !feature.enabled()) return original;
        var head = ((PistonHeadBlock) (Object) this).defaultBlockState().getBlock();
        if (!head.equals(feature.registers.copperPistonHeadBlock.get())) return original;
        return new ItemStack(state.getValue(PistonHeadBlock.TYPE) == PistonType.STICKY
            ? feature.registers.stickyCopperPistonItem.get()
            : feature.registers.copperPistonItem.get());
    }
}
