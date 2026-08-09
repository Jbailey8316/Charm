package svenhjol.charm.common.mixins.copper_pistons;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import svenhjol.charm.common.features.copper_pistons.CopperPistons;
import svenhjol.charmony.core.base.Mod;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {
    @ModifyArg(
        method = "moveBlocks",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;"),
        index = 2)
    private BlockState charm$useCopperHead(BlockState original) {
        if (!isCopper() || !original.is(Blocks.PISTON_HEAD)) return original;
        return Mod.tryGetSidedFeature(CopperPistons.class)
            .map(f -> f.registers.copperPistonHeadBlock.get().withPropertiesOf(original))
            .orElse(original);
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
        method = "moveBlocks",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean charm$recognizeCopperPistonStates(BlockState state, Block block) {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        if (feature != null && feature.enabled()) {
            if (block == Blocks.PISTON_HEAD && state.is(feature.registers.copperPistonHeadBlock.get())) return true;
            if (block == Blocks.MOVING_PISTON && state.is(feature.registers.movingCopperPistonBlock.get())) return true;
        }
        return state.is(block);
    }

    @ModifyReceiver(method = "triggerEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"))
    private Block charm$useCopperMovingPiston(Block original) {
        return isCopper() ? Mod.tryGetSidedFeature(CopperPistons.class).map(f -> f.registers.movingCopperPistonBlock.get()).orElse(original) : original;
    }

    @ModifyReturnValue(method = "getNeighborSignal", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;")), at = @At("RETURN"))
    private boolean charm$disableQuasiConnectivity(boolean original) {
        return isCopper() ? false : original;
    }

    @Unique
    private boolean isCopper() {
        var feature = Mod.tryGetSidedFeature(CopperPistons.class).orElse(null);
        if (feature == null || !feature.enabled()) return false;
        var state = ((PistonBaseBlock) (Object) this).defaultBlockState();
        return state.is(feature.registers.copperPistonBlock.get()) || state.is(feature.registers.stickyCopperPistonBlock.get());
    }
}
