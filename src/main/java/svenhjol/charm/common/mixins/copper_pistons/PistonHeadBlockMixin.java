package svenhjol.charm.common.mixins.copper_pistons;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.common.features.copper_pistons.CopperPistons;
import svenhjol.charmony.core.base.Mod;

@Mixin(PistonHeadBlock.class)
public abstract class PistonHeadBlockMixin {
    @Redirect(
        method = "isFittingBase",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean charm$recognizeCopperBase(BlockState state, Block block) {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        if (feature != null && feature.enabled()) {
            if (block == Blocks.PISTON && state.is(feature.registers.copperPistonBlock.get())) return true;
            if (block == Blocks.STICKY_PISTON && state.is(feature.registers.stickyCopperPistonBlock.get())) return true;
        }
        return state.is(block);
    }

    @Redirect(
        method = "canSurvive",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean charm$recognizeCopperMovingPiston(BlockState state, Block block) {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        if (feature != null && feature.enabled() && block == Blocks.MOVING_PISTON
            && state.is(feature.registers.movingCopperPistonBlock.get())) return true;
        return state.is(block);
    }

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
